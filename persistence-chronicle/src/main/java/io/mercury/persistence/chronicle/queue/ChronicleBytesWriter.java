package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.queue.ExcerptAppender;

@NotThreadSafe
public final class ChronicleBytesWriter extends AbstractChronicleWriter<ByteBuffer> {

	private ChronicleBytesWriter(String name, ExcerptAppender appender) {
		super(name, appender);
	}

	static ChronicleBytesWriter wrap(String name, ExcerptAppender appender) {
		return new ChronicleBytesWriter(name, appender);
	}

	@Override
	protected void append0(ByteBuffer t) {
		// use heap memory or direct by the byteBuffer
		internalAppender.writeBytes(BytesStore.wrap(t));
	}

}
