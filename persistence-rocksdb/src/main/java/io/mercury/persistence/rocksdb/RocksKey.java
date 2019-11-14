package io.mercury.persistence.rocksdb;

import javax.annotation.Nonnull;

public interface RocksKey {

	/**
	 * 
	 * @return RocksDB key byte[]
	 */
	@Nonnull
	byte[] keyBytes();

	default int keyLength() {
		return keyBytes().length;
	}

}
