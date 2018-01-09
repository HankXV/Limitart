package org.slingerxv.limitart.collections;

import java.util.*;

/**
 * 二维表
 *
 * @author hank
 * @version 2017/12/18 0018 19:34
 */
public interface Table<R, C, V> {
    /**
     * 获取所有元素
     *
     * @return
     */
    Collection<V> values();

    /**
     * 放置元素
     *
     * @param r
     * @param c
     * @param v
     * @return
     */
    V put(R r, C c, V v);

    /**
     * 获取一行
     *
     * @param r
     * @return
     */
    Map<C, V> row(R r);

    /**
     * 获取一个元素
     *
     * @param r
     * @param c
     * @return
     */
    V get(R r, C c);

    /**
     * 移除一个元素
     *
     * @param r
     * @param c
     * @return
     */
    V remove(R r, C c);

    /**
     * 移除一行
     *
     * @param r
     * @return
     */
    Map<C, V> remove(R r);

    /**
     * 清除所有元素
     */
    void clear();
}
