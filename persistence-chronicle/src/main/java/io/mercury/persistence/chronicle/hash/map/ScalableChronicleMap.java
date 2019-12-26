package io.mercury.persistence.chronicle.hash.map;

import io.mercury.persistence.chronicle.hash.ScalableChronicle;
import net.openhft.chronicle.map.ChronicleMap;

public final class ScalableChronicleMap<K, V> implements ScalableChronicle<ChronicleMap<K, V>> {

	private ChronicleMap<K, V> entity;

	@Override
	public ChronicleMap<K, V> entity() {
		return entity;
	}

}
