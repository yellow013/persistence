package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class ChronicleBytesReader extends AbstractChronicleReader<ByteBuffer> {

	private final int readBufferSize;
	private final boolean useDirectMemory;

	ChronicleBytesReader(String name, FileCycle fileCycle, ReadParam readParam, Logger logger, int readBufferSize,
			boolean useDirectMemory, ExcerptTailer excerptTailer, Consumer<ByteBuffer> consumer) {
		super(name, fileCycle, readParam, logger, excerptTailer, consumer);
		this.readBufferSize = readBufferSize;
		this.useDirectMemory = useDirectMemory;
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
		excerptTailer.readBytes(bytes);
		if (bytes.isEmpty())
			return null;
		// System.out.println(bytes.toDebugString());
		return bytes.underlyingObject();
	}

}
