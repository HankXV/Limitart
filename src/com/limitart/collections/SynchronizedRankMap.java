package com.limitart.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.limitart.collections.define.IRankObj;

/**
 * 排行榜集合
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class SynchronizedRankMap<K, V extends IRankObj<K>> {
	private TreeSet<V> treeSet;
	private HashMap<K, V> map;
	private final Comparator<V> comparator;
	private boolean modified = false;
	private List<V> indexList;
	private final int limit;

	/**
	 * 排行集合
	 * 
	 * @param comparator
	 * @param limit
	 *            排行最大长度
	 */
	public SynchronizedRankMap(Comparator<V> comparator, int limit) {
		this.treeSet = new TreeSet<>(comparator);
		this.map = new HashMap<>();
		this.comparator = comparator;
		this.limit = limit;
	}

	/**
	 * 放入一个元素
	 * 
	 * @param key
	 * @param value
	 * @return 返回被剔除排行榜的列表
	 */
	public synchronized List<V> put(K key, V value) {
		List<V> removeList = new ArrayList<>();
		if (map.containsKey(key)) {
			V obj = map.get(key);
			// 比较新数据与老数据大小
			int compare = comparator.compare(value, obj);
			if (compare == 0) {
				return removeList;
			}
			// 因为防止老对象被更改值，所以要删除一次
			treeSet.remove(obj);
		}
		map.put(key, value);
		treeSet.add(value);
		modified = true;
		// 清理排行最后的数据
		while (map.size() > limit) {
			V pollLast = treeSet.pollLast();
			map.remove(pollLast.key());
			removeList.add(pollLast);
		}
		return removeList;
	}

	/**
	 * 是否包含一个Key
	 * 
	 * @param key
	 * @return
	 */
	public synchronized boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public int size() {
		return map.size();
	}

	/**
	 * 找到此Key在排行榜的名次
	 * 
	 * @param key
	 * @return
	 */
	public synchronized int getIndex(K key) {
		if (!this.map.containsKey(key)) {
			return -1;
		}
		V v = map.get(key);
		checkModified();
		return Collections.binarySearch(indexList, v, this.comparator);
	}

	/**
	 * 获取一个范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public synchronized List<V> getRange(int start, int end) {
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
		for (int i = start; i < indexList.size() && i <= end; ++i) {
			temp.add(indexList.get(i));
		}
		return temp;
	}

	/**
	 * 获取指定位置的元数
	 * 
	 * @param index
	 * @return
	 */
	public synchronized V getAt(int index) {
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

	private void checkModified() {
		if (modified) {
			modified = false;
			indexList = new ArrayList<>(treeSet);
		}
	}

	@Override
	public String toString() {
		return treeSet.toString();
	}
}
