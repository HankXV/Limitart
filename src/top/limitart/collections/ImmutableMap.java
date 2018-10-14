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
package top.limitart.collections;

import top.limitart.base.Conditions;
import top.limitart.base.NotNull;
import top.limitart.base.Test2;
import top.limitart.base.ThreadSafe;

import java.util.*;


/**
 * 不可变Map TODO 实现MAP接口
 *
 * @author hank
 */
@ThreadSafe
public class ImmutableMap<K, V> implements Map<K, V> {
    private final Map<K, V> map;

    public static <K, V> ImmutableMap<K, V> of(@NotNull Map<K, V> map) {
        Conditions.notNull(map);
        ImmutableMap<K, V> il = new ImmutableMap<>();
        il.map.putAll(map);
        return il;
    }

    private ImmutableMap() {
        map = new HashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }


    @Override
    public V get(Object key) {
        Conditions.notNull(key);
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<>(map.keySet());
    }

    @Override
    public Collection<V> values() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<>(map.entrySet());
    }

    @Override
    public boolean containsKey(Object key) {
        Conditions.notNull(key);
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Conditions.notNull(value);
        return map.containsValue(value);
    }


    public void forEach(Test2<K, V> test) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (!test.test(entry.getKey(), entry.getValue())) {
                break;
            }
        }
    }

    public Map<K, V> copy() {
        return new HashMap<>(map);
    }
}
