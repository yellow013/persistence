package io.mercury.persistence.chronicle.queue;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.persistence.chronicle.exception.ChronicleWriteException;
import net.openhft.chronicle.queue.ExcerptAppender;

public abstract class AbstractChronicleAppender<T> {

	private final String writerName;

	protected final Logger logger;
	protected final ExcerptAppender excerptAppender;

	AbstractChronicleAppender(String writerName, Logger logger, ExcerptAppender excerptAppender) {
		this.writerName = writerName;
		this.logger = logger;
		this.excerptAppender = excerptAppender;
	}

	public ExcerptAppender excerptAppender() {
		return excerptAppender;
	}

	public int cycle() {
		return excerptAppender.cycle();
	}

	public int sourceId() {
		return excerptAppender.sourceId();
	}

	public String writerName() {
		return writerName;
	}

	/**
	 * append element to queue tail.
	 * 
	 * @param t
	 * @throws ChronicleWriteException
	 */
	@MayThrowsRuntimeException(ChronicleWriteException.class)
	public void append(@Nonnull T t) throws ChronicleWriteException {
		try {
			append0(t);
		} catch (Exception e) {
			throw new ChronicleWriteException(e.getMessage(), e);
		}
	}

	protected abstract void append0(T t);

}
