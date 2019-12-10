package io.mercury.persistence.chronicle;

import org.junit.Ignore;
import org.junit.Test;

import io.mercury.common.collections.Capacity;
import io.mercury.common.sys.SysProperties;
import io.mercury.common.thread.ThreadUtil;
import io.mercury.persistence.chronicle.map.ChronicleMapKeeperOfDate;
import io.mercury.persistence.chronicle.map.ChronicleMapOptions;
import net.openhft.chronicle.map.ChronicleMap;

public class ChronicleMapTest {

	@Ignore
	@Test
	public void test0() {

		ChronicleMapOptions<String, byte[]> options = ChronicleMapOptions
				.builder(String.class, byte[].class, SysProperties.USER_HOME, "betting")
				.capacityOfPow2(Capacity.L16_SIZE_65536).averageKey(new String(new byte[32]))
				.averageValue(new byte[128]).build();

		ChronicleMapKeeperOfDate<String, byte[]> mapKeeper = new ChronicleMapKeeperOfDate<>(options);

		ChronicleMap<String, byte[]> acquire = mapKeeper.acquire("2019.10.11");

		while (true) {
			System.out.println(acquire.size());
			ThreadUtil.sleep(2000);
		}

	}

}
