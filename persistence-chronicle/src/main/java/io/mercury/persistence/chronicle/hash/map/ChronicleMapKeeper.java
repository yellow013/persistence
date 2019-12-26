package io.mercury.persistence.chronicle.hash.map;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.collections.customize.BaseKeeper;
import io.mercury.common.utils.Assertor;
import io.mercury.persistence.chronicle.exception.ChronicleIOException;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

@ThreadSafe
public class ChronicleMapKeeper<K, V> extends BaseKeeper<String, ChronicleMap<K, V>> {

	private ChronicleMapConfigurator<K, V> configurator;

	public ChronicleMapKeeper(@Nonnull ChronicleMapConfigurator<K, V> configurator) {
		this.configurator = Assertor.nonNull(configurator, "configurator");
	}

	@MayThrowsRuntimeException(ChronicleIOException.class)
	@Override
	public ChronicleMap<K, V> acquire(String filename) throws ChronicleIOException {
		return super.acquire(filename);
	}

	@Override
	protected ChronicleMap<K, V> createWithKey(String filename) {
		ChronicleMapBuilder<K, V> builder = ChronicleMapBuilder.of(configurator.keyClass(), configurator.valueClass())
				.putReturnsNull(configurator.putReturnsNull()).removeReturnsNull(configurator.removeReturnsNull())
				.entries(configurator.entries());
		if (configurator.actualChunkSize() > 0)
			builder.actualChunkSize(configurator.actualChunkSize());
		if (configurator.averageKey() != null)
			builder.averageKey(configurator.averageKey());
		if (configurator.averageValue() != null)
			builder.averageValue(configurator.averageValue());
		if (configurator.persistent()) {
			File persistedFile = new File(configurator.savePath(), filename);
			try {
				if (!persistedFile.exists()) {
					File parentFile = persistedFile.getParentFile();
					if (!parentFile.exists())
						parentFile.mkdirs();
					return builder.createPersistedTo(persistedFile);
				} else {
					if (configurator.recover())
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
