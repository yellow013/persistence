package io.mercury.persistence.chronicle.map;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.collections.customize.BaseKeeper;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

@ThreadSafe
public class ChronicleMapKeeper<K, V> extends BaseKeeper<String, ChronicleMap<K, V>> {

	private ChronicleMapOptions<K, V> options;

	public ChronicleMapKeeper(@Nonnull ChronicleMapOptions<K, V> options) {
		if (options == null)
			throw new IllegalArgumentException("attributes can not be null");
		this.options = options;
	}

	@MayThrowsRuntimeException(ChronicleIOException.class)
	@Override
	public ChronicleMap<K, V> acquire(String filename) throws ChronicleIOException {
		return super.acquire(filename);
	}

	@Override
	protected ChronicleMap<K, V> createWithKey(String filename) {
		ChronicleMapBuilder<K, V> builder = ChronicleMapBuilder.of(options.keyClass(), options.valueClass())
				.putReturnsNull(options.putReturnsNull()).removeReturnsNull(options.removeReturnsNull())
				.entries(options.entries());
		if (options.actualChunkSize() > 0)
			builder.actualChunkSize(options.actualChunkSize());
		if (options.averageKey() != null)
			builder.averageKey(options.averageKey());
		if (options.averageValue() != null)
			builder.averageValue(options.averageValue());
		if (options.persistent()) {
			File persistedFile = new File(options.savePath(), filename);
			try {
				if (!persistedFile.exists()) {
					File parentFile = persistedFile.getParentFile();
					if (!parentFile.exists())
						parentFile.mkdirs();
					return builder.createPersistedTo(persistedFile);
				} else {
					if (options.recover())
						return builder.createOrRecoverPersistedTo(persistedFile);
					else
						return builder.createPersistedTo(persistedFile);
				}
			} catch (IOException e) {
				throw new ChronicleIOException(e);
			}
		} else
			return builder.create();
	}

}
