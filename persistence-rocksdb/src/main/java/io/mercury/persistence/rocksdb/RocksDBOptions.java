package io.mercury.persistence.rocksdb;

import static io.mercury.common.utils.StringUtil.fixPath;

import java.io.File;

import org.rocksdb.Options;

import io.mercury.common.sys.SysProperties;

public final class RocksDBOptions {

	private final String rootPath;
	private final String folder;
	private final File savePath;

	private final Options options;

	private RocksDBOptions(Builder builder) {
		this.rootPath = builder.rootPath;
		this.folder = builder.folder;
		this.savePath = new File(rootPath + "rocksdb/" + folder);
		this.options = new Options();
		options.setCreateIfMissing(builder.createIfMissing);
		options.setCreateMissingColumnFamilies(builder.createMissingColumnFamilies);

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

	public Options options() {
		return options;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String rootPath = SysProperties.JAVA_IO_TMPDIR + "/";
		private String folder = "default/";

		private boolean createIfMissing = true;
		private boolean createMissingColumnFamilies = false;

		public void rootPath(String rootPath) {
			this.rootPath = fixPath(rootPath);
		}

		public void folder(String folder) {
			this.folder = fixPath(folder);
		}

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
