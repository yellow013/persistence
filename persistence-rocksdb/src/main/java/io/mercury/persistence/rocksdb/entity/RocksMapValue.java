package io.mercury.persistence.rocksdb;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public interface RocksValue {

	/**
	 * 
	 * @return RocksDB value ByteBuf
	 */
	@Nonnull
	default byte[] bytes() {
		return byteBuf().array();
	}

	int valueLength();

	/**
	 * 
	 * @return RocksDB value ByteBuf
	 */
	@Nonnull
	default ByteBuf byteBuf() {
		return byteBuf(Unpooled.buffer(valueLength()));
	}

	@Nonnull
	ByteBuf byteBuf(@Nonnull ByteBuf useBuf);

}
