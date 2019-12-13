package io.mercury.persistence.chronicle.queue;

import io.mercury.common.number.RandomNumber;
import io.mercury.persistence.chronicle.queue.accessor.StringReader;
import io.mercury.persistence.chronicle.queue.accessor.StringWriter;

public class ChronicleStringQueue extends AbstractChronicleQueue<String, StringReader, StringWriter> {

	private ChronicleStringQueue(Builder builder) {
		super(builder);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public StringReader createReader(String readerName) {
		return StringReader.wrap(readerName, internalQueue().createTailer(), fileCycle());
	}

	@Override
	public StringWriter acquireWriter(String writerName) {
		return StringWriter.wrap(writerName, internalQueue().acquireAppender());
	}

	public static class Builder extends BaseBuilder<Builder> {

		public ChronicleStringQueue build() {
			return new ChronicleStringQueue(this);
		}

		@Override
		protected Builder self() {
			return this;
		}

	}

	public static void main(String[] args) {
		ChronicleStringQueue dataPersistence = ChronicleStringQueue.newBuilder().fileCycle(FileCycle.HOURLY).build();
		StringWriter queueWriter = dataPersistence.acquireWriter();
		StringReader queueReader = dataPersistence.createReader();
		new Thread(() -> {
			for (;;) {
				try {
					queueWriter.append(String.valueOf(RandomNumber.randomLong()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		CharSequence read = "";
		long nanoTime0 = System.nanoTime();
		do {
			try {
				read = queueReader.next();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (read != null);
		long nanoTime1 = System.nanoTime();
		System.out.println((nanoTime1 - nanoTime0) / 1000);

	}

}
