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
import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.Nullable;
import org.slingerxv.limitart.base.Proc1;

import java.util.Comparator;
import java.util.List;


/**
 * 有序Map接口(注意不要对V进行除接口外的操作，避免排序错误，V对象值的更新请特别注意引用类型的值)
 *
 * @param <K>
 * @param <V>
 * @author hank
 */
public interface RankMap<K, V extends RankMap.RankObj<K>> {
    static <K, V extends RankObj<K>> RankMap<K, V> create(@NotNull Comparator<V> comparator, int capacity) {
        return RankMapImpl.create(comparator, capacity);
    }

    static <K, V extends RankObj<K>> RankMap<K, V> create(@NotNull Comparator<V> comparator) {
        return RankMapImpl.create(comparator);
    }

    interface RankObj<K> {
        K key();

        int compareKey(K other);
    }

    interface LongRankObj extends RankObj<Long> {

        @Override
        default int compareKey(Long other) {
            return Long.compare(other, key());
        }
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    @Nullable
    V get(@NotNull final K key);

    /**
     * 替换或放入新的值
     *
     * @param value
     * @return
     */
    void replaceOrPut(@NotNull V value);

    /**
     * 是否包含Key
     *
     * @param key
     * @return
     */
    boolean containsKey(@NotNull final K key);

    /**
     * 删除值
     *
     * @param key
     * @return
     */
    V remove(@NotNull final K key);

    /**
     * 更新值
     *
     * @param key
     * @param consumer
     */
    void update(@NotNull final K key, @NotNull final Proc1<V> consumer);

    /**
     * 如果不存在则放入
     *
     * @param value
     */
    void putIfAbsent(@NotNull final V value);

    /**
     * 新增或更新
     *
     * @param key
     * @param consumer
     * @param instance
     */
    void updateOrPut(@NotNull final K key, @NotNull final Proc1<V> consumer, @NotNull final Func<V> instance);

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
    int getIndex(@NotNull final K key);

    /**
     * 获取一个范围的数据
     *
     * @param start 开始索引(包含边界)
     * @param end   结束索引(包含边界)
     * @return
     */
    @NotNull
    List<V> getRange(int start, int end);

    /**
     * 获取所有
     *
     * @return
     */
    @NotNull
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