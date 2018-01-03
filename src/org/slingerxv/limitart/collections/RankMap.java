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

import org.slingerxv.limitart.base.Func;

import java.util.List;


/**
 * 顺序Map接口
 *
 * @param <K>
 * @param <V>
 */
public interface RankMap<K, V extends Func<K>> {
    /**
     * 获取值
     *
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 放入值
     *
     * @param key
     * @param value
     * @return
     */
    V put(K key, V value);

    /**
     * 删除值
     *
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * 集合大小
     *
     * @return
     */
    int size();

    /**
     * 找到此Key在排行榜的名次
     *
     * @param key
     * @return
     */
    int getIndex(K key);

    /**
     * 获取一个范围的数据
     *
     * @param start 开始索引(包含边界)
     * @param end   结束索引(包含边界)
     * @return
     */
    List<V> getRange(int start, int end);

    /**
     * 获取所有
     *
     * @return
     */
    List<V> getAll();

    /**
     * 获取指定位置的元数
     *
     * @param index
     * @return
     */
    V getAt(int index);

    /**
     * 清空
     */
    void clear();

}