package io.mercury.persistence.chronicle.queue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.annotation.CheckForNull;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.datetime.TimeConst;
import io.mercury.persistence.chronicle.exception.ChronicleReadException;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.TailerState;

abstract class AbstractChronicleReader<T> {

	protected final ExcerptTailer internalTailer;

	private final FileCycle fileCycle;
	private final String name;

	protected AbstractChronicleReader(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		this.name = name;
		this.internalTailer = tailer;
		this.fileCycle = fileCycle;
	}

	public ExcerptTailer internalTailer() {
		return internalTailer;
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
		return internalTailer.moveToIndex(fileCycle.toIndex(epochSecond));
	}

	public void toStart() {
		internalTailer.toStart();
	}

	public void toEnd() {
		internalTailer.toEnd();
	}

	public int cycle() {
		return internalTailer.cycle();
	}

	public long epochSecond() {
		return ((long) internalTailer.cycle()) * fileCycle.getSeconds();
	}

	public long index() {
		return internalTailer.index();
	}

	public TailerState state() {
		return internalTailer.state();
	}

	public String name() {
		return name;
	}

	/**
	 * Get next element of current cursor position.
	 * 
	 * @return
	 * @throws ChronicleReadExceptions
	 */
	@MayThrowsRuntimeException(ChronicleReadException.class)
	@CheckForNull
	public T next() throws ChronicleReadException {
		try {
			return next0();
		} catch (Exception e) {
			throw new ChronicleReadException(e.getMessage(), e);
		}
	}

	protected abstract T next0();

}
