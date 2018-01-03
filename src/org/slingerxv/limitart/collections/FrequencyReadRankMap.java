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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slingerxv.limitart.base.*;

/**
 * 高频率读取排行结构 主要用于读取频率远远大于写入频率
 *
 * @param <K>
 * @param <V>
 * @author hank
 */
@ThreadUnsafe
public class FrequencyReadRankMap<K, V extends Func<K>> implements RankMap<K, V> {
    private List<V> list;
    private Map<K, V> map;
    private final Comparator<V> comparator;
    private int capacity;


    public FrequencyReadRankMap(@NotNull Comparator<V> comparator, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity > 0");
        }
        this.map = new HashMap<>(capacity);
        this.comparator = Conditions.notNull(comparator, "comparator");
        this.list = new ArrayList<>(capacity);
        this.capacity = capacity;
    }

    @Override
    public V get(@NotNull Object key) {
        return map.get(key);
    }

    @Override
    public V put(@NotNull K key, @NotNull V value) {
        Conditions.notNull(key, "key");
        Conditions.notNull(value, "value");
        if (map.containsKey(key)) {
            V obj = map.get(key);
            // 比较新数据与老数据大小
            int compare = comparator.compare(value, obj);
            if (compare == 0) {
                return null;
            }
            // 因为防止老对象被更改值，所以要删除一次
            int binarySearch = binarySearch(obj, false);
            list.remove(binarySearch);
        }
        int binarySearch = 0;
        if (size() > 0) {
            binarySearch = binarySearch(value, true);
        }
        map.put(key, value);
        list.add(binarySearch, value);
        while (map.size() > this.capacity) {
            V pollLast = list.remove(map.size() - 1);
            map.remove(pollLast.run());
        }
        return value;
    }

    @Override
    public V remove(@NotNull K key) {
        V remove = map.remove(key);
        if (remove != null) {
            list.remove(remove);
        }
        return remove;
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
    public int getIndex(@NotNull K key) {
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
            end = size - 1;
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

    private int binarySearch(V v, boolean similar) {
        int low = 0;
        int high = size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            V midVal = list.get(mid);
            int cmp = this.comparator.compare(midVal, v);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid;
        }
        if (similar) {
            return low;
        }
        return -1;
    }
}
