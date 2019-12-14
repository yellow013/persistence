package io.mercury.persistence.chronicle.map;

import static io.mercury.common.utils.StringUtil.fixPath;

import java.io.File;
import java.util.Objects;

import javax.annotation.Nonnull;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.collections.Capacity;
import io.mercury.common.config.Configurator;
import io.mercury.common.sys.SysProperties;

public final class ChronicleMapConfigurator<K, V> implements Configurator {

	private final Class<K> keyClass;
	private final Class<V> valueClass;
	private final K averageKey;
	private final V averageValue;

	private final boolean putReturnsNull;
	private final boolean removeReturnsNull;
	private final boolean recover;
	private final boolean persistent;

	private final long entries;
	private final int actualChunkSize;

	private final String rootPath;
	private final String folder;

	// final save path
	private final File savePath;

	private final String name;

	private ChronicleMapConfigurator(Builder<K, V> builder) {
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
		this.savePath = new File(rootPath + FixedFolder + folder);
		this.name = buildName();
	}

	private String buildName() {
		return savePath.getAbsolutePath() + ":[key==" + keyClass.getSimpleName() + ",value=="
				+ valueClass.getSimpleName() + "]";

	}

	private static final String FixedFolder = "chronicle-map/";

	@MayThrowsRuntimeException(NullPointerException.class)
	public static <K, V> Builder<K, V> builder(@Nonnull Class<K> keyClass, @Nonnull Class<V> valueClass) {
		Objects.requireNonNull(keyClass, "key class can not be null");
		Objects.requireNonNull(valueClass, "value class can not be null");
		return new Builder<>(keyClass, valueClass);
	}

	public static <K, V> Builder<K, V> builder(@Nonnull Class<K> keyClass, @Nonnull Class<V> valueClass,
			String rootPath, String folder) {
		Objects.requireNonNull(keyClass, "key class can not be null");
		Objects.requireNonNull(valueClass, "value class can not be null");
		return new Builder<>(keyClass, valueClass, rootPath, folder);
	}

	@Override
	public String name() {
		return name;
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

	public File savePath() {
		return savePath;
	}

	public static class Builder<K, V> {

		private Class<K> keyClass;
		private Class<V> valueClass;
		private String rootPath = SysProperties.JAVA_IO_TMPDIR + "/";
		private String folder = "default/";

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
			this.rootPath = fixPath(rootPath);
			this.folder = fixPath(rootPath);
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

		public Builder<K, V> entriesOfPow2(Capacity capacity) {
			this.entries = capacity.size();
			return this;
		}

		public ChronicleMapConfigurator<K, V> build() {
			return new ChronicleMapConfigurator<>(this);
		}
	}

}