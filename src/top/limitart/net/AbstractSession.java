package top.limitart.net;

import top.limitart.collections.ConstraintConcurrentMap;
import top.limitart.collections.ConstraintMap;

/**
 * 抽象会话
 *
 * @author hank
 * @version 2018/10/8 0008 17:07
 */
public abstract class AbstractSession<B, T> implements Session<B, T> {
    private final ConstraintMap<Integer> params = new ConstraintConcurrentMap<>();

    @Override
    public void writeNow(B buf) {
        writeNow(buf, null);
    }

    /**
     * 关闭会话
     */
    @Override
    public void close() {
        close(null);
    }


    /**
     * 获取自定义参数列表
     *
     * @return the params
     */
    public ConstraintMap<Integer> params() {
        return params;
    }

    @Override
    public T res() {
        return thread();
    }
}
