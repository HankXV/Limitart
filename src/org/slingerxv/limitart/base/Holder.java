package org.slingerxv.limitart.base;

/**
 * 持有器
 *
 * @author hank
 * @version 2017/12/18 0018 19:38
 */
public class Holder<T> {
    private T t;

    public Holder(T t) {
        this.t = t;
    }

    public Holder() {
    }

    public T get() {
        return this.t;
    }
}
