package io.mercury.persistence.rocksdb.map;

import org.rocksdb.Options;

import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.rocksdb.RocksStatic;
import io.mercury.persistence.rocksdb.map.entity.RocksReversibleKey;
import io.mercury.persistence.rocksdb.map.entity.RocksValue;

public class RocksReversibleMap<K extends RocksReversibleKey, V extends RocksValue> {

	static {
		RocksStatic.loadLibrary();
	}

	public static void main(String[] args) {

		Options options = new Options();

		Runtime.getRuntime().addShutdownHook(ThreadUtil.newThread(() -> options.close(), "RocksContainerCloseThread"));

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
