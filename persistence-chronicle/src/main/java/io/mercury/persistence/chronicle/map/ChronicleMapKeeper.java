package io.mercury.persistence.chronicle.map;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.collections.customize.Keeper;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

@ThreadSafe
public class ChronicleMapKeeper<K, V> extends Keeper<String, ChronicleMap<K, V>> {

	private ChronicleMapAttributes<K, V> attributes;

	public ChronicleMapKeeper(@Nonnull ChronicleMapAttributes<K, V> attributes) {
		if (attributes == null)
			throw new IllegalArgumentException("attributes can not be null");
		this.attributes = attributes;
	}

	@MayThrowsRuntimeException(ChronicleIOException.class)
	@Override
	public ChronicleMap<K, V> acquire(String filename) throws ChronicleIOException {
		return super.acquire(filename);
	}

	@Override
	protected ChronicleMap<K, V> createWithKey(String filename) {
		ChronicleMapBuilder<K, V> builder = ChronicleMapBuilder.of(attributes.keyClass(), attributes.valueClass())
				.putReturnsNull(attributes.putReturnsNull()).removeReturnsNull(attributes.removeReturnsNull())
				.entries(attributes.entries());
		if (attributes.actualChunkSize() > 0)
			builder.actualChunkSize(attributes.actualChunkSize());
		if (attributes.averageKey() != null)
			builder.averageKey(attributes.averageKey());
		if (attributes.averageValue() != null)
			builder.averageValue(attributes.averageValue());
		if (attributes.persistent()) {
			File persistedFile = new File(attributes.savePath(), filename);
			try {
				if (!persistedFile.exists()) {
					File parentFile = persistedFile.getParentFile();
					if (!parentFile.exists())
						parentFile.mkdirs();
					return builder.createPersistedTo(persistedFile);
				} else {
					if (attributes.recover())
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
