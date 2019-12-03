package io.mercury.persistence.rocksdb.entity;

import javax.annotation.Nonnull;

public interface RocksEntity extends RocksValue{

	/**
	 * 
	 * @return RocksDB columnFamily byte[]
	 */
	byte[] columnFamily();

	/**
	 * 
	 * @return RocksDB key byte[]
	 */
	@Nonnull
	byte[] key();

	int keyLength();

}
