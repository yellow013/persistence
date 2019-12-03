package io.mercury.persistence.rocksdb.map;

import java.io.Closeable;
import java.io.IOException;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

final class OriginalRocksDB implements Closeable {

	private final Options options;
	private final RocksDB rocksDB;

	public OriginalRocksDB(String path) throws RocksDBException {
		this.options = new Options();
		options.setCreateIfMissing(true);
		this.rocksDB = RocksDB.open(options, path);
	}


	
	

	@Override
	public void close() throws IOException {
		if (options != null)
			options.close();
		if (rocksDB != null)
			rocksDB.close();
	}
	
	
	public static void main(String[] args) {

	}

}
