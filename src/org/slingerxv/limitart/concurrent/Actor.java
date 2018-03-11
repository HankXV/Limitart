package org.slingerxv.limitart.concurrent;

import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.Nullable;
import org.slingerxv.limitart.base.Proc;
import org.slingerxv.limitart.base.Proc1;


/**
 * 资源占有者
 * 弱引用的持有某个对象资源
 * 这种角色同时只能强制性的占用一个资源或不占有
 *
 * @param <T> 资源实体
 * @param <R> 资源区域类型
 * @author hank
 */
public interface Actor<T, R extends Place<T>> {
    /**
     * 离开
     *
     * @param r 资源类型
     * @return
     */
    void leave(@NotNull R r);

    /**
     * 占有
     *
     * @param r 新资源
     * @return
     */
    void join(@NotNull R r, Proc onSuccess, Proc1<Exception> onFail);

    /**
     * 当前占用着在哪个资源域上(获取当前占有的资源)
     *
     * @return 资源实体
     */
    @Nullable
    R where();
}
