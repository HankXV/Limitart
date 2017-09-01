/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import org.slingerxv.limitart.funcs.Test2;
import org.slingerxv.limitart.util.StringUtil;

/**
 * String键约束型Map
 * 
 * @author hank
 *
 */
public class ConstraintMap<K> {
	private Map<K, Object> map;

	protected ConstraintMap(Map<K, Object> map) {
		this.map = Objects.requireNonNull(map, "map");
	}

	protected ConstraintMap() {
		map = new HashMap<>();
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return map.size();
	}

	public ConstraintMap<K> putByte(K key, byte value) {
		putObj(key, value);
		return this;
	}

	public byte getByte(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Byte.valueOf(getObj(key).toString());
	}

	public ConstraintMap<K> putShort(K key, short value) {
		putObj(key, value);
		return this;
	}

	public short getShort(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Short.valueOf(getObj(key).toString());
	}

	public ConstraintMap<K> putInt(K key, int value) {
		putObj(key, value);
		return this;
	}

	public int getInt(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Integer.valueOf(getObj(key).toString());
	}

	public ConstraintMap<K> putLong(K key, long value) {
		putObj(key, value);
		return this;
	}

	public long getLong(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Long.valueOf(getObj(key).toString());
	}

	public ConstraintMap<K> putFloat(K key, float value) {
		putObj(key, value);
		return this;
	}

	public float getFloat(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Float.valueOf(getObj(key).toString());
	}

	public ConstraintMap<K> putDouble(K key, double value) {
		putObj(key, value);
		return this;
	}

	public double getDouble(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Double.valueOf(getObj(key).toString());
	}

	public ConstraintMap<K> putChar(K key, char value) {
		putObj(key, value);
		return this;
	}

	public char getChar(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return getObj(key);
	}

	public ConstraintMap<K> putBoolean(K key, boolean value) {
		putInt(key, value ? 1 : 0);
		return this;
	}

	public boolean getBoolean(K key) {
		return getInt(key) == 1;
	}

	public ConstraintMap<K> putString(K key, String value) {
		if (value == null) {
			putObj(key, "");
		} else {
			putObj(key, value);
		}
		return this;
	}

	public String getString(K key) {
		if (!containsKey(key)) {
			return "";
		}
		return (String) map.get(key);
	}

	public ConstraintMap<K> foreach(BiConsumer<? super K, ? super Object> action) {
		map.forEach(action);
		return this;
	}

	public Set<K> keys() {
		return map.keySet();
	}

	public Collection<Object> values() {
		return map.values();
	}

	public String toJSON() {
		return StringUtil.toJSON(this.map);
	}

	public String toJSONWithClassInfo() {
		return StringUtil.toJSONWithClassInfo(this.map);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(Objects.requireNonNull(key, "key"));
	}

	public boolean containsValue(Object value) {
		return map.containsValue(Objects.requireNonNull(value, "value"));
	}

	public ConstraintMap<K> putObj(K key, Object value) {
		map.put(key, value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <V> V getObj(K key) {
		return (V) map.get(key);
	}

	public ConstraintMap<K> remove(K key) {
		map.remove(Objects.requireNonNull(key, "key"));
		return this;
	}

	public ConstraintMap<K> remove(Test2<K, Object> filter) {
		Iterator<Entry<K, Object>> iterator = map.entrySet().iterator();
		for (; iterator.hasNext();) {
			Entry<K, Object> next = iterator.next();
			if (filter.test(next.getKey(), next.getValue())) {
				iterator.remove();
			}
		}
		return this;
	}

	public ConstraintMap<K> putAll(Map<? extends K, ? extends Object> map) {
		this.map.putAll(Objects.requireNonNull(map, "map"));
		return this;
	}

	public ConstraintMap<K> putAll(ConstraintMap<K> map) {
		Objects.requireNonNull(map, "map");
		this.map.putAll(map.map);
		return this;
	}

	public static <K> ConstraintMap<K> fromJSON(String jsonContent) {
		ConstraintMap<K> map = new ConstraintMap<>();
		if (!StringUtil.isEmptyOrNull(jsonContent)) {
			@SuppressWarnings("unchecked")
			Map<K, Object> object = StringUtil.toObject(jsonContent, HashMap.class);
			map.putAll(object);
		}
		return map;
	}	

	public static <K> ConstraintMap<K> empty() {
		return new ConstraintMap<K>();
	}

	@SuppressWarnings("unchecked")
	public static <K> ConstraintMap<K> just(Object... kvs) {
		Objects.requireNonNull(kvs, "kvs");
		ConstraintMap<K> empty = ConstraintMap.empty();
		for (int i = 0; i < kvs.length; i += 2) {
			Objects.requireNonNull(kvs[i], "key");
			Objects.requireNonNull(kvs[i + 1], "value");
			empty.putObj((K) kvs[i], kvs[i + 1]);
		}
		return empty;
	}

	public static <K> ConstraintMap<K> from(Map<? extends K, ? extends Object> map) {
		ConstraintMap<K> empty = ConstraintMap.empty();
		empty.putAll(map);
		return empty;
	}

}
