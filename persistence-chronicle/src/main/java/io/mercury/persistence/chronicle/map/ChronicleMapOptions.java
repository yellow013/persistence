package io.mercury.persistence.chronicle.map;

import static io.mercury.common.utils.StringUtil.isPath;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.mercury.common.sys.SysProperties;

public final class ChronicleMapOptions<K, V> {

	private Class<K> keyClass;
	private Class<V> valueClass;
	private K averageKey;
	private V averageValue;

	private boolean putReturnsNull;
	private boolean removeReturnsNull;
	private boolean recover;
	private boolean persistent;

	private long entries;
	private int actualChunkSize;

	private String rootPath;
	private String folder;

	// final save path
	private Path savePath;

	private ChronicleMapOptions(Builder<K, V> builder) {
		this.keyClass = builder.keyClass;
		this.valueClass = builder.valueClass;
		this.averageKey = builder.averageKey;
		this.averageValue = builder.averageValue;
		this.putReturnsNull = builder.putReturnsNull;
		this.removeReturnsNull = builder.removeReturnsNull;
		this.recover = builder.recover;
		this.persistent = builder.persistent;
		this.entries = builder.entries;
		this.actualChunkSize = builder.actualChunkSize;
		this.rootPath = builder.rootPath;
		this.folder = builder.folder;
		initSavePath();
	}

	private static final String FixedFolder = "chronicle-map/";

	public static final String DefaultRootPath = SysProperties.JAVA_IO_TMPDIR + "/";
	public static final String DefaultFolder = "default/";

	private void initSavePath() {
		this.savePath = Paths.get(rootPath, FixedFolder, folder);
	}

	public static <K, V> Builder<K, V> builder(Class<K> keyClass, Class<V> valueClass) {
		return new Builder<>(keyClass, valueClass);
	}

	public static <K, V> Builder<K, V> builder(Class<K> keyClass, Class<V> valueClass, String rootPath, String folder) {
		return new Builder<>(keyClass, valueClass, rootPath, folder);
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

	public Path savePath() {
		return savePath;
	}

	public static class Builder<K, V> {

		private Class<K> keyClass;
		private Class<V> valueClass;
		private String rootPath = DefaultRootPath;
		private String folder = DefaultRootPath;

		private K averageKey;
		private V averageValue;

		private boolean putReturnsNull = false;
		private boolean removeReturnsNull = false;
		private boolean recover = false;
		private boolean persistent = true;

		private long entries = 32 << 16;
		private int actualChunkSize;

		public Builder(Class<K> keyClass, Class<V> valueClass) {
			this.keyClass = keyClass;
			this.valueClass = valueClass;
		}

		public Builder(Class<K> keyClass, Class<V> valueClass, String rootPath, String folder) {
			this.keyClass = keyClass;
			this.valueClass = valueClass;
			this.rootPath = isPath(rootPath) ? rootPath : rootPath + "/";
			this.folder = isPath(folder) ? folder : folder + "/";
		}

		public Builder<K, V> averageKey(K averageKey) {
			this.averageKey = averageKey;
			return this;
		}

		public Builder<K, V> averageValue(V averageValue) {
			this.averageValue = averageValue;
			return this;
		}

		public Builder<K, V> putReturnsNull(boolean putReturnsNull) {
			this.putReturnsNull = putReturnsNull;
			return this;
		}

		public Builder<K, V> removeReturnsNull(boolean removeReturnsNull) {
			this.removeReturnsNull = removeReturnsNull;
			return this;
		}

		public Builder<K, V> recover(boolean recover) {
			this.recover = recover;
			return this;
		}

		public Builder<K, V> persistent(boolean persistent) {
			this.persistent = persistent;
			return this;
		}

		public Builder<K, V> actualChunkSize(int actualChunkSize) {
			this.actualChunkSize = actualChunkSize;
			return this;
		}

		public Builder<K, V> entries(long entries) {
			this.entries = entries;
			return this;
		}

		public ChronicleMapOptions<K, V> build() {
			return new ChronicleMapOptions<>(this);
		}
	}

}
