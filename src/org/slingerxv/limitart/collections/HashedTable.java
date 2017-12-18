package org.slingerxv.limitart.collections;


import java.util.*;

public class HashedTable<R, C, V> implements Table<R, C, V> {
    private final Map<R, Map<C, V>> maps = new HashMap<>();

    @Override
    public Collection<V> values() {
        List<V> list = new LinkedList<>();
        for (Map<C, V> map : maps.values()) {
            list.addAll(map.values());
        }
        return list;
    }

    @Override
    public V put(R r, C c, V v) {
        Map<C, V> map = maps.get(r);
        if (map == null) {
            map = new HashMap<>();
            maps.put(r, map);
        }
        return map.put(c, v);
    }

    @Override
    public Map<C, V> row(R r) {
        return maps.getOrDefault(r, new HashMap<>());
    }

    @Override
    public V get(R r, C c) {
        Map<C, V> map = maps.get(r);
        if (map == null) {
            return null;
        }
        return map.get(c);
    }

    @Override
    public V remove(R r, C c) {
        Map<C, V> map = maps.get(r);
        if (map == null) {
            return null;
        }
        return map.remove(c);
    }
}
