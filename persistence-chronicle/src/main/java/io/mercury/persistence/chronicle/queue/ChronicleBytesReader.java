package io.mercury.persistence.chronicle.queue;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;

@NotThreadSafe
public final class ChronicleBytesReader extends AbstractChronicleReader<ByteBuffer> {

	private final int bufferSize;
	private final boolean useDirectMemory;

	ChronicleBytesReader(String name, FileCycle fileCycle, ReaderParam readerParam, Logger logger, int bufferSize,
			boolean useDirectMemory, ExcerptTailer excerptTailer, Consumer<ByteBuffer> consumer) {
		super(name, fileCycle, readerParam, logger, excerptTailer, consumer);
		this.bufferSize = bufferSize;
		this.useDirectMemory = useDirectMemory;
	}

	@Override
	protected ByteBuffer next0() {
		Bytes<ByteBuffer> bytes;
		if (useDirectMemory)
			// use direct memory
			bytes = Bytes.elasticByteBuffer(bufferSize);
		else
			// use heap memory
			bytes = Bytes.elasticHeapByteBuffer(bufferSize);
		excerptTailer.readBytes(bytes);
		if (bytes.isEmpty())
			return null;
		// System.out.println(bytes.toDebugString());
		return bytes.underlyingObject();
	}

}
