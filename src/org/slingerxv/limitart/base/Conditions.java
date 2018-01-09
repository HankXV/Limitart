package org.slingerxv.limitart.base;

/**
 * 条件判定
 *
 * @author hank
 * @version 2017/11/2 0002 20:46
 */
public class Conditions {
    /**
     * 检查元素是否为空，否则抛异常
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T notNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    /**
     * 检查元素是否为空，否则抛异常
     *
     * @param obj
     * @param info
     * @param <T>
     * @return
     */
    public static <T> T notNull(T obj, Object info) {
        if (obj == null)
            throw new NullPointerException(info.toString());
        return obj;
    }

    /**
     * 检查参数是否正确，否则抛异常
     *
     * @param isRight
     */
    public static void args(boolean isRight) {
        if (!isRight) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 检查参数是否正确，否则抛异常
     *
     * @param isRight
     * @param info
     */
    public static void args(boolean isRight, Object info) {
        if (!isRight) {
            throw new IllegalArgumentException(info.toString());
        }
    }

    /**
     * 检查数组边界
     *
     * @param index
     * @param size
     */
    public static int eleIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index:" + index + ",size:" + size);
        }
        return index;
    }

    /**
     * 检查数组边界
     *
     * @param index
     * @param size
     * @param info
     */
    public static int eleIndex(int index, int size, Object info) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(info.toString());
        }
        return index;
    }
}
