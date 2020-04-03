package io.mercury.persistence.chronicle.queue;

import static io.mercury.common.datetime.DateTimeUtil.datetimeOfSecond;
import static io.mercury.common.number.RandomNumber.randomUnsignedInt;
import static io.mercury.common.thread.ThreadUtil.sleep;
import static io.mercury.common.thread.ThreadUtil.startNewThread;
import static io.mercury.common.util.Assertor.nonNull;
import static io.mercury.common.util.StringUtil.fixPath;
import static io.mercury.persistence.chronicle.queue.AbstractChronicleReader.ReaderParam.defaultParam;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

import org.jctools.maps.NonBlockingHashMap;
import org.slf4j.Logger;

import io.mercury.common.annotation.lang.MayThrowsRuntimeException;
import io.mercury.common.annotation.lang.ProtectedAbstractMethod;
import io.mercury.common.log.CommonLoggerFactory;
import io.mercury.common.sys.SysProperties;
import io.mercury.common.thread.RuntimeInterruptedException;
import io.mercury.common.thread.ShutdownHooks;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.chronicle.queue.AbstractChronicleReader.ReaderParam;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

public abstract class AbstractChronicleQueue<T, R extends AbstractChronicleReader<T>, W extends AbstractChronicleAppender<T>>
		implements Closeable {

	private final String rootPath;
	private final String folder;
	private final boolean readOnly;
	private final long epoch;
	private final FileCycle fileCycle;
	private final int fileClearCycle;
	private final ObjIntConsumer<File> storeFileListener;

	private final File savePath;
	private final String queueName;

	protected final SingleChronicleQueue internalQueue;

	protected Logger logger = CommonLoggerFactory.getLogger(getClass());;

	AbstractChronicleQueue(QueueBuilder<?> builder) {
		this.rootPath = builder.rootPath;
		this.folder = builder.folder;
		this.readOnly = builder.readOnly;
		this.epoch = builder.epoch;
		this.fileCycle = builder.fileCycle;
		this.fileClearCycle = builder.fileClearCycle <= 0 ? 0 : builder.fileClearCycle < 3 ? 3 : builder.fileClearCycle;
		this.storeFileListener = builder.storeFileListener;
		this.logger = builder.logger != null ? builder.logger : logger;
		this.savePath = new File(rootPath + "chronicle-queue/" + folder);
		this.queueName = folder.replaceAll("/", "");
		this.internalQueue = buildChronicleQueue();
		buildClearThread();
		logger.info("{} initialized -> name==[{}], desc==[{}]", getClass().getSimpleName(), queueName,
				fileCycle.getDesc());
	}

	private SingleChronicleQueue buildChronicleQueue() {
		if (!savePath.exists())
			savePath.mkdirs();
		SingleChronicleQueueBuilder queueBuilder = SingleChronicleQueueBuilder.single(savePath)
				.rollCycle(fileCycle.getRollCycle()).readOnly(readOnly).storeFileListener(this::storeFileHandle);
		if (epoch > 0L)
			queueBuilder.epoch(epoch);
		// TODO 解决CPU缓存行填充问题
		ShutdownHooks.addShutdownHookThread("ChronicleQueue-Cleanup", this::shutdownHandle);
		return queueBuilder.build();
	}

	private void shutdownHandle() {
		// System.out.println("ChronicleQueue ShutdownHook of " + name + " start");
		logger.info("ChronicleQueue [{}] shutdown hook started", queueName);
		try {
			close();
		} catch (IOException e) {
			logger.error("ChronicleQueue [{}] shutdown hook throw exception: {}", queueName, e.getMessage(), e);
		}
		// System.out.println("ChronicleQueue ShutdownHook of " + name + " finished");
		logger.info("ChronicleQueue [{}] shutdown hook finished", queueName);
	}

	private AtomicInteger lastCycle;
	private ConcurrentMap<Integer, String> cycleFileMap;
	private Thread fileClearThread;
	private volatile boolean isClearRunning = true;

	private void buildClearThread() {
		if (fileClearCycle > 0) {
			this.lastCycle = new AtomicInteger();
			this.cycleFileMap = new NonBlockingHashMap<>();
			long delay = fileCycle.getSeconds() * fileClearCycle;
			this.fileClearThread = startNewThread(() -> {
				do {
					try {
						sleep(TimeUnit.SECONDS, delay);
					} catch (RuntimeInterruptedException e) {
						logger.info("Last execution fileClearTask");
						fileClearTask();
						logger.info("{} exit now", ThreadUtil.currentThreadName());
					}
					runFileClearTask();
				} while (isClearRunning);
			}, queueName + "-FileClear");
			// singleThreadScheduleWithFixedDelay(delay, delay, TimeUnit.SECONDS,
			// this::runFileClearTask);
			logger.info("Build clear thread is finished");
		}
	}

	private void runFileClearTask() {
		if (isClearRunning)
			fileClearTask();
	}

	private void fileClearTask() {
		int last = lastCycle.get();
		int delOffset = last - fileClearCycle;
		logger.info("Execute clear schedule : lastCycle==[{}], delOffset==[{}]", last, delOffset);
		Set<Integer> keySet = cycleFileMap.keySet();
		for (int saveCycle : keySet) {
			if (saveCycle < delOffset) {
				String fileAbsolutePath = cycleFileMap.get(saveCycle);
				logger.info("Delete cycle file : cycle==[{}], fileAbsolutePath==[{}]", saveCycle, fileAbsolutePath);
				File file = new File(fileAbsolutePath);
				if (file.exists()) {
					if (!file.delete()) {
						logger.warn("File delete failure !!!");
						cycleFileMap.remove(saveCycle);
					}
				} else {
					logger.error("File not exists, Please check the ChronicleQueue save path : [{}]",
							savePath.getAbsolutePath());
				}
			}
		}
	}

	private void storeFileHandle(int cycle, File file) {
		logger.info("Released file : cycle==[{}], file==[{}]", cycle, file.getAbsolutePath());
		if (storeFileListener != null) {
			storeFileListener.accept(file, cycle);
		}
		if (fileClearCycle > 0) {
			cycleFileMap.put(cycle, file.getAbsolutePath());
			lastCycle.set(cycle);
		}
	}

	public String queueName() {
		return queueName;
	}

	public String rootPath() {
		return rootPath;
	}

	public String folder() {
		return folder;
	}

	public File savePath() {
		return savePath;
	}

	public FileCycle fileCycle() {
		return fileCycle;
	}

	public SingleChronicleQueue internalQueue() {
		return internalQueue;
	}

	public boolean isClosed() {
		return internalQueue.isClosed();
	}

	@Override
	public void close() throws IOException {
		if (!isClosed())
			internalQueue.close();
		isClearRunning = false;
		if (fileClearThread != null)
			fileClearThread.interrupt();
		while (fileClearThread.getState() != State.TERMINATED)
			;
	}

	private static final String EMPTY_CONSUMER_MSG = "Reader consumer is an empty implementation";

	public R createReader() {
		return createReader(queueName + "-Reader-" + randomUnsignedInt(), defaultParam(),
				o -> logger.info(EMPTY_CONSUMER_MSG));
	}

	public R createReader(Consumer<T> consumer) {
		return createReader(queueName + "-Reader-" + randomUnsignedInt(), defaultParam(), consumer);
	}

	public R createReader(String readerName, Consumer<T> consumer) {
		return createReader(readerName, defaultParam(), consumer);
	}

	public R createReader(ReaderParam readerParam, Consumer<T> consumer) {
		return createReader(queueName + "-Reader-" + randomUnsignedInt(), readerParam, consumer);
	}

	public R createReader(String readerName, ReaderParam readerParam, Consumer<T> consumer) {
		return createReader(readerName, readerParam, logger, consumer);
	}

	@ProtectedAbstractMethod
	protected abstract R createReader(String readerName, ReaderParam readerParam, Logger logger, Consumer<T> consumer);

	@MayThrowsRuntimeException(IllegalStateException.class)
	public W acquireAppender() throws IllegalStateException {
		return acquireAppender(queueName + "-Appender-" + randomUnsignedInt(), null);
	}

	@MayThrowsRuntimeException(IllegalStateException.class)
	public W acquireAppender(String writerName) throws IllegalStateException {
		return acquireAppender(writerName, null);
	}

	@MayThrowsRuntimeException(IllegalStateException.class)
	public W acquireAppender(Supplier<T> supplier) throws IllegalStateException {
		return acquireAppender(queueName + "-Appender-" + randomUnsignedInt(), supplier);
	}

	@MayThrowsRuntimeException(IllegalStateException.class)
	public W acquireAppender(String writerName, Supplier<T> supplier) throws IllegalStateException {
		return acquireAppender(writerName, logger, supplier);
	}

	@ProtectedAbstractMethod
	protected abstract W acquireAppender(String writerName, Logger logger, Supplier<T> supplier);

	protected abstract static class QueueBuilder<B extends QueueBuilder<B>> {

		private String rootPath = SysProperties.JAVA_IO_TMPDIR + "/";
		private String folder = "auto-create-" + datetimeOfSecond() + "/";
		private boolean readOnly = false;
		private long epoch = 0L;
		private FileCycle fileCycle = FileCycle.SMALL_DAILY;
		private ObjIntConsumer<File> storeFileListener;
		private int fileClearCycle = 0;

		private Logger logger;

		public B rootPath(String rootPath) {
			this.rootPath = fixPath(rootPath);
			return self();
		}

		public B folder(String folder) {
			this.folder = fixPath(folder);
			return self();
		}

		public B readOnly(boolean readOnly) {
			this.readOnly = readOnly;
			return self();
		}

		public B epoch(long epoch) {
			this.epoch = epoch;
			return self();
		}

		public B fileCycle(FileCycle fileCycle) {
			this.fileCycle = nonNull(fileCycle, "fileCycle");
			return self();
		}

		public B fileClearCycle(int fileClearCycle) {
			this.fileClearCycle = fileClearCycle;
			return self();
		}

		public B storeFileListener(ObjIntConsumer<File> storeFileListener) {
			this.storeFileListener = nonNull(storeFileListener, "storeFileListener");
			return self();
		}

		public B logger(Logger logger) {
			this.logger = logger;
			return self();
		}

		@ProtectedAbstractMethod
		protected abstract B self();

	}

}
