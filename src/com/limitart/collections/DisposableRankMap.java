package com.limitart.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.limitart.collections.define.IRankMap;
import com.limitart.collections.define.IRankObj;

/**
 * 一次性排行，调用一次获得排名排名就永远不会改变
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class DisposableRankMap<K, V extends IRankObj<K>> implements IRankMap<K, V> {
	private final TreeSet<V> treeSet;
	private final Map<K, V> map;
	private final Comparator<V> comparator;
	private final int capacity;
	private List<V> result;

	public DisposableRankMap(Comparator<V> comparator, int capacity) {
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
	@Deprecated
	public int getIndex(K key) {
		return -1;
	}

	@Override
	public List<V> getAll() {
		if (result == null) {
			result = new ArrayList<>(treeSet);
		}
		return result;
	}

	@Override
	public synchronized List<V> getRange(int start, int end) {
		return null;
	}

	@Override
	@Deprecated
	public V getAt(int index) {
		return null;
	}

	@Override
	public String toString() {
		return treeSet.toString();
	}

}
