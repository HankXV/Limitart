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
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private int cacheSize;
	private Func2<Object, V, Void> onRemove;
	private Func2<Object, V, Boolean> canRemoveWithoutLRU;

	public LRUHashMap(int cacheSize) {
		super((int) Math.ceil(cacheSize / 0.75f) + 2, 0.75f, true);
		this.cacheSize = cacheSize;
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		boolean canRemove = false;
		if (size() > this.cacheSize) {
			canRemove = true;
		} else if (canRemoveWithoutLRU != null && canRemoveWithoutLRU.run(eldest.getKey(), eldest.getValue())) {
			canRemove = true;
		}
		if (canRemove && onRemove != null) {
			onRemove.run(eldest.getKey(), eldest.getValue());
		}
		return canRemove;
	}

	public LRUHashMap<K, V> onRemove(Func2<Object, V, Void> func) {
		this.onRemove = func;
		return this;
	}

	public LRUHashMap<K, V> canRemoveWithoutLRU(Func2<Object, V, Boolean> func) {
		this.canRemoveWithoutLRU = func;
		return this;
	}

	@Override
	public void clear() {
		if (onRemove != null) {
			for (Entry<K, V> entry : super.entrySet()) {
				onRemove.run(entry.getKey(), entry.getValue());
			}
		}
		super.clear();
	}

	@Override
	public V remove(Object key) {
		V remove = super.remove(key);
		if (remove != null && onRemove != null) {
			onRemove.run(key, remove);
		}
		return remove;
	}
}
