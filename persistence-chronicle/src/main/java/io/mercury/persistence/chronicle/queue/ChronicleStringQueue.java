package io.mercury.persistence.chronicle.queue;

import io.ffreedom.common.number.RandomNumber;
import io.mercury.persistence.chronicle.queue.accessor.StringReader;
import io.mercury.persistence.chronicle.queue.accessor.StringWriter;
import io.mercury.persistence.chronicle.queue.base.BaseChronicleQueue;
import io.mercury.persistence.chronicle.queue.base.FileCycle;

public class ChronicleStringQueue extends BaseChronicleQueue<String, StringReader, StringWriter> {

	private ChronicleStringQueue(Builder builder) {
		super(builder);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public StringReader createReader(String readerName) {
		return StringReader.wrap(readerName, getQueue().createTailer(), getFileCycle());
	}

	@Override
	public StringWriter acquireWriter(String writerName) {
		return StringWriter.wrap(writerName, getQueue().acquireAppender());
	}

	public static class Builder extends BaseBuilder<Builder> {

		public ChronicleStringQueue build() {
			return new ChronicleStringQueue(this);
		}

		@Override
		protected Builder getThis() {
			return this;
		}

	}

	public static void main(String[] args) {
		ChronicleStringQueue dataPersistence = ChronicleStringQueue.newBuilder().setFileCycle(FileCycle.HOURLY).build();
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
