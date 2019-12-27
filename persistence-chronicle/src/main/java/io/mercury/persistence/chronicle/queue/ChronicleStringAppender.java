package io.mercury.persistence.chronicle.queue;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.queue.ExcerptAppender;

@NotThreadSafe
public final class ChronicleStringWriter extends AbstractChronicleWriter<String> {

	private ChronicleStringWriter(String name, ExcerptAppender appender) {
		super(name, appender);
	}

	static ChronicleStringWriter wrap(String name, ExcerptAppender appender) {
		return new ChronicleStringWriter(name, appender);
	}

	@Override
	protected void append0(String t) {
		internalAppender.writeText(t);
	}

}
