package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

import io.mercury.common.number.RandomNumber;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.chronicle.queue.accessor.BytesReader;
import io.mercury.persistence.chronicle.queue.accessor.BytesWriter;

@Immutable
public class ChronicleBytesQueue extends AbstractChronicleQueue<ByteBuffer, BytesReader, BytesWriter> {

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
	public BytesReader createReader(String readerName) {
		return BytesReader.wrap(readerName, readBufferSize, useDirectMemory, internalQueue().createTailer(),
				fileCycle());
	}

	@Override
	public BytesWriter acquireWriter(String writerName) {
		return BytesWriter.wrap(writerName, internalQueue().acquireAppender());
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
		BytesWriter writer = queue.acquireWriter();
		BytesReader reader = queue.createReader();
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
		do {
			try {
				ByteBuffer next = reader.next();
				if (next == null)
					ThreadUtil.sleep(100);
				else
					System.out.println(new String(next.array()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (true);

	}

}
