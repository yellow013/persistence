package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.NotThreadSafe;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class ChronicleBytesReader extends AbstractChronicleReader<ByteBuffer> {

	private final int readBufferSize;
	private final boolean useDirectMemory;

	private ChronicleBytesReader(String name, int readBufferSize, boolean useDirectMemory, ExcerptTailer tailer,
			FileCycle fileCycle) {
		super(name, tailer, fileCycle);
		this.readBufferSize = readBufferSize;
		this.useDirectMemory = useDirectMemory;
	}

	static ChronicleBytesReader wrap(String name, int readBufferSize, boolean useDirectMemory, ExcerptTailer tailer,
			FileCycle fileCycle) {
		return new ChronicleBytesReader(name, readBufferSize, useDirectMemory, tailer, fileCycle);
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
