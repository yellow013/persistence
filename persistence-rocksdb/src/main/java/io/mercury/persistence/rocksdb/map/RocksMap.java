package io.mercury.persistence.rocksdb.map;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.thread.ShutdownHooks;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.rocksdb.RocksStatic;
import io.mercury.persistence.rocksdb.exception.RocksRuntimeException;
import io.mercury.persistence.rocksdb.map.entity.RocksKey;
import io.mercury.persistence.rocksdb.map.entity.RocksValue;

public class RocksMap<K extends RocksKey, V extends RocksValue> implements Map<K, V>, Closeable {

	static {
		RocksStatic.loadLibrary();
	}

	private final Options options;
	private final RocksDB rocksdb;

	@MayThrowsRuntimeException(RocksRuntimeException.class)
	public RocksMap(String savePath) {
		this.options = new Options();
		options.setCreateIfMissing(true);
		try {
			this.rocksdb = RocksDB.open(options, savePath);
		} catch (RocksDBException e) {
			throw new RocksRuntimeException(e);
		}
		ShutdownHooks.closeResourcesWhenShutdown(this);
	}

	public static void main(String[] args) {

		Options options = new Options();

		Runtime.getRuntime().addShutdownHook(

				ThreadUtil.newThread(() -> options.close(), "RocksContainerCloseThread"));

	}

	public void scan() {

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		if (options != null)
			options.close();
		if (rocksdb != null)
			rocksdb.close();
	}

}
