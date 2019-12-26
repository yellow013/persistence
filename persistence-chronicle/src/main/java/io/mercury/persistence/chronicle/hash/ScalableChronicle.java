package io.mercury.persistence.chronicle.hash;

import net.openhft.chronicle.hash.ChronicleHash;

public interface ScalableChronicle<T extends ChronicleHash<?, ?, ?, ?>> {

	T entity();

}