package com.limitart.collections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.limitart.collections.define.DefaultRankObj;
import com.limitart.collections.define.IRankMap;
import com.limitart.collections.define.IRankObj;
import com.limitart.util.RandomUtil;

/**
 * 高频率读取排行结构 主要用于读取频率远远大于写入频率
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class FrequencyReadRankMap<K, V extends IRankObj<K>> implements IRankMap<K, V> {
	private List<V> list;
	private Map<K, V> map;
	private final Comparator<V> comparator;
	private int capacity;

	public static void main(String[] args) {
		int count = 10;
		IRankMap<Long, DefaultRankObj> old = new FrequencyReadRankMap<>(DefaultRankObj.COMPARATOR, count);
		long now = System.currentTimeMillis();
		for (long i = 0; i < count; ++i) {
			DefaultRankObj obj = new DefaultRankObj(i, RandomUtil.randomLong(0, count), i, i);
			old.put(i, obj);
		}
		System.out.println("oldMap:" + (System.currentTimeMillis() - now));
		now = System.currentTimeMillis();
		for (long i = 0; i < count; ++i) {
			DefaultRankObj obj = new DefaultRankObj(i, RandomUtil.randomLong(0, count), i, i);
			old.put(i, obj);
		}
		System.out.println("oldMap:" + (System.currentTimeMillis() - now));
		System.out.println(old);
	}

	@Override
	public synchronized void clear() {
		list.clear();
		map.clear();
	}

	public FrequencyReadRankMap(Comparator<V> comparator, int capacity) {
		this.map = new HashMap<>(capacity);
		this.comparator = comparator;
		list = new ArrayList<>(capacity);
		this.capacity = capacity;
	}

	@Override
	public synchronized void put(K key, V value) {
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
		while (map.size() > this.capacity) {
			V pollLast = list.remove(map.size() - 1);
			map.remove(pollLast.key());
		}
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
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
		return list.subList(start, end);
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
