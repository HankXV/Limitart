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

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.Test2;
import org.slingerxv.limitart.base.ThreadSafe;

import java.util.HashMap;
import java.util.Map;


/**
 * 不可变Map TODO 实现MAP接口
 *
 * @author hank
 */
@ThreadSafe
public class ImmutableMap<K, V> {
    private final Map<K, V> map;

    public static <K, V> ImmutableMap<K, V> of(@NotNull Map<K, V> map) {
        Conditions.args(map != null && !map.isEmpty());
        ImmutableMap<K, V> il = new ImmutableMap<>();
        il.map.putAll(map);
        return il;
    }

    private ImmutableMap() {
        map = new HashMap<>();
    }

    public int size() {
        return map.size();
    }

    public V get(K k) {
        Conditions.notNull(k);
        return map.get(k);
    }


    public boolean containsKey(K k) {
        Conditions.notNull(k);
        return map.containsKey(k);
    }

    public boolean containsValue(V v) {
        Conditions.notNull(v);
        return map.containsValue(v);
    }

    public void forEach(Test2<K, V> test) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (!test.test(entry.getKey(), entry.getValue())) {
                break;
            }
        }
    }

    public Map<K, V> copy() {
        Map<K, V> hashMap = new HashMap<>();
        hashMap.putAll(map);
        return hashMap;
    }
}
