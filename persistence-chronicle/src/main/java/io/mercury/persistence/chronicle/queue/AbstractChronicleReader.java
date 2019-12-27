package io.mercury.persistence.chronicle.queue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.datetime.TimeConst;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.common.utils.Assertor;
import io.mercury.persistence.chronicle.exception.ChronicleReadException;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.TailerState;

public abstract class AbstractChronicleReader<T> implements Runnable {

	private final String readerName;
	private final FileCycle fileCycle;

	protected ReadParam readParam;

	protected final Logger logger;
	protected final ExcerptTailer excerptTailer;

	private final Consumer<T> consumer;

	AbstractChronicleReader(String readerName, FileCycle fileCycle, ReadParam readParam, Logger logger,
			ExcerptTailer excerptTailer, Consumer<T> consumer) {
		this.readerName = readerName;
		this.fileCycle = fileCycle;
		this.readParam = readParam;
		this.logger = logger;
		this.excerptTailer = excerptTailer;
		this.consumer = consumer;
	}

	public ExcerptTailer excerptTailer() {
		return excerptTailer;
	}

	public boolean moveTo(LocalDate date) {
		return moveTo(date.toEpochDay() * TimeConst.SECONDS_PER_DAY);
	}

	public boolean moveTo(LocalDateTime dateTime, ZoneId zoneId) {
		return moveTo(ZonedDateTime.of(dateTime, zoneId));
	}

	public boolean moveTo(ZonedDateTime dateTime) {
		return moveTo(dateTime.toEpochSecond());
	}

	/**
	 * Move cursor to input epoch seconds.
	 * 
	 * @param epochSecond
	 * @return
	 */
	public boolean moveTo(long epochSecond) {
		return excerptTailer.moveToIndex(fileCycle.toIndex(epochSecond));
	}

	public void toStart() {
		excerptTailer.toStart();
	}

	public void toEnd() {
		excerptTailer.toEnd();
	}

	public int cycle() {
		return excerptTailer.cycle();
	}

	public long epochSecond() {
		return ((long) excerptTailer.cycle()) * fileCycle.getSeconds();
	}

	public long index() {
		return excerptTailer.index();
	}

	public TailerState state() {
		return excerptTailer.state();
	}

	public String readerName() {
		return readerName;
	}

	public Thread runWithNewThread() {
		return runWithNewThread(readerName);
	}

	public Thread runWithNewThread(String threadName) {
		return ThreadUtil.startNewThread(this, threadName);
	}

	public static class ReadParam {

		private boolean readFailCrash;
		private boolean readFailLogging;
		private TimeUnit readIntervalUnit;
		private long readIntervalTime;
		private TimeUnit delayReadUnit;
		private long delayReadTime;
		private boolean waitingData;

		private boolean asyncExit;
		private Runnable exitRunnable;

		private ReadParam(Builder builder) {
			this.readFailCrash = builder.readFailCrash;
			this.readFailLogging = builder.readFailLogging;
			this.readIntervalUnit = builder.readIntervalUnit;
			this.readIntervalTime = builder.readIntervalTime;
			this.delayReadUnit = builder.delayReadUnit;
			this.delayReadTime = builder.delayReadTime;
			this.waitingData = builder.waitingData;
			this.asyncExit = builder.asyncExit;
			this.exitRunnable = builder.exitRunnable;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		static ReadParam Default() {
			return new Builder().build();
		}

		public static class Builder {

			private boolean readFailCrash = false;
			private boolean readFailLogging = true;
			private boolean waitingData = true;

			private TimeUnit readIntervalUnit = TimeUnit.MILLISECONDS;
			private long readIntervalTime = 100;
			private TimeUnit delayReadUnit = TimeUnit.MILLISECONDS;
			private long delayReadTime = 0;

			private boolean asyncExit = false;
			private Runnable exitRunnable;

			public Builder readFailCrash(boolean readFailCrash) {
				this.readFailCrash = readFailCrash;
				return this;
			}

			public Builder readFailLogging(boolean readFailLogging) {
				this.readFailLogging = readFailLogging;
				return this;
			}

			public Builder waitingData(boolean waitingData) {
				this.waitingData = waitingData;
				return this;
			}

			public Builder readInterval(TimeUnit timeUnit, long time) {
				this.readIntervalUnit = Assertor.nonNull(timeUnit, "timeUnit");
				this.readIntervalTime = Assertor.longGreaterThan(time, 0, "time");
				return this;
			}

			public Builder delayRead(TimeUnit timeUnit, long time) {
				this.delayReadUnit = Assertor.nonNull(timeUnit, "timeUnit");
				this.delayReadTime = Assertor.longGreaterThan(time, 0, "time");
				return this;
			}

			public Builder asyncExit(boolean asyncExit) {
				this.asyncExit = asyncExit;
				return this;
			}

			public Builder exitRunnable(Runnable exitRunnable) {
				this.exitRunnable = exitRunnable;
				return this;
			}

			public ReadParam build() {
				return new ReadParam(this);
			}
		}
	}

	/**
	 * Get next element of current cursor position.
	 * 
	 * @return
	 * @throws ChronicleReadExceptions
	 */
	@MayThrowsRuntimeException(ChronicleReadException.class)
	@CheckForNull
	private T next() throws ChronicleReadException {
		try {
			return next0();
		} catch (Exception e) {
			throw new ChronicleReadException(e.getMessage(), e);
		}
	}

	@Override
	public void run() {
		if (readParam.delayReadTime > 0)
			ThreadUtil.sleep(readParam.delayReadUnit, readParam.delayReadTime);
		for (;;) {
			T next = null;
			try {
				next = next();
			} catch (ChronicleReadException e) {
				if (readParam.readFailLogging)
					logger.error("{} call next throw exception -> {}", readerName, e.getMessage(), e);
				if (readParam.readFailCrash)
					throw e;
			}
			if (next == null) {
				if (readParam.waitingData)
					ThreadUtil.sleep(readParam.readIntervalUnit, readParam.readIntervalTime);
				else {
					if (readParam.exitRunnable != null)
						if (readParam.asyncExit)
							ThreadUtil.startNewThread(readParam.exitRunnable, readerName + "-exit");
						else
							readParam.exitRunnable.run();
					logger.info("reader->{} exit.", readerName);
					break;
				}
			} else
				consumer.accept(next);
		}
	}

	protected abstract T next0();

}
