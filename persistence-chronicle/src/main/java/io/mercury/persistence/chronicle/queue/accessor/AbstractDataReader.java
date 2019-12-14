package io.mercury.persistence.chronicle.queue.accessor;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.persistence.chronicle.exception.ChronicleReadException;
import io.mercury.persistence.chronicle.queue.FileCycle;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.TailerState;

public abstract class AbstractDataReader<T> {

	protected ExcerptTailer tailer;

	private FileCycle fileCycle;

	private String name;

	protected AbstractDataReader(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		this.name = name;
		this.tailer = tailer;
		this.fileCycle = fileCycle;
	}

	public ExcerptTailer getTailer() {
		return tailer;
	}

	/**
	 * Move cursor to input epoch seconds.
	 * 
	 * @param epochSecond
	 * @return
	 */
	public boolean moveTo(long epochSecond) {
		return tailer.moveToIndex(fileCycle.calculateIndex(epochSecond));
	}

	public void toStart() {
		tailer.toStart();
	}

	public void toEnd() {
		tailer.toEnd();
	}

	public int cycle() {
		return tailer.cycle();
	}

	public long epochSecond() {
		return (long) tailer.cycle() * fileCycle.getSeconds();
	}

	public long index() {
		return tailer.index();
	}

	public TailerState state() {
		return tailer.state();
	}

	public String name() {
		return name;
	}

	/**
	 * Get next element of current cursor position.
	 * 
	 * @return
	 * @throws ChronicleReadException
	 */
	@MayThrowsRuntimeException(ChronicleReadException.class)
	public T next() throws ChronicleReadException {
		try {
			return next0();
		} catch (Exception e) {
			throw new ChronicleReadException(e.getMessage(), e);
		}
	}

	protected abstract T next0();

}