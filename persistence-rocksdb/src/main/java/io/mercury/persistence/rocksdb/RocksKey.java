package io.mercury.persistence.rocksdb;

import javax.annotation.Nonnull;

public interface RocksKey {
	
	

	default int keyLength() {
		return key().length;
	}

	/**
	 * 
	 * @return RocksDB key byte[]
	 */
	@Nonnull
	byte[] key();

}
