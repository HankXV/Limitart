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

import org.slingerxv.limitart.base.ThreadUnsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 二维表HashMap实现
 *
 * @author hank
 */
@ThreadUnsafe
public class HashedTable<R, C, V> implements Table<R, C, V> {
    private final Map<R, Map<C, V>> maps = new HashMap<>();

    @Override
    public Set<R> keySet() {
        return maps.keySet();
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

    @Override
    public Map<C, V> remove(R r) {
        Map<C, V> row = row(r);
        row.clear();
        return row;
    }

    @Override
    public void clear() {
        maps.clear();
    }
}
