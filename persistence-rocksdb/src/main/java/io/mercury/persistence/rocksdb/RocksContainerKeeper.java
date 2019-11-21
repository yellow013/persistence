package io.mercury.persistence.rocksdb;

import io.ffreedom.common.collections.customize.Keeper;

public class RocksContainerKeeper<K extends RocksKey, V extends RocksValue>
		extends Keeper<String, RocksContainer<K, V>> {

	public RocksContainerKeeper() {
		RocksStatic.loadLibrary();
	}

	@Override
	protected RocksContainer<K, V> createWith(String k) {
		// TODO Auto-generated method stub
		return null;
	}

}
