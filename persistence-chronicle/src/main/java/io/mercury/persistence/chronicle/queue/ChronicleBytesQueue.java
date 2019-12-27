package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;

import io.mercury.common.number.RandomNumber;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.chronicle.queue.AbstractChronicleReader.ReadParam;

@Immutable
public class ChronicleBytesQueue
		extends AbstractChronicleQueue<ByteBuffer, ChronicleBytesReader, ChronicleBytesAppender> {

	private final int readBufferSize;
	private final boolean useDirectMemory;

	private ChronicleBytesQueue(Builder builder) {
		super(builder);
		this.readBufferSize = builder.readBufferSize;
		this.useDirectMemory = builder.useDirectMemory;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	protected ChronicleBytesReader buildReader(String readerName, ReadParam readParam, Logger logger,
			Consumer<ByteBuffer> consumer) {
		return new ChronicleBytesReader(readerName, fileCycle(), readParam, logger, readBufferSize, useDirectMemory,
				internalQueue().createTailer(), consumer);
	}

	@Override
	protected ChronicleBytesAppender acquireAppender(String writerName, Logger logger) {
		return new ChronicleBytesAppender(writerName, logger, internalQueue().acquireAppender());
	}

	public static class Builder extends BaseBuilder<Builder> {

		private int readBufferSize = 256;
		private boolean useDirectMemory = false;

		private Builder() {
		}

		public ChronicleBytesQueue build() {
			return new ChronicleBytesQueue(this);
		}

		/**
		 * if set size less than 256, use default size the 256
		 * 
		 * @param readBufferSize
		 * @return
		 */
		public Builder readBufferSize(int readBufferSize) {
			this.readBufferSize = Math.max(readBufferSize, 256);
			return this;
		}

		public Builder useDirectMemory(boolean useDirectMemory) {
			this.useDirectMemory = useDirectMemory;
			return this;
		}

		@Override
		protected Builder self() {
			return this;
		}

	}

	public static void main(String[] args) {
		ChronicleBytesQueue queue = ChronicleBytesQueue.newBuilder().folder("byte-test").readBufferSize(512)
				.fileCycle(FileCycle.MINUTELY).build();
		ChronicleBytesAppender writer = queue.acquireAppender();
		ChronicleBytesReader reader = queue.buildReader(next -> System.out.println(new String(next.array())));
		new Thread(() -> {
			ByteBuffer buffer = ByteBuffer.allocate(512);
			for (;;) {
				try {
					writer.append(buffer.put(String.valueOf(RandomNumber.randomLong()).getBytes()));
					buffer.clear();
					ThreadUtil.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		reader.runWithNewThread();
	}

}
