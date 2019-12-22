package io.mercury.persistence.chronicle.queue.accessor;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import io.mercury.persistence.chronicle.queue.FileCycle;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class BytesReader extends AbstractDataReader<ByteBuffer> {

	private final int readBufferSize;
	private final boolean useDirectMemory;

	private BytesReader(String name, int readBufferSize, boolean useDirectMemory, ExcerptTailer tailer,
			FileCycle fileCycle) {
		super(name, tailer, fileCycle);
		this.readBufferSize = readBufferSize;
		this.useDirectMemory = useDirectMemory;
	}

	public static BytesReader wrap(String name, int readBufferSize, boolean useDirectMemory, ExcerptTailer tailer,
			FileCycle fileCycle) {
		return new BytesReader(name, readBufferSize, useDirectMemory, tailer, fileCycle);
	}

	@Override
	protected ByteBuffer next0() {
		Bytes<ByteBuffer> bytes;
		if (useDirectMemory)
			// use direct memory
			bytes = Bytes.elasticByteBuffer(readBufferSize);
		else
			// use heap memory
			bytes = Bytes.elasticHeapByteBuffer(readBufferSize);
		tailer.readBytes(bytes);
		if (bytes.isEmpty())
			return null;
		// System.out.println(bytes.toDebugString());
		return bytes.underlyingObject();
	}

}
