package io.mercury.persistence.chronicle.queue;

import static io.mercury.common.util.StringUtil.fixPath;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.slf4j.Logger;

import io.mercury.common.annotation.lang.MayThrowsRuntimeException;
import io.mercury.common.annotation.lang.ProtectedAbstractMethod;
import io.mercury.common.collections.MutableMaps;
import io.mercury.common.datetime.DateTimeUtil;
import io.mercury.common.datetime.EpochTime;
import io.mercury.common.log.CommonLoggerFactory;
import io.mercury.common.number.RandomNumber;
import io.mercury.common.sys.SysProperties;
import io.mercury.common.thread.ShutdownHooks;
import io.mercury.common.util.Assertor;
import io.mercury.persistence.chronicle.queue.AbstractChronicleReader.ReaderParam;
import net.openhft.chronicle.queue.impl.TableStore;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.queue.impl.table.Metadata;

public abstract class AbstractChronicleQueue<T, R extends AbstractChronicleReader<T>, W extends AbstractChronicleAppender<T>> {

	private final String rootPath;
	private final String folder;
	private final boolean readOnly;
	private final long epoch;
	private final FileCycle fileCycle;
	private final long fileCleanCycle;
	private final ObjIntConsumer<File> storeFileListener;

	private final File savePath;
	private final String queueName;

	private final SingleChronicleQueue internalQueue;

	protected Logger logger = CommonLoggerFactory.getLogger(getClass());;

	AbstractChronicleQueue(BaseBuilder<?> builder) {
		this.rootPath = builder.rootPath;
		this.folder = builder.folder;
		this.readOnly = builder.readOnly;
		this.epoch = builder.epoch;
		this.fileCycle = builder.fileCycle;
		this.fileCleanCycle = builder.fileCleanCycle;
		this.storeFileListener = builder.storeFileListener;
		this.logger = builder.logger != null ? builder.logger : logger;
		this.savePath = new File(rootPath + "chronicle-queue/" + folder);
		this.queueName = folder;
		this.internalQueue = buildChronicleQueue();
		logger.info("ChronicleDataQueue initialized -> name==[{}], desc==[{}]", queueName, fileCycle.getDesc());
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
		logger.info("ChronicleQueue ShutdownHook of {} start", queueName);
		internalQueue.close();
		// System.out.println("ChronicleQueue ShutdownHook of " + name + " finished");
		logger.info("ChronicleQueue ShutdownHook of {} finished", queueName);
	}

	private MutableIntObjectMap<String> storeFileMap = MutableMaps.newIntObjectHashMap();

	private AtomicInteger lastCycle = new AtomicInteger();
	

	private void storeFileHandle(int cycle, File file) {
		logger.info("Released file : cycle==[{}], file==[{}]", cycle, file.getAbsolutePath());
		if (storeFileListener != null) {
			storeFileListener.accept(file, cycle);
		}
		storeFileMap.put(cycle, file.getAbsolutePath());
		System.out.println(cycle + " ============ " + EpochTime.hour());
		lastCycle.set(cycle);
		
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

	public R createReader(Consumer<T> consumer) {
		return createReader(queueName + "-Reader-" + RandomNumber.randomUnsignedInt(), ReaderParam.Default(), logger,
				consumer);
	}

	public R createReader(String readerName, Consumer<T> consumer) {
		return createReader(readerName, ReaderParam.Default(), logger, consumer);
	}

	public R createReader(ReaderParam readerParam, Consumer<T> consumer) {
		return createReader(queueName + "-Reader-" + RandomNumber.randomUnsignedInt(), readerParam, logger, consumer);
	}

	public R createReader(String readerName, ReaderParam readerParam, Consumer<T> consumer) {
		return createReader(readerName, readerParam, logger, consumer);
	}

	@ProtectedAbstractMethod
	protected abstract R createReader(String readerName, ReaderParam readerParam, Logger logger, Consumer<T> consumer);

	@MayThrowsRuntimeException(IllegalStateException.class)
	public W acquireAppender() {
		return acquireAppender(queueName + "-Appender-" + RandomNumber.randomUnsignedInt(), logger);
	}

	public W acquireAppender(String writerName) {
		return acquireAppender(writerName, logger);
	}

	@ProtectedAbstractMethod
	protected abstract W acquireAppender(String writerName, Logger logger);

	protected abstract static class BaseBuilder<B extends BaseBuilder<B>> {

		private String rootPath = SysProperties.JAVA_IO_TMPDIR + "/";
		private String folder = "auto-create-" + DateTimeUtil.datetimeOfSecond() + "/";
		private boolean readOnly = false;
		private long epoch = 0L;
		private FileCycle fileCycle = FileCycle.SMALL_DAILY;
		private ObjIntConsumer<File> storeFileListener;
		private long fileCleanCycle = 0L;

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
			this.fileCycle = Assertor.nonNull(fileCycle, "fileCycle");
			return self();
		}

		public B fileCleanCycle(long fileCleanCycle) {
			this.fileCleanCycle = fileCleanCycle;
			return self();
		}

		public B storeFileListener(ObjIntConsumer<File>... storeFileListeners) {
			this.storeFileListener = Assertor.nonNull(storeFileListener, "storeFileListener");
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
