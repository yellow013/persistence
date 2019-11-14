package io.mercury.persistence.rocksdb;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;

public class RocksContainer<K extends RocksKey, V extends RocksValue> {

	static {
		RocksDB.loadLibrary();
	}

	public static void main(String[] args) {

		Options options = new Options();
	}

}
