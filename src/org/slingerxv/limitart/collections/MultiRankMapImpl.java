package org.slingerxv.limitart.collections;

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.Func;
import org.slingerxv.limitart.base.Proc1;

import java.util.*;

/**
 * 复合排行Map
 *
 * @author hank
 * @version 2018/4/17 0017 16:41
 */
public class MultiRankMapImpl<K, V extends RankMap.RankObj<K>> implements MultiRankMap<K, V> {
    private final Map<K, V> map;
    private final Map<Comparator<V>, List<V>> list;
    private Comparator<V> first;

    @SafeVarargs
    public static <K, V extends RankMap.RankObj<K>> MultiRankMapImpl<K, V> create(Comparator<V>... comparators) {
        return new MultiRankMapImpl<>(comparators);
    }

    @SafeVarargs
    private MultiRankMapImpl(Comparator<V>... comparators) {
        Conditions.args(
                comparators != null && comparators.length > 0, "comparators needed!");
        list = new HashMap<>();
        map = new HashMap<>();
        for (Comparator comparator : comparators) {
            Conditions.args(
                    !list.containsKey(comparator), "comparator duplicated:%s", comparator);
            if (first != null) {
                first = comparator;
            }
            list.put(comparator, new ArrayList<>());
        }
    }

    @Override
    public void clear() {
        map.clear();
        list.values().forEach(List::clear);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void replaceOrPut(V value) {
        Conditions.notNull(value, "value");
        K key = value.key();
        if (map.containsKey(key)) {
            V obj = map.get(key);
            Conditions.args(value != obj, "can not put the same object(same hash):%s", value);
            // 比较新数据与老数据大小
            for (Map.Entry<Comparator<V>, List<V>> entry : list.entrySet()) {
                Comparator<V> comparator = entry.getKey();
                List<V> list = entry.getValue();
                int compare = comparator.compare(value, obj);
                if (compare == 0) {
                    continue;
                }
                // 因为防止老对象被更改值，所以要删除一次
                listRemove(comparator, list, obj);
            }
            map.remove(key);
        }
        putIfAbsent(value);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public V remove(K key) {
        V remove = map.remove(key);
        if (remove != null) {
            list.forEach((c, l) -> listRemove(c, l, remove));
        }
        return remove;
    }

    @Override
    public void update(K key, Proc1<V> consumer) {
        V old = map.get(key);
        Conditions.notNull(old, "key(%s) pufIfAbsent first!", key);
        // 在更新前先找到老值的位置
        list.forEach((c, l) -> listRemove(c, l, old));
        consumer.run(old);
        list.forEach((c, l) -> l.add(binarySearch(c, l, old, true), old));
    }

    @Override
    public void putIfAbsent(V value) {
        Conditions.notNull(value, "value");
        Conditions.args(!map.containsKey(value.key()), "key duplicated:%s", value.key());
        map.put(value.key(), value);
        list.forEach(
                (c, l) -> {
                    int binarySearch = 0;
                    // 这里必须要用列表的size而不是map的size
                    if (l.size() > 0) {
                        binarySearch = binarySearch(c, l, value, true);
                    }
                    l.add(binarySearch, value);
                });
    }

    @Override
    public void updateOrPut(K key, Proc1<V> consumer, Func<V> instance) {
        if (!containsKey(key)) {
            V newInstance = instance.run();
            consumer.run(newInstance);
            putIfAbsent(newInstance);
        } else {
            update(key, consumer);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int getIndex(Comparator<V> comparator, K key) {
        if (!this.map.containsKey(key)) {
            return -1;
        }
        V v = map.get(key);
        return binarySearch(comparator, list.get(comparator), v, false);
    }

    @Override
    public List<V> getRange(Comparator<V> comparator, int startIndex, int endIndex) {
        List<V> vs = list.get(comparator);
        Conditions.notNull(vs, "comparator not exist", comparator);
        List<V> temp = new ArrayList<>();
        int start = startIndex;
        int end = endIndex + 1;
        int size = vs.size();
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
            end = size;
        }
        if (start == end) {
            V at = getAt(comparator, start);
            if (at != null) {
                temp.add(at);
            }
            return temp;
        }
        return vs.subList(start, end);
    }

    @Override
    public List<V> getAll(Comparator<V> comparator) {
        List<V> vs = list.get(comparator);
        Conditions.notNull(vs, "comparator not exist", comparator);
        return new ArrayList<>(vs);
    }

    @Override
    public V getAt(Comparator<V> comparator, int at) {
        List<V> vs = list.get(comparator);
        Conditions.notNull(vs, "comparator not exist", comparator);
        int size = vs.size();
        if (size == 0) {
            return null;
        }
        if (at < 0) {
            return null;
        }
        if (at >= size) {
            return null;
        }
        return vs.get(at);
    }

    private void listRemove(Comparator<V> comparator, List<V> list, V old) {
        int i = binarySearch(comparator, list, old, false);
        V remove = list.remove(i);
        Conditions.args(
                old == remove, "remove obj not equals,comparator or key comparator must be error!");
    }

    private int binarySearch(Comparator<V> comparator, List<V> list, V v, boolean similar) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            V midVal = list.get(mid);
            int cmp = comparator.compare(midVal, v);
            if (cmp == 0) {
                cmp = midVal.compareKey(v.key());
            }
            if (cmp < 0) low = mid + 1;
            else if (cmp > 0) high = mid - 1;
            else return mid;
        }
        if (similar) {
            return low;
        }
        throw new IllegalStateException("can not find pos,maybe change the value without this map???");
    }
}
