/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.collections;

import org.slingerxv.limitart.base.*;

import java.util.*;

/**
 * 高频率读取排行结构 主要用于读取频率远远大于写入频率
 *
 * @param <K>
 * @param <V>
 * @author hank
 */
@ThreadUnsafe
public class RankMapImpl<K, V extends RankMap.RankObj<K>> implements RankMap<K, V> {
    private final List<V> list;
    private final Map<K, V> map;
    private final Comparator<V> comparator;
    private final int capacity;

    public static <K, V extends RankMap.RankObj<K>> RankMapImpl<K, V> create(@NotNull Comparator<V> comparator, int capacity) {
        return new RankMapImpl(comparator, capacity);
    }

    public static <K, V extends RankMap.RankObj<K>> RankMapImpl<K, V> create(@NotNull Comparator<V> comparator) {
        return new RankMapImpl(comparator, 0);
    }

    private RankMapImpl(@NotNull Comparator<V> comparator, int capacity) {
        this.map = new HashMap<>();
        this.comparator = Conditions.notNull(comparator, "comparator");
        this.list = new ArrayList<>();
        this.capacity = capacity;
    }

    @Override
    public V get(@NotNull K key) {
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
            int compare = comparator.compare(value, obj);
            if (compare == 0) {
                return;
            }
            // 因为防止老对象被更改值，所以要删除一次
            listRemove(obj);
            // 这里删除实际上是个多余的动作，暂时兼容一下下面的接口
            map.remove(key);
        }
        putIfAbsent(value);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public V remove(@NotNull K key) {
        V old = map.remove(key);
        if (old != null) {
            listRemove(old);
        }
        return old;
    }

    @Override
    public void update(K key, Proc1<V> consumer) {
        V old = map.get(key);
        Conditions.notNull(old != null, "key(%s) pufIfAbsent first!", key);
        // 在更新前先找到老值的位置
        listRemove(old);
        consumer.run(old);
        int newIndex = binarySearch(old, true);
        list.add(newIndex, old);
    }

    @Override
    public void putIfAbsent(V value) {
        Conditions.notNull(value, "value");
        Conditions.args(!map.containsKey(value.key()), "key duplicated:%s", value.key());
        int binarySearch = 0;
        if (size() > 0) {
            binarySearch = binarySearch(value, true);
        }
        map.put(value.key(), value);
        list.add(binarySearch, value);
        while (capacity > 0 && map.size() > this.capacity) {
            V pollLast = list.remove(map.size() - 1);
            map.remove(pollLast.key());
        }
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
    public void clear() {
        list.clear();
        map.clear();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public int getIndex(@com.sun.istack.internal.NotNull K key) {
        if (!this.map.containsKey(key)) {
            return -1;
        }
        V v = map.get(key);
        return binarySearch(v, false);
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<>(list);
    }

    @Override
    public List<V> getRange(int startIndex, int endIndex) {
        List<V> temp = new ArrayList<>();
        int start = startIndex;
        int end = endIndex + 1;
        int size = size();
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
            V at = getAt(start);
            if (at != null) {
                temp.add(at);
            }
            return temp;
        }
        return list.subList(start, end);
    }

    @Override
    public V getAt(int at) {
        int size = size();
        if (size == 0) {
            return null;
        }
        if (at < 0) {
            return null;
        }
        if (at >= size) {
            return null;
        }
        return list.get(at);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    private void listRemove(V old) {
        int i = binarySearch(old, false);
        V remove = list.remove(i);
        Conditions.args(
                old == remove, "remove obj not equals,comparator or key comparator must be error!");
    }

    private int binarySearch(V v, boolean similar) {
        int low = 0;
        int high = size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            V midVal = list.get(mid);
            int cmp = this.comparator.compare(midVal, v);
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
