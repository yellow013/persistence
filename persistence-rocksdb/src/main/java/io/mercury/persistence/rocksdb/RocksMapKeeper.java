package io.mercury.persistence.rocksdb;

import io.mercury.common.collections.customize.Keeper;
import io.mercury.persistence.rocksdb.map.RocksMap;
import io.mercury.persistence.rocksdb.map.entity.RocksKey;
import io.mercury.persistence.rocksdb.map.entity.RocksValue;

public class RocksMapKeeper<K extends RocksKey, V extends RocksValue> extends Keeper<String, RocksMap<K, V>> {

	public RocksMapKeeper() {
		RocksStatic.loadLibrary();
	}

	@Override
	protected RocksMap<K, V> createWithKey(String k) {
		// TODO Auto-generated method stub
		return null;
	}

}
