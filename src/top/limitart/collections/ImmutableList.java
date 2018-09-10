/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.limitart.collections;

import top.limitart.base.Conditions;
import top.limitart.base.NotNull;
import top.limitart.base.Test2;
import top.limitart.base.ThreadSafe;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 不可变列表
 *
 * @author hank
 */
@ThreadSafe
public class ImmutableList<E> implements List<E> {
    private final Object[] arrays;

    public static <E> ImmutableList<E> of(@NotNull List<E> list) {
        Conditions.args(list != null && !list.isEmpty());
        ImmutableList<E> il = new ImmutableList<>(list.size());
        for (int i = 0; i < list.size(); ++i) {
            il.arrays[i] = list.get(i);
        }
        return il;
    }

    @SafeVarargs
    public static <E> ImmutableList<E> just(@NotNull E... elements) {
        Conditions.args(elements != null && elements.length > 0);
        ImmutableList<E> il = new ImmutableList<>(elements.length);
        System.arraycopy(elements, 0, il.arrays, 0, elements.length);
        return il;
    }

    private ImmutableList(int capacity) {
        arrays = new Object[capacity];
    }

    @Override
    public E get(int index) {
        return (E) arrays[Conditions.eleIndex(index, arrays.length)];
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < arrays.length; ++i) {
            if (o.equals(arrays[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = arrays.length - 1; i >= 0; --i) {
            if (o.equals(arrays[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public void forEach(@NotNull Test2<Integer, E> test) {
        for (int i = 0; i < arrays.length; ++i) {
            if (!test.test(i, (E) arrays[i])) {
                break;
            }
        }
    }

    @Override
    public @NotNull
    E[] toArray() {
        Object[] copy = new Object[arrays.length];
        System.arraycopy(arrays, 0, copy, 0, arrays.length);
        return (E[]) copy;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = size();
        T[] copy = a.length >= size ? a :
                (T[]) Array
                        .newInstance(a.getClass().getComponentType(), size);
        System.arraycopy(arrays, 0, copy, 0, arrays.length);
        return copy;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return arrays.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < arrays.length; ++i) {
            if (o.equals(arrays[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator(this.arrays);
    }
}
