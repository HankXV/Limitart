package org.slingerxv.limitart.collections;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.funcs.Test2;
import org.slingerxv.limitart.funcs.Tests;

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
	private Proc2<Object, V> onRemove;
	private Test2<Object, V> canRemoveWithoutLRU;

	public LRUHashMap(int cacheSize) {
		super((int) Math.ceil(cacheSize / 0.75f) + 2, 0.75f, true);
		this.cacheSize = cacheSize;
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		if ((size() > this.cacheSize) || Tests.invoke(canRemoveWithoutLRU, eldest.getKey(), eldest.getValue())) {
			Procs.invoke(onRemove, eldest.getKey(), eldest.getValue());
			return true;
		}
		return false;
	}

	public LRUHashMap<K, V> onRemove(Proc2<Object, V> func) {
		this.onRemove = func;
		return this;
	}

	public LRUHashMap<K, V> canRemoveWithoutLRU(Test2<Object, V> func) {
		this.canRemoveWithoutLRU = func;
		return this;
	}

	@Override
	public void clear() {
		for (Entry<K, V> entry : super.entrySet()) {
			Procs.invoke(onRemove, entry.getKey(), entry.getValue());
		}
		super.clear();
	}

	@Override
	public V remove(Object key) {
		V remove = super.remove(key);
		if (remove != null) {
			Procs.invoke(onRemove, key, remove);
		}
		return remove;
	}
}
