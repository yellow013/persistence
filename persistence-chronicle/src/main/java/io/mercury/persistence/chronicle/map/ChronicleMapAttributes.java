package io.mercury.persistence.chronicle.map;

import static io.ffreedom.common.utils.StringUtil.isPath;

import io.ffreedom.common.env.SystemPropertys;

public final class ChronicleMapAttributes<K, V> {

	private Class<K> keyClass;
	private Class<V> valueClass;
	private K averageKey;
	private V averageValue;
	private String savePath;

	private boolean putReturnsNull = false;
	private boolean removeReturnsNull = false;
	private boolean recover = false;
	private boolean persistent = true;

	private long entries = 32 << 16;
	private int actualChunkSize;
	private String rootPath;
	private String folder;

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

	private static final String DefaultRootPath = SystemPropertys.JAVA_IO_TMPDIR + "/";
	private static final String DefaultFolder = "default/";

	public static <K, V> ChronicleMapAttributes<K, V> buildOf(Class<K> keyClass, Class<V> valueClass) {
		return new ChronicleMapAttributes<>(keyClass, valueClass, DefaultRootPath, DefaultFolder);
	}

	public static <K, V> ChronicleMapAttributes<K, V> buildOf(Class<K> keyClass, Class<V> valueClass, String rootPath,
			String folder) {
		return new ChronicleMapAttributes<>(keyClass, valueClass, rootPath, folder);
	}

	public Class<K> getKeyClass() {
		return keyClass;
	}

	public Class<V> getValueClass() {
		return valueClass;
	}

	public K getAverageKey() {
		return averageKey;
	}

	public V getAverageValue() {
		return averageValue;
	}

	public String getSavePath() {
		return savePath;
	}

	public boolean isPutReturnsNull() {
		return putReturnsNull;
	}

	public boolean isRemoveReturnsNull() {
		return removeReturnsNull;
	}

	public boolean isRecover() {
		return recover;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public int getActualChunkSize() {
		return actualChunkSize;
	}

	public long getEntries() {
		return entries;
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getFolder() {
		return folder;
	}

	public ChronicleMapAttributes<K, V> setActualChunkSize(int actualChunkSize) {
		this.actualChunkSize = actualChunkSize;
		return this;
	}

	public ChronicleMapAttributes<K, V> setEntries(long entries) {
		this.entries = entries;
		return this;
	}

	public ChronicleMapAttributes<K, V> setAverageKey(K averageKey) {
		this.averageKey = averageKey;
		return this;
	}

	public ChronicleMapAttributes<K, V> setAverageValue(V averageValue) {
		this.averageValue = averageValue;
		return this;
	}

	public ChronicleMapAttributes<K, V> setPutReturnsNull(boolean putReturnsNull) {
		this.putReturnsNull = putReturnsNull;
		return this;
	}

	public ChronicleMapAttributes<K, V> setRemoveReturnsNull(boolean removeReturnsNull) {
		this.removeReturnsNull = removeReturnsNull;
		return this;
	}

	public ChronicleMapAttributes<K, V> setRecover(boolean recover) {
		this.recover = recover;
		return this;
	}

	public ChronicleMapAttributes<K, V> setPersistent(boolean persistent) {
		this.persistent = persistent;
		return this;
	}

}
