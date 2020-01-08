package io.mercury.persistence.chronicle.queue;

import java.util.function.Consumer;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;

import io.mercury.common.number.RandomNumber;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.chronicle.queue.AbstractChronicleReader.ReaderParam;

@Immutable
public class ChronicleStringQueue extends AbstractChronicleQueue<String, ChronicleStringReader, ChronicleStringAppender> {

	private ChronicleStringQueue(Builder builder) {
		super(builder);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	protected ChronicleStringReader createReader(String readerName, ReaderParam readerParam, Logger logger,
			Consumer<String> consumer) {
		return new ChronicleStringReader(readerName, fileCycle(), readerParam, logger, internalQueue().createTailer(),
				consumer);
	}

	@Override
	protected ChronicleStringAppender acquireAppender(String writerName, Logger logger) {
		return new ChronicleStringAppender(writerName, logger, internalQueue().acquireAppender());
	}

	public static class Builder extends BaseBuilder<Builder> {

		private Builder() {
		}

		public ChronicleStringQueue build() {
			return new ChronicleStringQueue(this);
		}

		@Override
		protected Builder self() {
			return this;
		}

	}

	public static void main(String[] args) {
		ChronicleStringQueue queue = ChronicleStringQueue.newBuilder().fileCycle(FileCycle.MINUTELY).build();
		ChronicleStringAppender queueWriter = queue.acquireAppender();
		ChronicleStringReader queueReader = queue.createReader(next -> System.out.println(next));
		new Thread(() -> {
			for (;;) {
				try {
					queueWriter.append(String.valueOf(RandomNumber.randomLong()));
					ThreadUtil.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		queueReader.runningOnNewThread();
	}

}
