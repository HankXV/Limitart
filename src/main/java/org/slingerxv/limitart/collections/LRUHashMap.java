package org.slingerxv.limitart.collections;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slingerxv.limitart.funcs.Func2;

/**
 * LRU非线程安全型Map
 * 
 * @author hank
 *
 * @param <K>
 * @param <V>
 */
public class LRUHashMap<K, V> {
	private int cacheSize;
	private LinkedHashMap<K, V> cacheMap = null;
	private Func2<K, V, Void> onRemove;
	private Func2<K, V, Boolean> canRemoveWithoutLRU;

	public LRUHashMap(int size) {
		this.cacheSize = size;
		cacheMap = new LinkedHashMap<K, V>((int) Math.ceil(cacheSize / 0.75f) + 2, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<K, V> eldest) {
				boolean canRemove = false;
				if (size() > cacheSize) {
					canRemove = true;
				} else if (canRemoveWithoutLRU != null && canRemoveWithoutLRU.run(eldest.getKey(), eldest.getValue())) {
					canRemove = true;
				}
				if (canRemove && onRemove != null) {
					onRemove.run(eldest.getKey(), eldest.getValue());
				}
				return canRemove;
			}
		};
	}

	public LRUHashMap<K, V> onRemove(Func2<K, V, Void> func) {
		this.onRemove = func;
		return this;
	}

	public LRUHashMap<K, V> canRemoveWithoutLRU(Func2<K, V, Boolean> func) {
		this.canRemoveWithoutLRU = func;
		return this;
	}

	public V put(K key, V value) {
		return cacheMap.put(key, value);
	}

	public V get(K key) {
		return cacheMap.get(key);
	}

	public V remove(K key) {
		V remove = cacheMap.remove(key);
		if (remove != null && onRemove != null) {
			onRemove.run(key, remove);
		}
		return remove;
	}

	public boolean containsKey(K key) {
		return cacheMap.containsKey(key);
	}

	public void clear() {
		if (onRemove != null) {
			for (Entry<K, V> entry : cacheMap.entrySet()) {
				onRemove.run(entry.getKey(), entry.getValue());
			}
		}
		cacheMap.clear();
	}

	public int size() {
		return cacheMap.size();
	}
}
