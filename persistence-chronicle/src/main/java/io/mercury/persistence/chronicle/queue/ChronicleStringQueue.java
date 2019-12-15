package io.mercury.persistence.chronicle.queue;

import io.mercury.common.number.RandomNumber;
import io.mercury.common.thread.ThreadUtil;
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
		StringWriter queueWriter = queue.acquireWriter();
		StringReader queueReader = queue.createReader();
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
		do {
			try {
				String next = queueReader.next();
				if (next == null)
					ThreadUtil.sleep(100);
				else
					System.out.println(next);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (true);

	}

}
