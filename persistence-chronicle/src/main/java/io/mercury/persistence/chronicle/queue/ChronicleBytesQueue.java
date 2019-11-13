package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;

import io.mercury.persistence.chronicle.queue.accessor.BytesReader;
import io.mercury.persistence.chronicle.queue.accessor.BytesWriter;
import io.mercury.persistence.chronicle.queue.base.ChronicleDataQueue;

public class ChronicleBytesQueue extends ChronicleDataQueue<ByteBuffer, BytesReader, BytesWriter> {

	private ChronicleBytesQueue(Builder builder) {
		super(builder);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public BytesReader createReader() {
		return BytesReader.wrap(getQueue().createTailer(), getFileCycle());
	}

	@Override
	public BytesWriter acquireWriter() {
		return BytesWriter.wrap(getQueue().acquireAppender());
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
