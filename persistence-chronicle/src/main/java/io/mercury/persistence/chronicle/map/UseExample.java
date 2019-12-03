package io.mercury.persistence.chronicle.map;

import io.mercury.common.env.SysPropertys;
import io.mercury.common.thread.ThreadUtil;
import net.openhft.chronicle.map.ChronicleMap;

public class UseExample {

	public static void main(String[] args) {

		ChronicleMapAttributes<String, byte[]> attributes = ChronicleMapAttributes
				.buildOf(String.class, byte[].class, SysPropertys.USER_HOME, "betting")
				.averageKey("uuid__game__merOrderId______").averageValue(new byte[128]);

		ChronicleMapKeeperOfLocalDate<String, byte[]> mapKeeper = new ChronicleMapKeeperOfLocalDate<>(attributes);

		ChronicleMap<String, byte[]> acquire = mapKeeper.acquire("2019.10.11");
		
		while (true) {
			System.out.println(acquire.size());
			ThreadUtil.sleep(2000);
		}


	}
}
