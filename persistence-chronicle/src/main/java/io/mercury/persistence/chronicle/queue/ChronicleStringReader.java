package io.mercury.persistence.chronicle.queue;

import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;

import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class ChronicleStringReader extends AbstractChronicleReader<String> {

	ChronicleStringReader(String name, FileCycle fileCycle, ReadParam readParam, Logger logger,
			ExcerptTailer excerptTailer, Consumer<String> consumer) {
		super(name, fileCycle, readParam, logger, excerptTailer, consumer);
	}

	@Override
	protected String next0() {
		return excerptTailer.readText();
	}

}
