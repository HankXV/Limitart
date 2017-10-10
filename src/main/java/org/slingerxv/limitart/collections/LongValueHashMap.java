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
 * @author Hank
 *
 */
public class LongValueHashMap<K> implements LongValueMap<K> {
	private Map<K, Long> map;

	private LongValueHashMap() {
		this(new HashMap<>());
	}

	protected LongValueHashMap(Map<K, Long> map) {
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
	public Long get(Object key) {
		return map.get(key);
	}

	@Override
	public Long put(K key, Long value) {
		return map.put(key, value);
	}

	@Override
	public Long remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Long> m) {
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
	public Collection<Long> values() {
		return map.values();
	}

	@Override
	public Set<Map.Entry<K, Long>> entrySet() {
		return map.entrySet();
	}

	@Override
	public long getValue(K key) {
		return map.getOrDefault(key, 0L);
	}

	@Override
	public long setValue(K key, long value) {
		Long old = put(key, value);
		return old == null ? 0L : old.longValue();
	}

	@Override
	public long getAndIncrement(K key) {
		return getAndAdd(key, 1);
	}

	@Override
	public long incrementAndGet(K key) {
		return addAndGet(key, 1);
	}

	@Override
	public long getAndDecrement(K key) {
		return getAndAdd(key, -1);
	}

	@Override
	public long decrementAndGet(K key) {
		return addAndGet(key, -1);
	}

	@Override
	public long getAndAdd(K key, long value) {
		long old = getValue(key);
		return setValue(key, old + value);
	}

	@Override
	public long addAndGet(K key, long value) {
		setValue(key, getValue(key) + value);
		return getValue(key);
	}

	@Override
	public long sum() {
		return map.values().stream().mapToLong(Long::longValue).sum();
	}

	public static <K> LongValueHashMap<K> empty() {
		return new LongValueHashMap<>();
	}
}
