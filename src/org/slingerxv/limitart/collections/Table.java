package org.slingerxv.limitart.collections;

import java.util.*;

/**
 * 二维表
 *
 * @author hank
 * @version 2017/12/18 0018 19:34
 */
public interface Table<R, C, V> {
    Collection<V> values();

    V put(R r, C c, V v);

    Map<C, V> row(R r);

    V get(R r, C c);

    V remove(R r, C c);
}
