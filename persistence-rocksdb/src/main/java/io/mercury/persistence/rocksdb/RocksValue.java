package io.mercury.persistence.rocksdb;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;

public interface RocksValue {

	/**
	 * 
	 * @return RocksDB value byte[]
	 */
	@Nonnull
	ByteBuf valueBytes();

	int valueLength();

}
