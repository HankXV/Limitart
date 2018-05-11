package org.slingerxv.limitart.collections;

import com.sun.istack.internal.NotNull;
import org.slingerxv.limitart.base.Func;
import org.slingerxv.limitart.base.Proc1;

import java.util.Comparator;
import java.util.List;

/**
 * 复合排行Map
 *
 * @author hank
 * @version 2018/1/3 0003 13:50
 */
public interface MultiRankMap<K, V extends RankMap.RankObj<K>> {
    static <K, V extends RankMap.RankObj<K>> MultiRankMap<K, V> create(Comparator... comparators) {
        return MultiRankMapImpl.create(comparators);
    }

    /**
     * 清空
     */
    void clear();

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    V get(K key);

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
    boolean containsKey(K key);

    /**
     * 删除值
     *
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * 更新或插入
     *
     * @param key
     * @param consumer
     */
    void update(final K key, final Proc1<V> consumer);

    /**
     * 如果不存在则放入
     *
     * @param value
     */
    void putIfAbsent(final V value);

    /**
     * 新增或更新
     *
     * @param key
     * @param consumer
     * @param instance
     */
    void updateOrPut(final K key, final Proc1<V> consumer, final Func<V> instance);

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
    int getIndex(Comparator<V> comparator, K key);

    /**
     * 获取一个范围的数据
     *
     * @param start 开始索引(包含边界)
     * @param end   结束索引(包含边界)
     * @return
     */
    List<V> getRange(Comparator<V> comparator, int start, int end);

    /**
     * 获取所有
     *
     * @return
     */
    List<V> getAll(Comparator<V> comparator);

    /**
     * 获取指定位置的元数
     *
     * @param index
     * @return
     */
    V getAt(Comparator<V> comparator, int index);
}
