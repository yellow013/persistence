package io.mercury.persistence.json;

import static com.alibaba.fastjson.JSON.toJSONString;

import javax.annotation.Nonnull;

public final class JsonWrapper {

	public static String toJson(@Nonnull Object obj) {
		return toJSONString(obj);
	}

	public static String toJsonWithFormat(@Nonnull Object obj) {
		return toJSONString(obj, true);
	}

}
