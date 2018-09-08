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
public interface IntMap<K> extends Map<K, Integer> {
    static <K> IntMap<K> empty() {
        return new HashedIntMap<>();
    }

    /**
     * 获取计数
     *
     * @param key
     * @return
     */
    int getCount(K key);

    /**
     * 设置计数
     *
     * @param key
     * @param newValue
     * @return
     */
    int putCount(K key, int newValue);

    /**
     * 总数
     *
     * @return
     */
    int sum();

    /**
     * 加1并获取
     *
     * @param key
     * @return
     */
    int incrementAndGet(K key);

    /**
     * 减1并获取
     *
     * @param key
     * @return
     */
    int decrementAndGet(K key);

    /**
     * 增加并获取
     *
     * @param key
     * @param delta
     * @return
     */
    int addAndGet(K key, int delta);

    /**
     * 获取并加1
     *
     * @param key
     * @return
     */
    int getAndIncrement(K key);

    /**
     * 获取并减1
     *
     * @param key
     * @return
     */
    int getAndDecrement(K key);

    /**
     * 获取并增加
     *
     * @param key
     * @param delta
     * @return
     */
    int getAndAdd(K key, int delta);
}
