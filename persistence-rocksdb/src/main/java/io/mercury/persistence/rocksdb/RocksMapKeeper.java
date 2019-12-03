package io.mercury.persistence.rocksdb;

import io.mercury.common.collections.customize.Keeper;
import io.mercury.persistence.rocksdb.entity.RocksEntity;

public class RocksContainerKeeper<T extends RocksEntity> extends Keeper<String, RocksContainer<T>> {

	public RocksContainerKeeper() {
		RocksStatic.loadLibrary();
	}

	@Override
	protected RocksContainer<T> createWith(String k) {
		// TODO Auto-generated method stub
		return null;
	}

}
