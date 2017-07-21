package org.slingerxv.limitart.collections.define;

import java.util.List;
import java.util.Map;

import org.slingerxv.limitart.funcs.Func;

public interface IRankMap<K, V extends Func<K>> extends Map<K, V> {

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