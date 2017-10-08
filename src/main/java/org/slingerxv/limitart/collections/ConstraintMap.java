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
import java.util.Objects;
import java.util.Set;

import org.slingerxv.limitart.funcs.Test2;

/**
 * String键约束型Map
 * 
 * @author hank
 *
 */
public class ConstraintMap<K> implements Map<K, Object> {
	private Map<K, Object> map;

	protected ConstraintMap(Map<K, Object> map) {
		this.map = Objects.requireNonNull(map, "map");
	}

	protected ConstraintMap() {
		map = new HashMap<>();
	}

	/**
	 * 清除
	 */
	@Override
	public void clear() {
		map.clear();
	}

	/**
	 * 大小
	 * 
	 * @return
	 */
	@Override
	public int size() {
		return map.size();
	}

	/**
	 * 放入Byte
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putByte(K key, byte value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取Byte
	 * 
	 * @param key
	 * @return 返回0或其他
	 */
	public byte getByte(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Byte.valueOf(getObj(key).toString());
	}

	/**
	 * 放入Short
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putShort(K key, short value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取Short
	 * 
	 * @param key
	 * @return 返回0或其他
	 */
	public short getShort(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Short.valueOf(getObj(key).toString());
	}

	/**
	 * 放入int
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putInt(K key, int value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取int
	 * 
	 * @param key
	 * @return 0或其他int
	 */
	public int getInt(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return Integer.valueOf(getObj(key).toString());
	}

	/**
	 * 放入long
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putLong(K key, long value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取long
	 * 
	 * @param key
	 * @return 0L或者其他long
	 */
	public long getLong(K key) {
		if (!containsKey(key)) {
			return 0L;
		}
		return Long.valueOf(getObj(key).toString());
	}

	/**
	 * 放入浮点
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putFloat(K key, float value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取浮点
	 * 
	 * @param key
	 * @return 0F或者其他浮点
	 */
	public float getFloat(K key) {
		if (!containsKey(key)) {
			return 0F;
		}
		return Float.valueOf(getObj(key).toString());
	}

	/**
	 * 放入double
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putDouble(K key, double value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取double
	 * 
	 * @param key
	 * @return 0D或者其他double
	 */
	public double getDouble(K key) {
		if (!containsKey(key)) {
			return 0D;
		}
		return Double.valueOf(getObj(key).toString());
	}

	/**
	 * 放入char
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putChar(K key, char value) {
		putObj(key, value);
		return this;
	}

	/**
	 * 获取char
	 * 
	 * @param key
	 * @return 0或其他char
	 */
	public char getChar(K key) {
		if (!containsKey(key)) {
			return 0;
		}
		return getObj(key);
	}

	/**
	 * 写入布尔
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putBoolean(K key, boolean value) {
		putInt(key, value ? 1 : 0);
		return this;
	}

	/**
	 * 获取布尔
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(K key) {
		return getInt(key) == 1;
	}

	/**
	 * 放入字符串
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putString(K key, String value) {
		if (value == null) {
			putObj(key, "");
		} else {
			putObj(key, value);
		}
		return this;
	}

	/**
	 * 获取字符串
	 * 
	 * @param key
	 * @return ""或其他字符串
	 */
	public String getString(K key) {
		if (!containsKey(key)) {
			return "";
		}
		return (String) map.get(key);
	}

	/**
	 * 是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * 是否包含Key
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(Object key) {
		return map.containsKey(Objects.requireNonNull(key, "key"));
	}

	/**
	 * 是否包含值
	 * 
	 * @param value
	 * @return
	 */
	public boolean containsValue(Object value) {
		return map.containsValue(Objects.requireNonNull(value, "value"));
	}

	/**
	 * 放入原始对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public ConstraintMap<K> putObj(K key, Object value) {
		map.put(key, value);
		return this;
	}

	/**
	 * 获取原始对象
	 * 
	 * @param key
	 * @return null或对象
	 */
	@SuppressWarnings("unchecked")
	public <V> V getObj(K key) {
		return (V) map.get(key);
	}

	/**
	 * 通过条件移除
	 * 
	 * @param filter
	 * @return
	 */
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

	/**
	 * 放入一个同类型Map
	 * 
	 * @param map
	 * @return
	 */
	public ConstraintMap<K> putAll(ConstraintMap<K> map) {
		Objects.requireNonNull(map, "map");
		this.map.putAll(map.map);
		return this;
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	@Override
	public Object put(K key, Object value) {
		return map.put(key, value);
	}

	@Override
	public Object remove(Object value) {
		return map.remove(value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Object> m) {
		this.map.putAll(m);
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Set<Entry<K, Object>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}

	/**
	 * 构造一个空的对象
	 * 
	 * @return
	 */
	public static <K> ConstraintMap<K> empty() {
		return new ConstraintMap<K>();
	}

	/**
	 * 通过键值对数组构造
	 * 
	 * @param kvs
	 * @return
	 */
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

	/**
	 * 从一个Map构造出此对象
	 * 
	 * @param map
	 * @return
	 */
	public static <K> ConstraintMap<K> from(Map<? extends K, ? extends Object> map) {
		ConstraintMap<K> empty = ConstraintMap.empty();
		empty.putAll(map);
		return empty;
	}
}
