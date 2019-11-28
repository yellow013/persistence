package io.mercury.persistence.chronicle.map;

import java.time.LocalDate;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.datetime.DateTimeUtil;
import net.openhft.chronicle.map.ChronicleMap;

public final class ChronicleMapKeeperOfLocalDate<K, V> extends ChronicleMapKeeper<K, V> {

	public ChronicleMapKeeperOfLocalDate(ChronicleMapAttributes<K, V> attributes) {
		super(attributes);
	}

	@MayThrowsRuntimeException
	public ChronicleMap<K, V> acquire(LocalDate date) throws ChronicleIOException {
		return super.acquire(String.valueOf(DateTimeUtil.date(date)));
	}

}
