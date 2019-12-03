package io.mercury.persistence.chronicle.map;

import static io.mercury.common.utils.StringUtil.isPath;

import io.mercury.common.env.SysPropertys;

public final class ChronicleMapAttributes<K, V> {

	private Class<K> keyClass;
	private Class<V> valueClass;
	private K averageKey;
	private V averageValue;

	private boolean putReturnsNull = false;
	private boolean removeReturnsNull = false;
	private boolean recover = false;
	private boolean persistent = true;

	private long entries = 32 << 16;
	private int actualChunkSize;

	private String rootPath;
	private String folder;
	private String savePath;

	private ChronicleMapAttributes(Class<K> keyClass, Class<V> valueClass, String rootPath, String folder) {
		this.keyClass = keyClass;
		this.valueClass = valueClass;
		this.rootPath = isPath(rootPath) ? rootPath : rootPath + "/";
		this.folder = isPath(folder) ? folder : folder + "/";
		setSavePath();
	}

	private static final String ChronicleMapFolder = "chronicle-map/";

	private void setSavePath() {
		this.savePath = rootPath + ChronicleMapFolder + folder;
	}

	private static final String DefaultRootPath = SysPropertys.JAVA_IO_TMPDIR + "/";
	private static final String DefaultFolder = "default/";

	public static <K, V> ChronicleMapAttributes<K, V> buildOf(Class<K> keyClass, Class<V> valueClass) {
		return new ChronicleMapAttributes<>(keyClass, valueClass, DefaultRootPath, DefaultFolder);
	}

	public static <K, V> ChronicleMapAttributes<K, V> buildOf(Class<K> keyClass, Class<V> valueClass, String rootPath,
			String folder) {
		return new ChronicleMapAttributes<>(keyClass, valueClass, rootPath, folder);
	}

	public Class<K> keyClass() {
		return keyClass;
	}

	public Class<V> valueClass() {
		return valueClass;
	}

	public K averageKey() {
		return averageKey;
	}

	public V averageValue() {
		return averageValue;
	}

	public boolean putReturnsNull() {
		return putReturnsNull;
	}

	public boolean removeReturnsNull() {
		return removeReturnsNull;
	}

	public boolean recover() {
		return recover;
	}

	public boolean persistent() {
		return persistent;
	}

	public int actualChunkSize() {
		return actualChunkSize;
	}

	public long entries() {
		return entries;
	}

	public String rootPath() {
		return rootPath;
	}

	public String folder() {
		return folder;
	}

	public String savePath() {
		return savePath;
	}

	public ChronicleMapAttributes<K, V> actualChunkSize(int actualChunkSize) {
		this.actualChunkSize = actualChunkSize;
		return this;
	}

	public ChronicleMapAttributes<K, V> entries(long entries) {
		this.entries = entries;
		return this;
	}

	public ChronicleMapAttributes<K, V> averageKey(K averageKey) {
		this.averageKey = averageKey;
		return this;
	}

	public ChronicleMapAttributes<K, V> averageValue(V averageValue) {
		this.averageValue = averageValue;
		return this;
	}

	public ChronicleMapAttributes<K, V> putReturnsNull(boolean putReturnsNull) {
		this.putReturnsNull = putReturnsNull;
		return this;
	}

	public ChronicleMapAttributes<K, V> removeReturnsNull(boolean removeReturnsNull) {
		this.removeReturnsNull = removeReturnsNull;
		return this;
	}

	public ChronicleMapAttributes<K, V> recover(boolean recover) {
		this.recover = recover;
		return this;
	}

	public ChronicleMapAttributes<K, V> persistent(boolean persistent) {
		this.persistent = persistent;
		return this;
	}

}
