package io.mercury.persistence.rocksdb;

import javax.annotation.Nonnull;

public interface RocksValue {

	/**
	 * 
	 * @return RocksDB value byte[]
	 */
	@Nonnull
	byte[] valueBytes();

	default int valueLength() {
		return valueBytes().length;
	}

}
