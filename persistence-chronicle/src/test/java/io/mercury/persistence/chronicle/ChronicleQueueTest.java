package io.mercury.persistence.chronicle;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Ignore;
import org.junit.Test;

import io.mercury.persistence.chronicle.queue.ChronicleStringAppender;
import io.mercury.persistence.chronicle.queue.ChronicleStringQueue;
import io.mercury.persistence.chronicle.queue.ChronicleStringReader;
import io.mercury.persistence.chronicle.queue.FileCycle;

public class ChronicleQueueTest {

	@Ignore
	@Test
	public void test0() {
		ChronicleStringQueue persistence = ChronicleStringQueue.newBuilder().fileCycle(FileCycle.MINUTELY).build();
		ChronicleStringAppender writer = persistence.acquireAppender();
		ChronicleStringReader reader = persistence.createReader(text -> System.out.println(text));

		LocalDateTime wantOf = LocalDateTime.of(2019, 9, 26, 20, 35);
		// Start 2019-09-26T20:35:02.526

		long epochSecond = wantOf.toEpochSecond(ZoneOffset.ofHours(8));
		boolean moved = reader.moveTo(epochSecond);
		System.out.println(moved);
		reader.runningOnNewThread();
		while (true) {
			try {
				writer.append(LocalDateTime.now().toString());
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
