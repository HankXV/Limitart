package top.limitart.concurrent;

import top.limitart.base.NotNull;

/**
 * 资源区域
 * 用于资源占有者获取资源的场地
 *
 * @param <T> 所拥有的资源类型
 * @author hank
 */
public interface Place<T> {

    /**
     * 获取资源实体
     *
     * @return
     */
    @NotNull
    T res();
}
