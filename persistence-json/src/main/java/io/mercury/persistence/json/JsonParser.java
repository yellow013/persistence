package io.mercury.persistence.json;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;

import io.ffreedom.common.collections.ImmutableLists;
import io.ffreedom.common.collections.ImmutableMaps;
import io.ffreedom.common.collections.MutableLists;
import io.ffreedom.common.collections.MutableMaps;

public final class JsonParser {

	public static final <T> T toObject(@Nonnull String json, @Nonnull Class<T> clazz) {
		return parseObject(json, clazz);
	}

	public static final <T> List<T> toList(@Nonnull String json, @Nonnull Class<T> clazz) {
		return parseArray(json, clazz);
	}

	public static final List<Object> toList(@Nonnull String json) {
		return parseArray(json);
	}

	public static final MutableList<Object> toMutableList(@Nonnull String json) {
		return MutableLists.newFastList(
				// JSONArray实现List接口, 转换为MutableList
				parseArray(json));
	}

	public static final ImmutableList<Object> toImmutableList(@Nonnull String json) {
		return ImmutableLists.newList(
				// JSONArray实现List接口, 转换为ImmutableList
				parseArray(json));
	}

	public static final Map<String, Object> toMap(@Nonnull String json) {
		return parseObject(json);
	}

	public static final MutableMap<String, Object> toMutableMap(@Nonnull String json) {
		return MutableMaps.newUnifiedMap(
				// JSONObject实现Map接口, 转换为MutableMap
				parseObject(json));
	}

	public static final ImmutableMap<String, Object> toImmutableMap(@Nonnull String json) {
		return ImmutableMaps.newMap(
				// JSONObject实现Map接口, 转换为ImmutableMap
				parseObject(json));
	}

}
