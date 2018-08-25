///*
// * Copyright (c) 2016-present The Limitart Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.slingerxv.limitart.collections;
//
//import Conditions;
//import Func;
//import NotNull;
//import ThreadUnsafe;
//
//import java.util.*;
//
///**
// * 高频率写入排行结构 主要用于写入频率远远大于读取频率
// *
// * @param <K>
// * @param <V>
// * @author hank
// */
//@ThreadUnsafe
//public class FrequencyWriteRankMap<K, V extends Func<K>> implements RankMap<K, V> {
//    private final TreeSet<V> treeSet;
//    private final Map<K, V> map;
//    private final Comparator<V> comparator;
//    private final int capacity;
//    private List<V> indexList;
//    private boolean modified = false;
//
//    public FrequencyWriteRankMap(@NotNull Comparator<V> comparator, int capacity) {
//        Conditions.args(capacity > 0);
//        this.treeSet = new TreeSet<>(comparator);
//        this.map = new HashMap<>(capacity);
//        this.comparator = Conditions.notNull(comparator, "comparator");
//        this.capacity = capacity;
//    }
//
//    @Override
//    public V get(K key) {
//        return map.get(key);
//    }
//
//    @Override
//    public V put(@NotNull K key, @NotNull V value) {
//        Conditions.notNull(key, "key");
//        Conditions.notNull(value, "value");
//        if (map.containsKey(key)) {
//            V obj = map.get(key);
//            // 比较新数据与老数据大小
//            if (comparator.compare(value, obj) == 0) {
//                return null;
//            }
//            // 因为防止老对象被更改值，所以要删除一次
//            treeSet.remove(obj);
//        }
//        map.put(key, value);
//        treeSet.add(value);
//        modified = true;
//        // 清理排行最后的数据
//        while (map.size() > capacity) {
//            V pollLast = treeSet.pollLast();
//            map.remove(Conditions.notNull(pollLast.run()));
//        }
//        return value;
//    }
//
//    @Override
//    public V remove(@NotNull K key) {
//        V remove = map.remove(key);
//        if (remove != null) {
//            treeSet.remove(remove);
//        }
//        return remove;
//    }
//
//    @Override
//    public void clear() {
//        treeSet.clear();
//        map.clear();
//        indexList = null;
//        modified = true;
//    }
//
//    @Override
//    public int size() {
//        return map.size();
//    }
//
//    @Override
//    public int getIndex(K key) {
//        if (!this.map.containsKey(key)) {
//            return -1;
//        }
//        V v = map.get(key);
//        checkModified();
//        return Collections.binarySearch(indexList, v, this.comparator);
//    }
//
//    @Override
//    public List<V> getAll() {
//        checkModified();
//        return new ArrayList<>(indexList);
//    }
//
//    @Override
//    public List<V> getRange(int startIndex, int endIndex) {
//        List<V> temp = new ArrayList<>();
//        int size = size();
//        int start = startIndex;
//        int end = endIndex + 1;
//        if (size == 0) {
//            return temp;
//        }
//        if (start < 0) {
//            start = 0;
//        }
//        if (end < start) {
//            end = start;
//        }
//        if (end >= size) {
//            end = size;
//        }
//        if (start == end) {
//            V at = getAt(start);
//            if (at != null) {
//                temp.add(at);
//            }
//            return temp;
//        }
//        checkModified();
//        return indexList.subList(start, end);
//    }
//
//    @Override
//    public V getAt(int at) {
//        int size = size();
//        if (size == 0) {
//            return null;
//        }
//        if (at < 0) {
//            return null;
//        }
//        if (at >= size) {
//            return null;
//        }
//        if (at == 0) {
//            return treeSet.first();
//        }
//        if (at == size() - 1) {
//            return treeSet.last();
//        }
//        checkModified();
//        return indexList.get(at);
//    }
//
//    @Override
//    public String toString() {
//        return treeSet.toString();
//    }
//
//    private void checkModified() {
//        if (modified) {
//            modified = false;
//            indexList = new ArrayList<>(treeSet);
//        }
//    }
//}
