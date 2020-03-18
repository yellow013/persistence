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

	ChronicleStringAppender appender = persistence.acquireAppender();
	ChronicleStringReader reader = persistence.createReader(text -> System.out.println(text));

	boolean moved = reader.moveTo(LocalDateTime.now().minusMinutes(10), ZoneOffset.ofHours(8));

	System.out.println("is moved == " + moved);
	reader.runningOnNewThread();
	while (true) {
	    try {
		appender.append(LocalDateTime.now().toString());
		Thread.sleep(8000);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }

}
