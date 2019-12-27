package io.mercury.persistence.chronicle.queue;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;

import net.openhft.chronicle.queue.ExcerptAppender;

@NotThreadSafe
public final class ChronicleStringAppender extends AbstractChronicleAppender<String> {

	ChronicleStringAppender(String writerName, Logger logger, ExcerptAppender excerptAppender) {
		super(writerName, logger, excerptAppender);
	}

	@Override
	protected void append0(String t) {
		excerptAppender.writeText(t);
	}

}