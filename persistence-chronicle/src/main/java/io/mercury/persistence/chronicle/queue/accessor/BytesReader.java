package io.mercury.persistence.chronicle.queue.accessor;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import io.mercury.persistence.chronicle.queue.base.DataReader;
import io.mercury.persistence.chronicle.queue.base.FileCycle;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class BytesReader extends DataReader<ByteBuffer> {

	private BytesReader(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		super(name, tailer, fileCycle);
	}

	public static BytesReader wrap(String name, ExcerptTailer tailer, FileCycle fileCycle) {
		return new BytesReader(name, tailer, fileCycle);
	}

	@Override
	protected ByteBuffer next0() {
		// use heap memory
		Bytes<ByteBuffer> bytes = Bytes.elasticByteBuffer();
		tailer.readBytes(bytes);
		// use direct memory
		// bytes.toTemporaryDirectByteBuffer();
		ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length());
		bytes.copyTo(byteBuffer);
		return byteBuffer;// ByteBuffer.wrap(bytes.toByteArray());
	}

}
