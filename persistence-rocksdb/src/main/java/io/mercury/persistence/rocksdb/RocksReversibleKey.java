package io.mercury.persistence.rocksdb;

public interface RocksReversibleKey extends RocksKey {

	/**
	 * 
	 * @return RocksDB reverse key byte[]
	 */
	byte[] reverseKeyBytes();

}
