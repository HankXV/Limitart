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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slingerxv.limitart.funcs.Func;

/**
 * 高频率读取排行结构 主要用于读取频率远远大于写入频率
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class FrequencyReadRankMap<K, V extends Func<K>> implements IRankMap<K, V> {
	private List<V> list;
	private Map<K, V> map;
	private final Comparator<V> comparator;
	private int capacity;

	@Override
	public synchronized void clear() {
		list.clear();
		map.clear();
	}

	public FrequencyReadRankMap(Comparator<V> comparator, int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity > 0");
		}
		this.map = new HashMap<>(capacity);
		this.comparator = Objects.requireNonNull(comparator, "comparator");
		this.list = new ArrayList<>(capacity);
		this.capacity = capacity;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public int getIndex(K key) {
		if (!this.map.containsKey(key)) {
			return -1;
		}
		V v = map.get(key);
		return binarySearch(v, false);
	}

	@Override
	public List<V> getAll() {
		return new ArrayList<>(list);
	}

	@Override
	public List<V> getRange(int startIndex, int endIndex) {
		List<V> temp = new ArrayList<>();
		int start = startIndex;
		int end = endIndex + 1;
		int size = size();
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
		return list.subList(start, end);
	}

	@Override
	public V getAt(int at) {
		int size = size();
		if (size == 0) {
			return null;
		}
		if (at < 0) {
			return null;
		}
		if (at >= size) {
			return null;
		}
		return list.get(at);
	}

	@Override
	public String toString() {
		return list.toString();
	}

	private int binarySearch(V v, boolean similar) {
		int low = 0;
		int high = size() - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			V midVal = list.get(mid);
			int cmp = this.comparator.compare(midVal, v);
			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid;
		}
		if (similar) {
			return low;
		}
		return -1;
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
			int compare = comparator.compare(value, obj);
			if (compare == 0) {
				return null;
			}
			// 因为防止老对象被更改值，所以要删除一次
			int binarySearch = binarySearch(obj, false);
			list.remove(binarySearch);
		}
		int binarySearch = 0;
		if (size() > 0) {
			binarySearch = binarySearch(value, true);
		}
		map.put(key, value);
		list.add(binarySearch, value);
		while (map.size() > this.capacity) {
			V pollLast = list.remove(map.size() - 1);
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
		Map<? extends K, ? extends V> requireNonNull = Objects.requireNonNull(map, "map");
		for (Entry<? extends K, ? extends V> entry : requireNonNull.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

}
