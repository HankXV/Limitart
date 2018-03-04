package org.slingerxv.limitart.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 数组迭代器
 *
 * @author hank
 * @version 2018/2/12 0012 13:45
 */
public class ArrayIterator<E> implements Iterator<E> {
    private final Object[] array;
    private int curIndex = 0;

    public ArrayIterator(Object[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return this.curIndex < array.length;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more element,please call hasNext() to check");
        }
        return (E) array[this.curIndex++];
    }
}
