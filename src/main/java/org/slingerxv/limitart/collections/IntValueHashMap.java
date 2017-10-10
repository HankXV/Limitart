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
import java.util.Map;
import java.util.Set;

/**
 * @author hank
 *
 */
public class IntValueHashMap<K> implements IntValueMap<K> {
	private Map<K, Integer> map;

	private IntValueHashMap() {
		this(new HashMap<>());
	}

	protected IntValueHashMap(Map<K, Integer> map) {
		this.map = map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Integer get(Object key) {
		return map.get(key);
	}

	@Override
	public Integer put(K key, Integer value) {
		return map.put(key, value);
	}

	@Override
	public Integer remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Integer> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Integer> values() {
		return map.values();
	}

	@Override
	public Set<Map.Entry<K, Integer>> entrySet() {
		return map.entrySet();
	}

	@Override
	public int getValue(K key) {
		return map.getOrDefault(key, 0);
	}

	@Override
	public int setValue(K key, int value) {
		Integer old = put(key, value);
		return old == null ? 0 : old.intValue();
	}

	@Override
	public int getAndIncrement(K key) {
		return getAndAdd(key, 1);
	}

	@Override
	public int incrementAndGet(K key) {
		return addAndGet(key, 1);
	}

	@Override
	public int getAndDecrement(K key) {
		return getAndAdd(key, -1);
	}

	@Override
	public int decrementAndGet(K key) {
		return addAndGet(key, -1);
	}

	@Override
	public int getAndAdd(K key, int value) {
		int old = getValue(key);
		return setValue(key, old + value);
	}

	@Override
	public int addAndGet(K key, int value) {
		setValue(key, getValue(key) + value);
		return getValue(key);
	}

	@Override
	public int sum() {
		return map.values().stream().mapToInt(Integer::intValue).sum();
	}

	public static <K> IntValueHashMap<K> empty() {
		return new IntValueHashMap<>();
	}
}
