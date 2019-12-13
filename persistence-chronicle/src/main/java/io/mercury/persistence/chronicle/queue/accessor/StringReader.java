package io.mercury.persistence.chronicle.queue.accessor;

import javax.annotation.concurrent.NotThreadSafe;

import io.mercury.persistence.chronicle.queue.FileCycle;
import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class StringReader extends AbstractDataReader<String> {

	private StringReader(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		super(name, tailer, fileCycle);
	}

	public static StringReader wrap(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		return new StringReader(name, tailer, fileCycle);
	}

	@Override
	protected String next0() {
		return tailer.readText();
	}

}
