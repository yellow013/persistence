package io.mercury.persistence.chronicle.queue.accessor;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.queue.ExcerptAppender;

@NotThreadSafe
public final class StringWriter extends AbstractDataWriter<String> {

	private StringWriter(String name, ExcerptAppender appender) {
		super(name, appender);
	}

	public static StringWriter wrap(String name, ExcerptAppender appender) {
		return new StringWriter(name, appender);
	}

	@Override
	protected void append0(String t) {
		appender.writeText(t);
	}

}