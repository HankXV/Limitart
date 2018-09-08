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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * 抽象计数Map
 *
 * @author hank
 * @version 2018/3/1 0001 17:16
 */
public abstract class AbstractIntMap<K> implements IntMap<K> {
    private final Map<K, Integer> map;

    protected AbstractIntMap(Map<K, Integer> map) {
        this.map = map;
    }

    /**
     * 获取计数
     *
     * @param key
     * @return
     */
    @Override
    public int getCount(K key) {
        return map.getOrDefault(key, 0);
    }

    /**
     * 放置计数
     *
     * @param key
     * @param newValue
     * @return
     */
    @Override
    public int putCount(K key, int newValue) {
        return getAndUpdate(key, x -> newValue);
    }

    /**
     * 计数总和
     *
     * @return
     */
    @Override
    public int sum() {
        return map.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 加1并获取
     *
     * @param key
     * @return
     */
    @Override
    public int incrementAndGet(K key) {
        return addAndGet(key, 1);
    }

    /**
     * 减1并获取
     *
     * @param key
     * @return
     */
    @Override
    public int decrementAndGet(K key) {
        return addAndGet(key, -1);
    }

    /**
     * 增加并获取
     *
     * @param key
     * @param delta
     * @return
     */
    @Override
    public int addAndGet(K key, int delta) {
        return accumulateAndGet(key, delta, GameMathUtil::safeAdd);
    }

    /**
     * 获取并加1
     *
     * @param key
     * @return
     */
    @Override
    public int getAndIncrement(K key) {
        return getAndAdd(key, 1);
    }

    /**
     * 获取并减1
     *
     * @param key
     * @return
     */
    @Override
    public int getAndDecrement(K key) {
        return getAndAdd(key, -1);
    }

    /**
     * 获取并增加
     *
     * @param key
     * @param delta
     * @return
     */
    @Override
    public int getAndAdd(K key, int delta) {
        return getAndAccumulate(key, delta, GameMathUtil::safeAdd);
    }

    /**
     * 更新并获取
     *
     * @param key
     * @param updaterFunction
     * @return
     */
    public int updateAndGet(K key, IntUnaryOperator updaterFunction) {
        return map.compute(key, (k, value) -> updaterFunction.applyAsInt((value == null) ? 0 : value));
    }

    /**
     * 获取并更新
     *
     * @param key
     * @param updaterFunction
     * @return
     */
    private int getAndUpdate(K key, IntUnaryOperator updaterFunction) {
        AtomicInteger holder = new AtomicInteger();
        map.compute(key, (k, value) -> {
            int oldValue = (value == null) ? 0 : value;
            holder.set(oldValue);
            return updaterFunction.applyAsInt(oldValue);
        });
        return holder.get();
    }

    private int accumulateAndGet(K key, int x, IntBinaryOperator accumulatorFunction) {
        return updateAndGet(key, oldValue -> accumulatorFunction.applyAsInt(oldValue, x));
    }

    private int getAndAccumulate(K key, int x, IntBinaryOperator accumulatorFunction) {
        return getAndUpdate(key, oldValue -> accumulatorFunction.applyAsInt(oldValue, x));
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
    public Integer get(Object key) {
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
    public Integer put(K key, Integer value) {
        throw new NotImplementedException();
    }

    @Override
    public Integer remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Integer> m) {
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
    public Collection<Integer> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, Integer>> entrySet() {
        return map.entrySet();
    }
}
