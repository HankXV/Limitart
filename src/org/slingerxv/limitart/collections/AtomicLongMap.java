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


import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.ThreadSafe;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/**
 * 线程安全计数Map
 *
 * @param <K>
 * @author hank
 */
@ThreadSafe
public final class AtomicLongMap<K> implements Map<K, Long>, Serializable {
    private static final long serialVersionUID = 1L;
    private final ConcurrentHashMap<K, Long> map = new ConcurrentHashMap<>();

    public long getCount(@NotNull K key) {
        return map.getOrDefault(key, 0L);
    }

    public long putCount(@NotNull K key, long newValue) {
        return getAndUpdate(key, x -> newValue);
    }

    public long sum() {
        return map.values().stream().mapToLong(Long::longValue).sum();
    }

    public long incrementAndGet(@NotNull K key) {
        return addAndGet(key, 1);
    }

    public long decrementAndGet(@NotNull K key) {
        return addAndGet(key, -1);
    }

    public long addAndGet(@NotNull K key, long delta) {
        return accumulateAndGet(key, delta, Long::sum);
    }

    public long getAndIncrement(@NotNull K key) {
        return getAndAdd(key, 1);
    }

    public long getAndDecrement(@NotNull K key) {
        return getAndAdd(key, -1);
    }

    public long getAndAdd(@NotNull K key, long delta) {
        return getAndAccumulate(key, delta, Long::sum);
    }

    public long updateAndGet(@NotNull K key, @NotNull LongUnaryOperator updaterFunction) {
        return map.compute(key, (k, value) -> updaterFunction.applyAsLong((value == null) ? 0L : value));
    }

    private long getAndUpdate(K key, LongUnaryOperator updaterFunction) {
        AtomicLong holder = new AtomicLong();
        map.compute(key, (k, value) -> {
            int oldValue = (value == null) ? 0 : value.intValue();
            holder.set(oldValue);
            return updaterFunction.applyAsLong(oldValue);
        });
        return holder.get();
    }

    private long accumulateAndGet(K key, long x, LongBinaryOperator accumulatorFunction) {
        return updateAndGet(key, oldValue -> accumulatorFunction.applyAsLong(oldValue, x));
    }

    private long getAndAccumulate(K key, long x, LongBinaryOperator accumulatorFunction) {
        return getAndUpdate(key, oldValue -> accumulatorFunction.applyAsLong(oldValue, x));
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
    public boolean containsKey(@NotNull Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(@NotNull Object value) {
        return map.containsValue(value);
    }

    @Override
    @Deprecated
    public Long get(@NotNull Object key) {
        throw new NotImplementedException();
    }

    @Override
    @Deprecated
    public Long put(@NotNull K key, @NotNull Long value) {
        throw new NotImplementedException();
    }

    @Override
    public Long remove(@NotNull Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends Long> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Long> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, Long>> entrySet() {
        return map.entrySet();
    }
}