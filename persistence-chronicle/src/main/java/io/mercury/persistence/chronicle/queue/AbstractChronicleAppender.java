package io.mercury.persistence.chronicle.queue;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import io.mercury.common.annotation.lang.MayThrowsRuntimeException;
import io.mercury.common.annotation.lang.ProtectedAbstractMethod;
import io.mercury.persistence.chronicle.exception.ChronicleWriteException;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.ExcerptAppender;

public abstract class AbstractChronicleAppender<T> implements Runnable, Closeable {

	private final String appenderName;

	protected final Logger logger;
	protected final ExcerptAppender excerptAppender;

	private volatile boolean isClose = false;

	private Supplier<T> supplier;

	AbstractChronicleAppender(String appenderName, Logger logger, ExcerptAppender excerptAppender,
			Supplier<T> supplier) {
		this.appenderName = appenderName;
		this.logger = logger;
		this.excerptAppender = excerptAppender;
		this.supplier = supplier;
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

	@ProtectedAbstractMethod
	protected abstract void append0(T t);

	/**
	 * append element to queue tail.
	 * 
	 * @param t
	 * @throws ChronicleWriteException
	 */
	@MayThrowsRuntimeException(ChronicleWriteException.class)
	public void append(@Nonnull T t) throws ChronicleWriteException {
		try {
			if (t != null) {
				if (!isClose)
					append0(t);
				else
					logger.warn("appenderName -> {} the swap queue is closed.");
			} else
				logger.warn("appenderName -> {} received null object, Not written to queue.");
		} catch (Exception e) {
			throw new ChronicleWriteException(e.getMessage(), e);
		}
	}

	@Override
	public void run() {
		if (supplier != null) {
			for (;;) {
				T t = supplier.get();
				append(t);
			}
		} else {
			logger.warn("supplier is null, thread exit.");
			return;
		}
	}

	public void close() {
		isClose = true;
	}

}
