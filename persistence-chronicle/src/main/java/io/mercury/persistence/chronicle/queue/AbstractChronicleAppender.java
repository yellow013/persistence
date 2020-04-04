package io.mercury.persistence.chronicle.queue;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import io.mercury.common.annotation.lang.MayThrowsRuntimeException;
import io.mercury.common.annotation.lang.ProtectedAbstractMethod;
import io.mercury.persistence.chronicle.exception.ChronicleWriteException;
import io.mercury.persistence.chronicle.queue.AbstractChronicleQueue.CloseableChronicleAccessor;
import net.openhft.chronicle.queue.ExcerptAppender;

public abstract class AbstractChronicleAppender<T> extends CloseableChronicleAccessor implements Runnable {

	private final String appenderName;

	protected final Logger logger;
	protected final ExcerptAppender excerptAppender;

	private Supplier<T> supplier;

	AbstractChronicleAppender(long allocationNo, String appenderName, Logger logger, ExcerptAppender excerptAppender,
			Supplier<T> supplier) {
		super(allocationNo);
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
	protected abstract void append0(@Nonnull T t);

	/**
	 * append element to queue tail.
	 * 
	 * @param t
	 * @throws ChronicleWriteException
	 */
	@MayThrowsRuntimeException(ChronicleWriteException.class)
	public void append(@Nonnull T t) throws IllegalStateException, ChronicleWriteException {
		if (isClose) {
			throw new IllegalStateException("Unable to append data, Chronicle queue is closed");
		}
		try {
			if (t != null)
				append0(t);
			else
				logger.warn("appenderName -> {} received null object, Not written to queue.");
		} catch (Exception e) {
			throw new ChronicleWriteException(e.getMessage(), e);
		}
	}

	@Override
	public void run() {
		if (supplier != null) {
			for (;;) {
				if (isClose) {
					logger.info("Chronicle queue is closed, Thread exit");
					break;
				} else {
					T t = supplier.get();
					append(t);
				}
			}
		} else {
			logger.warn("Supplier is null, Thread exit");
		}
	}

	protected void close0() {

	}

}
