package io.mercury.persistence.rocksdb;

import org.rocksdb.Options;

public class RocksContainer<K extends RocksKey, V extends RocksValue> {
	
	
	static {
		RocksStatic.loadLibrary();
	}

	public static void main(String[] args) {

		Options options = new Options();

	}

	public V get(K k) {
		k.key();
		
		return null;
	}

}
