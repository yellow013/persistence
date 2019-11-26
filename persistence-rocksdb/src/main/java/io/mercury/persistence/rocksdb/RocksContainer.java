package io.mercury.persistence.rocksdb;

import org.rocksdb.Options;

import io.ffreedom.common.thread.ThreadUtil;
import io.mercury.persistence.rocksdb.entity.RocksEntity;

public class RocksContainer<T extends RocksEntity> {

	static {
		RocksStatic.loadLibrary();
	}

	public static void main(String[] args) {

		Options options = new Options();

		Runtime.getRuntime().addShutdownHook(ThreadUtil.newThread(() -> options.close(), "RocksContainerCloseThread"));

	}

	public T get(byte[] key) {
		

		return null;
	}

}
