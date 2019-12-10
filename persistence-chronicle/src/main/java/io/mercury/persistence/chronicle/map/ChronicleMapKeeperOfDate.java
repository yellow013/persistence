package io.mercury.persistence.chronicle.map;

import java.time.LocalDate;

import io.mercury.common.annotations.lang.MayThrowsRuntimeException;
import io.mercury.common.datetime.DateTimeUtil;
import net.openhft.chronicle.map.ChronicleMap;

public final class ChronicleMapKeeperOfDate<K, V> extends ChronicleMapKeeper<K, V> {

	public ChronicleMapKeeperOfDate(ChronicleMapOptions<K, V> options) {
		super(options);
	}

	@MayThrowsRuntimeException(ChronicleIOException.class)
	public ChronicleMap<K, V> acquire(LocalDate date) throws ChronicleIOException {
		return super.acquire(String.valueOf(DateTimeUtil.date(date)));
	}

}
