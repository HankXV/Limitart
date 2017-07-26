package org.slingerxv.limitart.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements java.io.Serializable {

	private static final long serialVersionUID = -8672117787651310382L;

	private static final Object PRESENT = new Object();

	private final ConcurrentHashMap<E, Object> map;

	public ConcurrentHashSet() {
		map = new ConcurrentHashMap<>();
	}

	public ConcurrentHashSet(int initialCapacity) {
		map = new ConcurrentHashMap<>(initialCapacity);
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean add(E e) {
		return map.put(e, PRESENT) == null;
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	@Override
	public void clear() {
		map.clear();
	}
}