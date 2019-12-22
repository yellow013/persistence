package io.mercury.persistence.chronicle.queue.accessor;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.queue.ExcerptAppender;

@NotThreadSafe
public final class BytesWriter extends AbstractDataWriter<ByteBuffer> {

	private BytesWriter(String name, ExcerptAppender appender) {
		super(name, appender);
	}

	public static BytesWriter wrap(String name, ExcerptAppender appender) {
		return new BytesWriter(name, appender);
	}

	@Override
	protected void append0(ByteBuffer t) {
		// use heap memory or direct determined by byteBuffer
		appender.writeBytes(BytesStore.wrap(t));
	}

}
