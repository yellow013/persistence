package io.mercury.persistence.chronicle.queue;

import javax.annotation.Nonnull;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.persistence.chronicle.exception.ChronicleWriteException;
import net.openhft.chronicle.queue.ExcerptAppender;

abstract class AbstractChronicleWriter<T> {

	protected final ExcerptAppender internalAppender;

	private final String name;

	protected AbstractChronicleWriter(String name, ExcerptAppender appender) {
		this.name = name;
		this.internalAppender = appender;
	}

	public ExcerptAppender internalAppender() {
		return internalAppender;
	}

	public int cycle() {
		return internalAppender.cycle();
	}

	public int sourceId() {
		return internalAppender.sourceId();
	}

	public String name() {
		return name;
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
