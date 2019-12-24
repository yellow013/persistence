package io.mercury.persistence.chronicle.queue;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class ChronicleStringReader extends AbstractChronicleReader<String> {

	private ChronicleStringReader(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		super(name, tailer, fileCycle);
	}

	static ChronicleStringReader wrap(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		return new ChronicleStringReader(name, tailer, fileCycle);
	}

	@Override
	protected String next0() {
		return internalTailer.readText();
	}

}
