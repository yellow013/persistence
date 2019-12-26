package io.mercury.persistence.chronicle.hash.set;

import io.mercury.persistence.chronicle.hash.ScalableChronicle;
import net.openhft.chronicle.set.ChronicleSet;

public class ScalableChronicleSet<K> implements ScalableChronicle<ChronicleSet<K>> {

	private ChronicleSet<K> entity;

	@Override
	public ChronicleSet<K> entity() {
		return entity;
	}

}
