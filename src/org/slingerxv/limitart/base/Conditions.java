package org.slingerxv.limitart.base;

/**
 * 条件判定
 *
 * @author hank
 * @version 2017/11/2 0002 20:46
 */
public class Conditions {
    public static <T> T notNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    public static <T> T notNull(T obj, Object info) {
        if (obj == null)
            throw new NullPointerException(info.toString());
        return obj;
    }

    public static void checkArgs(boolean isRight) {
        if (!isRight) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgs(boolean isRight, Object info) {
        if (!isRight) {
            throw new IllegalArgumentException(info.toString());
        }
    }
}
