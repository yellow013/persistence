package io.mercury.persistence.rocksdb.map;

import org.rocksdb.Options;
import org.rocksdb.RocksDBException;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.rocksdb.RocksDBRuntimeException;
import io.mercury.persistence.rocksdb.RocksStatic;
import io.mercury.persistence.rocksdb.map.entity.RocksKey;
import io.mercury.persistence.rocksdb.map.entity.RocksValue;

public class RocksMap<K extends RocksKey, V extends RocksValue> {

	static {
		RocksStatic.loadLibrary();
	}

	private final OriginalRocksDB rocksDB;

	@MayThrowsRuntimeException(RocksDBRuntimeException.class)
	public RocksMap() {
		try {
			this.rocksDB = new OriginalRocksDB(null);
		} catch (RocksDBException e) {
			throw new RocksDBRuntimeException(e);
		}
	}

	public static void main(String[] args) {

		Options options = new Options();

		Runtime.getRuntime().addShutdownHook(

				ThreadUtil.newThread(() -> options.close(), "RocksContainerCloseThread"));

	}

	public V get(K key) {

		return null;
	}

	public V get(byte[] key) {

		return null;
	}

	public V put(K key, V value) {
		return value;
	}

	public void scan() {

	}

}
