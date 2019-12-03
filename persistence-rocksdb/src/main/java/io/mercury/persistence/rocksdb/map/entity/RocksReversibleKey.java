package io.mercury.persistence.rocksdb.entity;

public interface RocksReversibleEntity extends RocksEntity {

	/**
	 * 
	 * @return RocksDB reverse key byte[]
	 */
	byte[] reverseKey();

}
