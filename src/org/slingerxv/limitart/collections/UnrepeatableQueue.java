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

import org.slingerxv.limitart.base.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 不重复队列
 *
 * @author hank
 */
@ThreadSafe
public class UnrepeatableQueue<V> {
    private Set<V> set = new HashSet<>();
    private Queue<V> queue = new LinkedList<>();

    /**
     * 当前队列大小
     *
     * @return
     */
    public int size() {
        return set.size();
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * 清除全部元素
     */
    public synchronized void clear() {
        set.clear();
        queue.clear();
    }

    /**
     * 是否包含元素
     *
     * @param value
     * @return
     */
    public boolean contains(V value) {
        return set.contains(value);
    }

    /**
     * 压入队列
     *
     * @param value
     */
    public synchronized boolean offer(@NotNull V value) {
        if (!contains(value)) {
            set.add(value);
            queue.offer(value);
            return true;
        }
        return false;
    }

    /**
     * 取出一个元素
     *
     * @return
     */
    public synchronized @Nullable
    V poll() {
        V poll = queue.poll();
        if (poll != null) {
            set.remove(poll);
        }
        return poll;
    }

    /**
     * 按指定数量出队列
     *
     * @param pollCount
     * @param proc
     */
    public synchronized void pollTo(int pollCount, @Nullable Proc1<V> proc) {
        V temp;
        for (int count = 0;
             count <= pollCount && (temp = poll()) != null;
             ++count) {
            Procs.invoke(proc, temp);
        }
    }
}