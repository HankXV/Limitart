package com.limitart.collections;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

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
	private LRUListener listener = null;

	public LRUHashMap(int size) {
		this.cacheSize = size;
		cacheMap = new LinkedHashMap<K, V>((int) Math.ceil(cacheSize / 0.75f) + 2, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<K, V> eldest) {
				boolean canRemove = false;
				if (size() > cacheSize) {
					canRemove = true;
				} else if (listener != null && listener.canRemoveWithoutLRU(eldest.getKey(), eldest.getValue())) {
					canRemove = true;
				}
				if (canRemove && listener != null) {
					listener.onRemove(eldest.getKey(), eldest.getValue());
				}
				return canRemove;
			}
		};
	}

	public LRUHashMap(int size, LRUListener listener) {
		this(size);
		this.listener = listener;
	}

	public V put(K key, V value) {
		return cacheMap.put(key, value);
	}

	public V get(K key) {
		return cacheMap.get(key);
	}

	public V remove(K key) {
		V remove = cacheMap.remove(key);
		if (remove != null && listener != null) {
			listener.onRemove(key, remove);
		}
		return remove;
	}

	public boolean containsKey(K key) {
		return cacheMap.containsKey(key);
	}

	public void clear() {
		if (listener != null) {
			for (Entry<K, V> entry : cacheMap.entrySet()) {
				listener.onRemove(entry.getKey(), entry.getValue());
			}
		}
		cacheMap.clear();
	}

	public int size() {
		return cacheMap.size();
	}

	public interface LRUListener {
		public void onRemove(Object key, Object value);

		public boolean canRemoveWithoutLRU(Object key, Object value);
	}
}
