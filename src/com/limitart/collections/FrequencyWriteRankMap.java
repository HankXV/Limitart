package com.limitart.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.limitart.collections.define.IRankMap;
import com.limitart.collections.define.IRankObj;

/**
 * 高频率写入排行结构 主要用于写入频率远远大于读取频率
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class FrequencyWriteRankMap<K, V extends IRankObj<K>> implements IRankMap<K, V> {
	private final TreeSet<V> treeSet;
	private final Map<K, V> map;
	private final Comparator<V> comparator;
	private final int capacity;
	private List<V> indexList;
	private boolean modified = false;

	public FrequencyWriteRankMap(Comparator<V> comparator, int capacity) {
		this.treeSet = new TreeSet<>(comparator);
		this.map = new HashMap<>(capacity);
		this.comparator = comparator;
		this.capacity = capacity;
	}

	@Override
	public synchronized void put(K key, V value) {
		if (map.containsKey(key)) {
			V obj = map.get(key);
			// 比较新数据与老数据大小
			if (comparator.compare(value, obj) == 0) {
				return;
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
			map.remove(pollLast.key());
		}
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
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
	public List<V> getRange(int start, int end) {
		List<V> temp = new ArrayList<>();
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
		checkModified();
		return indexList.subList(start, end);
	}

	@Override
	public V getAt(int index) {
		int size = size();
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

}
