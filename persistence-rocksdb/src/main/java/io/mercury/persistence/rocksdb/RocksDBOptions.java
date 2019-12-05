package io.mercury.persistence.rocksdb;

import java.nio.file.Path;

import org.rocksdb.Options;

import io.mercury.common.sys.SysProperties;

public final class RocksDBOptions {

	private final Options options;
	
	private Path savePath;

	private RocksDBOptions(Builder builder) {
		this.options = new Options();
		options.setCreateIfMissing(builder.createIfMissing);
		options.setCreateMissingColumnFamilies(builder.createMissingColumnFamilies);

	}
	
	private static final String FixedFolder = "rocksdb/";
	
	public static final String DefaultRootPath = SysProperties.JAVA_IO_TMPDIR + "/";
	public static final String DefaultFolder = "default/";

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private boolean createIfMissing = true;
		private boolean createMissingColumnFamilies = false;

		public boolean createIfMissing() {
			return createIfMissing;
		}

		public void createIfMissing(boolean createIfMissing) {
			this.createIfMissing = createIfMissing;
		}

		public boolean createMissingColumnFamilies() {
			return createMissingColumnFamilies;
		}

		public void createMissingColumnFamilies(boolean createMissingColumnFamilies) {
			this.createMissingColumnFamilies = createMissingColumnFamilies;
		}

		public RocksDBOptions build() {
			return new RocksDBOptions(this);
		}

	}

}
