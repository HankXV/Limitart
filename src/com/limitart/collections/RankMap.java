package com.limitart.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.limitart.collections.define.IRankObj;

public class RankMap<K, V extends IRankObj<K>> {
	private List<V> list;
	private Map<K, V> map;
	private final Comparator<V> comparator;
	private int capacity;

	/**
	 * 排行集合
	 * 
	 * @param comparator
	 * @param limit
	 *            排行最大长度
	 */
	public RankMap(Comparator<V> comparator, int capacity) {
		this.map = new HashMap<>();
		this.comparator = comparator;
		list = new ArrayList<>(capacity);
		this.capacity = capacity;
	}

	/**
	 * 放入一个元素
	 * 
	 * @param key
	 * @param value
	 * @return 返回被剔除排行榜的列表
	 */
	public void put(K key, V value) {
		if (map.containsKey(key)) {
			V obj = map.get(key);
			// 比较新数据与老数据大小
			int compare = comparator.compare(value, obj);
			if (compare == 0) {
				return;
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
		while (map.size() + 1 > this.capacity) {
			V pollLast = list.remove(map.size() - 1);
			map.remove(pollLast.key());
		}
	}

	/**
	 * 是否包含一个Key
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key) {
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
	public int getIndex(K key) {
		if (!this.map.containsKey(key)) {
			return -1;
		}
		V v = map.get(key);
		return binarySearch(v, false);
	}

	/**
	 * 获取一个范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
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
		for (int i = start; i < size && i <= end; ++i) {
			temp.add(list.get(i));
		}
		return temp;
	}

	/**
	 * 获取指定位置的元数
	 * 
	 * @param index
	 * @return
	 */
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
		return list.get(index);
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
}
