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

import top.limitart.base.*;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * LRU非线程安全型Map
 *
 * @param <K>
 * @param <V>
 * @author hank
 */
@ThreadUnsafe
public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
    private transient static final long serialVersionUID = 1L;
    private transient final int cacheSize;
    private transient Proc2<Object, V> onRemove;
    private transient Test2<Object, V> canRemoveWithoutLRU;

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

    /**
     * 当元素被删除时
     *
     * @param func
     * @return
     */
    public LRUHashMap<K, V> onRemove(Proc2<Object, V> func) {
        this.onRemove = func;
        return this;
    }

    /**
     * 在LRU规则外是否可以移除
     *
     * @param func
     * @return
     */
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
