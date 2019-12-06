package io.mercury.persistence.chronicle.queue.base;

import static io.mercury.common.utils.StringUtil.fixPath;

import java.io.File;
import java.util.function.ObjIntConsumer;

import org.slf4j.Logger;

import io.mercury.common.log.CommonLoggerFactory;
import io.mercury.common.sys.SysProperties;
import io.mercury.common.thread.ShutdownHooks;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

public abstract class BaseChronicleQueue<T, R extends DataReader<T>, W extends DataWriter<T>> {

	private final String rootPath;
	private final String folder;
	private final FileCycle fileCycle;
	private final ObjIntConsumer<File> storeFileListener;

	private final File savePath;
	private final String name;

	private SingleChronicleQueue internalQueue;

	protected Logger logger;

	protected BaseChronicleQueue(BaseBuilder<?> builder) {
		this.rootPath = builder.rootPath;
		this.folder = builder.folder;
		this.fileCycle = builder.fileCycle;
		this.storeFileListener = builder.storeFileListener;
		this.logger = builder.logger;
		this.savePath = new File(rootPath + "chronicle-queue/" + folder);
		this.name = folder;
		initChronicleQueue();
	}

	private void initChronicleQueue() {
		if (!savePath.exists())
			savePath.mkdirs();
		this.internalQueue = SingleChronicleQueueBuilder.single(savePath).rollCycle(fileCycle.getRollCycle())
				.storeFileListener(this::storeFileHandle).build();
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

	@Deprecated
	public boolean deleteFolder() {
		if (savePath.isAbsolute())
			return savePath.delete();
		return false;
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
		private Logger logger = CommonLoggerFactory.getLogger(BaseChronicleQueue.class);
		private FileCycle fileCycle = FileCycle.SMALL_DAILY;
		private ObjIntConsumer<File> storeFileListener;

		public B rootPath(String rootPath) {
			this.rootPath = fixPath(rootPath);
			return getThis();
		}

		public B folder(String folder) {
			this.folder = fixPath(folder);
			return getThis();
		}

		public B logger(Logger logger) {
			this.logger = logger;
			return getThis();
		}

		public B fileCycle(FileCycle fileCycle) {
			this.fileCycle = fileCycle;
			return getThis();
		}

		public B storeFileListener(ObjIntConsumer<File> storeFileListener) {
			this.storeFileListener = storeFileListener;
			return getThis();
		}

		protected abstract B getThis();

	}

}
