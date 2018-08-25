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

import java.util.Map;

/**
 * 计数Map
 *
 * @author hank
 * @version 2018/3/1 0001 17:17
 */
public interface LongMap<K> extends Map<K, Long> {
    static <K> LongMap<K> empty() {
        return new HashedLongMap<>();
    }

    /**
     * 获取计数
     *
     * @param key
     * @return
     */
    long getCount(K key);

    /**
     * 设置计数
     *
     * @param key
     * @param newValue
     * @return
     */
    long putCount(K key, long newValue);

    /**
     * 总数
     *
     * @return
     */
    long sum();

    /**
     * 加1并获取
     *
     * @param key
     * @return
     */
    long incrementAndGet(K key);

    /**
     * 减1并获取
     *
     * @param key
     * @return
     */
    long decrementAndGet(K key);

    /**
     * 增加并获取
     *
     * @param key
     * @param delta
     * @return
     */
    long addAndGet(K key, long delta);

    /**
     * 获取并加1
     *
     * @param key
     * @return
     */
    long getAndIncrement(K key);

    /**
     * 获取并减1
     *
     * @param key
     * @return
     */
    long getAndDecrement(K key);

    /**
     * 获取并增加
     *
     * @param key
     * @param delta
     * @return
     */
    long getAndAdd(K key, long delta);
}
