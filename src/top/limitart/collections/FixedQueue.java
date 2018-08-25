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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 有界队列(如超过队列新数据排除老数据)
 *
 * @author hank
 * @version 2018/6/7 0007 22:30
 */
public class FixedQueue<E> implements Queue<E> {
    private static final int DEFAULT_CAPACITY = 50;
    private int capacity = DEFAULT_CAPACITY;
    private Queue<E> instance = new LinkedList();

    public FixedQueue(Queue<E> origin, int capacity) {
        Conditions.args(capacity > 0, "capacity must > 0");
        this.capacity = capacity;
        this.instance = origin;
    }

    public FixedQueue() {
    }

    public int size() {
        return instance.size();
    }

    public boolean isEmpty() {
        return instance.isEmpty();
    }

    public boolean contains(Object o) {
        return instance.contains(o);
    }

    public Iterator<E> iterator() {
        return instance.iterator();
    }

    public Object[] toArray() {
        return instance.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return instance.toArray(a);
    }

    public boolean add(E e) {
        return offer(e);
    }

    public boolean remove(Object o) {
        return instance.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return instance.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean flag = instance.addAll(c);
        checkSize();
        return flag;
    }

    public boolean removeAll(Collection<?> c) {
        return instance.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return instance.retainAll(c);
    }

    public void clear() {
        instance.clear();
    }

    public boolean offer(E e) {
        boolean flag = instance.offer(e);
        checkSize();
        return flag;
    }

    public E remove() {
        return instance.remove();
    }

    public E poll() {
        return instance.poll();
    }

    public E element() {
        return instance.element();
    }

    public E peek() {
        return instance.peek();
    }

    private void checkSize() {
        while (size() > capacity) {
            poll();
        }
    }

    public Queue<E> getOrigin() {
        return instance;
    }
}
