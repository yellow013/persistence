package io.mercury.persistence.chronicle.queue;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import io.mercury.common.annotation.lang.MayThrowsRuntimeException;
import io.mercury.common.annotation.lang.ProtectedAbstractMethod;
import io.mercury.persistence.chronicle.exception.ChronicleWriteException;
import net.openhft.chronicle.queue.ExcerptAppender;

public abstract class AbstractChronicleAppender<T> {

	private final String appenderName;

	protected final Logger logger;
	protected final ExcerptAppender excerptAppender;

	AbstractChronicleAppender(String appenderName, Logger logger, ExcerptAppender excerptAppender) {
		this.appenderName = appenderName;
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

	public String appenderName() {
		return appenderName;
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
			if (t != null)
				append0(t);
			else
				logger.warn("appenderName -> {} received null object, Not written to queue.");
		} catch (Exception e) {
			throw new ChronicleWriteException(e.getMessage(), e);
		}
	}

	@ProtectedAbstractMethod
	protected abstract void append0(T t);

}
