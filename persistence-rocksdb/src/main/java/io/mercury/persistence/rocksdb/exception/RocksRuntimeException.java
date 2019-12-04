package io.mercury.persistence.rocksdb;

public final class RocksDBRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6940482392634006619L;

	public RocksDBRuntimeException() {
		super();
	}

	public RocksDBRuntimeException(String message) {
		super(message);
	}

	public RocksDBRuntimeException(Throwable throwable) {
		super(throwable);
	}

	public RocksDBRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
