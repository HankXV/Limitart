package com.limitart.collections.define;

import java.util.List;

import com.limitart.collections.define.IRankObj;

public interface IRankMap<K, V extends IRankObj<K>> {

	/**
	 * 放入一个元素
	 * 
	 * @param key
	 * @param value
	 */
	void put(K key, V value);

	/**
	 * 是否包含一个Key
	 * 
	 * @param key
	 * @return
	 */
	boolean containsKey(K key);

	/**
	 * 集合大小
	 * 
	 * @return
	 */
	int size();

	/**
	 * 找到此Key在排行榜的名次
	 * 
	 * @param key
	 * @return
	 */
	int getIndex(K key);

	/**
	 * 获取一个范围的数据
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	List<V> getRange(int start, int end);

	List<V> getAll();

	/**
	 * 获取指定位置的元数
	 * 
	 * @param index
	 * @return
	 */
	V getAt(int index);

	void clear();

}