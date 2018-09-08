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

import top.limitart.util.GameMathUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;

/**
 * 抽象计数Map
 *
 * @author hank
 * @version 2018/3/1 0001 17:16
 */
public abstract class AbstractLongMap<K> implements LongMap<K> {
    private final Map<K, Long> map;

    protected AbstractLongMap(Map<K, Long> map) {
        this.map = map;
    }

    /**
     * 获取计数
     *
     * @param key
     * @return
     */
    @Override
    public long getCount(K key) {
        return map.getOrDefault(key, 0L);
    }

    /**
     * 放置计数
     *
     * @param key
     * @param newValue
     * @return
     */
    @Override
    public long putCount(K key, long newValue) {
        return getAndUpdate(key, x -> newValue);
    }

    /**
     * 计数总和
     *
     * @return
     */
    @Override
    public long sum() {
        return map.values().stream().mapToLong(Long::longValue).sum();
    }

    /**
     * 加1并获取
     *
     * @param key
     * @return
     */
    @Override
    public long incrementAndGet(K key) {
        return addAndGet(key, 1L);
    }

    /**
     * 减1并获取
     *
     * @param key
     * @return
     */
    @Override
    public long decrementAndGet(K key) {
        return addAndGet(key, -1L);
    }

    /**
     * 增加并获取
     *
     * @param key
     * @param delta
     * @return
     */
    @Override
    public long addAndGet(K key, long delta) {
        return accumulateAndGet(key, delta, GameMathUtil::safeAdd);
    }

    /**
     * 获取并加1
     *
     * @param key
     * @return
     */
    @Override
    public long getAndIncrement(K key) {
        return getAndAdd(key, 1L);
    }

    /**
     * 获取并减1
     *
     * @param key
     * @return
     */
    @Override
    public long getAndDecrement(K key) {
        return getAndAdd(key, -1L);
    }

    /**
     * 获取并增加
     *
     * @param key
     * @param delta
     * @return
     */
    @Override
    public long getAndAdd(K key, long delta) {
        return getAndAccumulate(key, delta, GameMathUtil::safeAdd);
    }

    /**
     * 更新并获取
     *
     * @param key
     * @param updaterFunction
     * @return
     */
    public long updateAndGet(K key, LongUnaryOperator updaterFunction) {
        return map.compute(key, (k, value) -> updaterFunction.applyAsLong((value == null) ? 0L : value));
    }

    /**
     * 获取并更新
     *
     * @param key
     * @param updaterFunction
     * @return
     */
    private long getAndUpdate(K key, LongUnaryOperator updaterFunction) {
        AtomicLong holder = new AtomicLong();
        map.compute(key, (k, value) -> {
            long oldValue = (value == null) ? 0L : value;
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
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * 使用getCount代替
     *
     * @param key
     * @return
     */
    @Deprecated
    @Override
    public Long get(Object key) {
        throw new NotImplementedException();
    }

    /**
     * 使用putCount代替
     *
     * @param key
     * @param value
     * @return
     */
    @Deprecated
    @Override
    public Long put(K key, Long value) {
        throw new NotImplementedException();
    }

    @Override
    public Long remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Long> m) {
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
