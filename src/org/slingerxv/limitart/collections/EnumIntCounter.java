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
import org.slingerxv.limitart.util.EnumUtil;
import org.slingerxv.limitart.util.GameMathUtil;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 通过枚举的序列(ordinal)来计数
 *
 * @author hank
 */
@ThreadUnsafe
public class EnumIntCounter<E extends Enum<E>> implements Iterable<Integer> {
    private int[] counts;

    public static <E extends Enum<E>> EnumIntCounter<E> create(Class<E> enumClass) {
        return new EnumIntCounter<>(enumClass);
    }

    public EnumIntCounter(Class<E> enumClass) {
        counts = new int[EnumUtil.length(enumClass)];
    }

    /**
     * 大小
     *
     * @return
     */
    public int size() {
        return counts.length;
    }

    /**
     * 总计数
     *
     * @return
     */
    public int sum() {
        int sum = 0;
        for (int count : counts) {
            sum += count;
        }
        return sum;
    }

    /**
     * 获取计数
     *
     * @param e
     * @return
     */
    public int getCount(E e) {
        return getCount(e.ordinal());
    }

    private int getCount(int ordinal) {
        if (ordinal > counts.length - 1 || ordinal < 0) {
            return 0;
        }
        return counts[ordinal];
    }

    /**
     * 放置计数
     *
     * @param e
     * @param value
     * @return
     */
    public int putCount(E e, int value) {
        return putCount(e.ordinal(), value);
    }

    private int putCount(int ordinal, int value) {
        // 容错
        if (ordinal > counts.length - 1) {
            int[] temp = new int[ordinal + 1];
            System.arraycopy(counts, 0, temp, 0, counts.length);
            counts = temp;
        }
        int old = counts[ordinal];
        counts[ordinal] = value;
        return old;
    }

    /**
     * 重置单个计数为0
     *
     * @param e
     */
    public void zero(E e) {
        putCount(e, 0);
    }

    /**
     * 全部重置为0
     */
    public void zero() {
        counts = new int[counts.length];
    }

    /**
     * 全部重置为0
     */
    public void reset() {
        zero();
    }

    /**
     * 增加1并获取
     *
     * @param key
     * @return
     */
    public int incrementAndGet(E key) {
        return addAndGet(key, 1);
    }

    /**
     * 减1并获取
     *
     * @param key
     * @return
     */
    public int decrementAndGet(E key) {
        return addAndGet(key, -1);
    }

    /**
     * 增加并获取
     *
     * @param key
     * @param delta
     * @return
     */
    public int addAndGet(E key, int delta) {
        int result = GameMathUtil.safeAdd(getCount(key), delta);
        putCount(key, result);
        return result;
    }

    /**
     * 获取并加1
     *
     * @param key
     * @return
     */
    public int getAndIncrement(E key) {
        return getAndAdd(key, 1);
    }

    /**
     * 获取并减1
     *
     * @param key
     * @return
     */
    public int getAndDecrement(E key) {
        return getAndAdd(key, -1);
    }

    /**
     * 获取并增加
     *
     * @param key
     * @param delta
     * @return
     */
    public int getAndAdd(E key, int delta) {
        int old = getCount(key);
        putCount(key, GameMathUtil.safeAdd(old, delta));
        return old;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntArrayIterator(this.counts);
    }

    @Override
    public String toString() {
        return Arrays.toString(this.counts);
    }
}
