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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.slingerxv.limitart.collections.define.IRankMap;
import org.slingerxv.limitart.funcs.Func;

/**
 * 高频率写入排行结构 主要用于写入频率远远大于读取频率
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class FrequencyWriteRankMap<K, V extends Func<K>> implements IRankMap<K, V> {
	private final TreeSet<V> treeSet;
	private final Map<K, V> map;
	private final Comparator<V> comparator;
	private final int capacity;
	private List<V> indexList;
	private boolean modified = false;

	public FrequencyWriteRankMap(Comparator<V> comparator, int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity > 0");
		}
		this.treeSet = new TreeSet<>(comparator);
		this.map = new HashMap<>(capacity);
		this.comparator = Objects.requireNonNull(comparator, "comparator");
		this.capacity = capacity;
	}

	@Override
	public synchronized void clear() {
		treeSet.clear();
		map.clear();
		indexList = null;
		modified = true;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public int getIndex(K key) {
		if (!this.map.containsKey(key)) {
			return -1;
		}
		V v = map.get(key);
		checkModified();
		return Collections.binarySearch(indexList, v, this.comparator);
	}

	@Override
	public List<V> getAll() {
		checkModified();
		return new ArrayList<>(indexList);
	}

	@Override
	public List<V> getRange(int startIndex, int endIndex) {
		List<V> temp = new ArrayList<>();
		int size = size();
		int start = startIndex;
		int end = endIndex;
		if (size == 0) {
			return temp;
		}
		if (start < 0) {
			start = 0;
		}
		if (end < start) {
			end = start;
		}
		if (end >= size) {
			end = size - 1;
		}
		if (start == end) {
			V at = getAt(start);
			if (at != null) {
				temp.add(at);
			}
			return temp;
		}
		checkModified();
		return indexList.subList(start, end);
	}

	@Override
	public V getAt(int at) {
		int size = size();
		int index = at;
		if (size == 0) {
			return null;
		}
		if (index < 0) {
			index = 0;
		}
		if (index >= size) {
			index = size - 1;
		}
		if (index == 0) {
			return treeSet.first();
		}
		if (index == size() - 1) {
			return treeSet.last();
		}
		checkModified();
		return indexList.get(index);
	}

	@Override
	public String toString() {
		return treeSet.toString();
	}

	private synchronized void checkModified() {
		if (modified) {
			modified = false;
			indexList = new ArrayList<>(treeSet);
		}
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
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public synchronized V put(K key, V value) {
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(value, "value");
		if (map.containsKey(key)) {
			V obj = map.get(key);
			// 比较新数据与老数据大小
			if (comparator.compare(value, obj) == 0) {
				return null;
			}
			// 因为防止老对象被更改值，所以要删除一次
			treeSet.remove(obj);
		}
		map.put(key, value);
		treeSet.add(value);
		modified = true;
		// 清理排行最后的数据
		while (map.size() > capacity) {
			V pollLast = treeSet.pollLast();
			map.remove(pollLast.run());
		}
		return value;
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Entry<? extends K, ? extends V> entry : Objects.requireNonNull(map, "map").entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Set<K> keySet() {
		return null;
	}

	@Override
	public Collection<V> values() {
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return null;
	}
}
