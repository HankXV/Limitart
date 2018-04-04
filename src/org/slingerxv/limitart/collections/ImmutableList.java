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
package org.slingerxv.limitart.collections;

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.Test2;
import org.slingerxv.limitart.base.ThreadSafe;

import java.util.*;

/**
 * 不可变列表 TODO 实现List接口
 *
 * @author hank
 */
@ThreadSafe
public class ImmutableList<E> implements Iterable<E> {
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

    public E get(int index) {
        return (E) arrays[Conditions.eleIndex(index, arrays.length)];
    }

    public void forEach(@NotNull Test2<Integer, E> test) {
        for (int i = 0; i < arrays.length; ++i) {
            if (!test.test(i, (E) arrays[i])) {
                break;
            }
        }
    }

    public @NotNull
    E[] toArray() {
        Object[] copy = new Object[arrays.length];
        System.arraycopy(arrays, 0, copy, 0, arrays.length);
        return (E[]) copy;
    }

    public int size() {
        return arrays.length;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator(this.arrays);
    }
}
