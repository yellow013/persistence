package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;

import io.mercury.persistence.chronicle.queue.accessor.BytesReader;
import io.mercury.persistence.chronicle.queue.accessor.BytesWriter;
import io.mercury.persistence.chronicle.queue.base.BaseChronicleQueue;

public class ChronicleBytesQueue extends BaseChronicleQueue<ByteBuffer, BytesReader, BytesWriter> {

	private ChronicleBytesQueue(Builder builder) {
		super(builder);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public BytesReader createReader(String readerName) {
		return BytesReader.wrap(readerName, internalQueue().createTailer(), fileCycle());
	}

	@Override
	public BytesWriter acquireWriter(String writerName) {
		return BytesWriter.wrap(writerName, internalQueue().acquireAppender());
	}

	public static class Builder extends BaseBuilder<Builder> {

		public ChronicleBytesQueue build() {
			return new ChronicleBytesQueue(this);
		}

		@Override
		protected Builder getThis() {
			return this;
		}

	}

}
