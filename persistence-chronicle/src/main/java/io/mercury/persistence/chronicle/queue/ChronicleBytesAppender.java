package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.queue.ExcerptAppender;

@NotThreadSafe
public final class ChronicleBytesAppender extends AbstractChronicleAppender<ByteBuffer> {

	ChronicleBytesAppender(String writerName, Logger logger, ExcerptAppender excerptAppender) {
		super(writerName, logger, excerptAppender);
	}

	@Override
	protected void append0(ByteBuffer t) {
		// use heap memory or direct by the byteBuffer
		excerptAppender.writeBytes(BytesStore.wrap(t));
	}

}
