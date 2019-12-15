package io.mercury.persistence.chronicle.queue;

import static io.mercury.common.utils.StringUtil.fixPath;

import java.io.File;
import java.util.function.ObjIntConsumer;

import org.slf4j.Logger;

import io.mercury.common.log.CommonLoggerFactory;
import io.mercury.common.sys.SysProperties;
import io.mercury.common.thread.ShutdownHooks;
import io.mercury.common.utils.Assertor;
import io.mercury.persistence.chronicle.queue.accessor.AbstractDataReader;
import io.mercury.persistence.chronicle.queue.accessor.AbstractDataWriter;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

public abstract class AbstractChronicleQueue<T, R extends AbstractDataReader<T>, W extends AbstractDataWriter<T>> {

	private final String rootPath;
	private final String folder;
	private final boolean readOnly;
	private final long epoch;
	private final FileCycle fileCycle;
	private final ObjIntConsumer<File> storeFileListener;

	private final File savePath;
	private final String name;

	private SingleChronicleQueue internalQueue;

	protected Logger logger;

	protected AbstractChronicleQueue(BaseBuilder<?> builder) {
		this.rootPath = builder.rootPath;
		this.folder = builder.folder;
		this.fileCycle = builder.fileCycle;
		this.readOnly = builder.readOnly;
		this.epoch = builder.epoch;
		this.storeFileListener = builder.storeFileListener;
		this.logger = builder.logger;
		this.savePath = new File(rootPath + "chronicle-queue/" + folder);
		this.name = folder;
		initChronicleQueue();
	}

	private void initChronicleQueue() {
		if (!savePath.exists())
			savePath.mkdirs();
		SingleChronicleQueueBuilder queueBuilder = SingleChronicleQueueBuilder.single(savePath)
				.rollCycle(fileCycle.getRollCycle()).readOnly(readOnly).storeFileListener(this::storeFileHandle);
		if (epoch > 0L)
			queueBuilder.epoch(epoch);
		this.internalQueue = queueBuilder.build();
		// TODO 解决CPU缓存行填充问题
		ShutdownHooks.addShutdownHookThread("ChronicleQueue-Cleanup", this::shutdownHandle);
		logger.info("ChronicleDataQueue initialized -> name==[{}], desc==[{}]", name, fileCycle.getDesc());
	}

	private void shutdownHandle() {
		internalQueue.close();
		logger.info("Run ShutdownHook of {}", name);
	}

	private void storeFileHandle(int cycle, File file) {
		if (storeFileListener != null)
			storeFileListener.accept(file, cycle);
		else
			logger.info("Released file : cycle==[{}], file==[{}]", cycle, file.getAbsolutePath());
	}

	public String name() {
		return name;
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

	public R createReader() {
		return createReader(name);
	}

	public abstract R createReader(String readerName);

	public W acquireWriter() {
		return acquireWriter(name);
	}

	public abstract W acquireWriter(String writerName);

	protected abstract static class BaseBuilder<B extends BaseBuilder<B>> {

		private String rootPath = SysProperties.JAVA_IO_TMPDIR + "/";
		private String folder = "default/";
		private boolean readOnly = false;
		private long epoch = 0L;
		private FileCycle fileCycle = FileCycle.SMALL_DAILY;
		private ObjIntConsumer<File> storeFileListener;
		private Logger logger = CommonLoggerFactory.getLogger(AbstractChronicleQueue.class);

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

		public B storeFileListener(ObjIntConsumer<File> storeFileListener) {
			this.storeFileListener = Assertor.nonNull(storeFileListener, "storeFileListener");
			return self();
		}

		public B logger(Logger logger) {
			this.logger = logger;
			return self();
		}

		protected abstract B self();

	}

}
