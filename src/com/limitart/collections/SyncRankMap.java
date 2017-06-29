package com.limitart.collections;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.limitart.collections.define.IRankObj;

/**
 * 排行榜集合
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class SyncRankMap<K, V extends IRankObj<K>> extends RankMap<K, V> {

	private ReentrantLock lock = new ReentrantLock();

	public SyncRankMap(Comparator<V> comparator, int capacity) {
		super(comparator, capacity);
	}

	/**
	 * 放入一个元素
	 * 
	 * @param key
	 * @param value
	 * @return 返回被剔除排行榜的列表
	 */
	@Override
	public void put(K key, V value) {
		lock.lock();
		try {
			super.put(key, value);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 是否包含一个Key
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean containsKey(K key) {
		lock.lock();
		try {
			return super.containsKey(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 找到此Key在排行榜的名次
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public int getIndex(K key) {
		lock.lock();
		try {
			return super.getIndex(key);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取一个范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public List<V> getRange(int start, int end) {
		lock.lock();
		try {
			return super.getRange(start, end);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 获取指定位置的元数
	 * 
	 * @param index
	 * @return
	 */
	@Override
	public V getAt(int index) {
		lock.lock();
		try {
			return super.getAt(index);
		} finally {
			lock.unlock();
		}
	}
}
