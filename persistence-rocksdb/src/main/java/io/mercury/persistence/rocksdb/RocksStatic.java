package io.mercury.persistence.rocksdb;

import java.util.concurrent.atomic.AtomicBoolean;

import org.rocksdb.RocksDB;

public final class RocksStatic {

	private static AtomicBoolean IsLoadLibrary = new AtomicBoolean(false);

	public static void loadLibrary() {
		if (IsLoadLibrary.compareAndSet(false, true))
			RocksDB.loadLibrary();
	}

}
