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
import top.limitart.base.Test1;
import top.limitart.base.ThreadSafe;

import java.util.*;


/**
 * 不可变Set
 *
 * @author hank
 */
@ThreadSafe
public class ImmutableSet<E> implements Set<E> {
    private final Set<E> set;

    public static <E> ImmutableSet<E> of(@NotNull Collection<E> collection) {
        Conditions.args(collection != null && !collection.isEmpty());
        ImmutableSet<E> il = new ImmutableSet<>();
        il.set.addAll(collection);
        return il;
    }

    @SafeVarargs
    public static <E> ImmutableSet<E> just(@NotNull E... elements) {
        Conditions.args(elements != null && elements.length > 0);
        ImmutableSet<E> il = new ImmutableSet<>();
        Collections.addAll(il.set, elements);
        return il;
    }

    private ImmutableSet() {
        set = new HashSet<>();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        Conditions.notNull(o);
        return set.contains(o);
    }

    public void forEach(Test1<E> test) {
        for (E e : set) {
            if (!test.test(e)) {
                break;
            }
        }
    }

    public Set<E> copy() {
        return new HashSet<>(set);
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator(this.set.toArray());
    }

    @Override
    public Object[] toArray() {
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return set.toArray(a);
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
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
