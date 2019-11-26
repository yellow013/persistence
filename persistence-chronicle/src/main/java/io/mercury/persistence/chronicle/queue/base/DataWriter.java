package io.mercury.persistence.chronicle.queue.base;

import io.ffreedom.common.annotations.lang.MayThrowsRuntimeException;
import net.openhft.chronicle.queue.ExcerptAppender;

public abstract class DataWriter<T> {

	protected ExcerptAppender appender;

	private String writerName;

	protected DataWriter(ExcerptAppender appender) {
		this.appender = appender;
	}

	public ExcerptAppender getAppender() {
		return appender;
	}

	public int cycle() {
		return appender.cycle();
	}

	public int sourceId() {
		return appender.sourceId();
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
	@MayThrowsRuntimeException
	public void append(T t) throws ChronicleWriteException {
		try {
			append0(t);
		} catch (Exception e) {
			throw new ChronicleWriteException(e.getMessage(), e);
		}
	}

	protected abstract void append0(T t);

}
