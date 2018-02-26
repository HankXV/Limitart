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
package org.slingerxv.limitart.pool;

import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;
import org.slingerxv.limitart.util.TimeUtil;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 简单的对象池
 *
 * @author hank
 * @version 2018/2/6 0006 0:33
 */
@ThreadUnsafe
public class SimplePool<T extends Poolable> implements Pool<T> {
    private static final Logger LOGGER = Loggers.create();
    private final Func<T> factory;
    private Queue<T> queue;
    private final int initialSize;
    private final IntCounter getCount;
    private final IntCounter backCount;

    public SimplePool(Func<T> factory, int initialSize) {
        Conditions.args(initialSize > 0);
        this.factory = Conditions.notNull(factory, "factory");
        queue = new LinkedList<>();
        this.initialSize = initialSize;
        getCount = new IntCounter(0);
        backCount = new IntCounter(initialSize);
    }

    @Override
    public @NotNull
    T get() {
        T poll = null;
        try {
            Conditions.notNull(queue, "already closed!");
            poll = queue.poll();
            if (poll == null) {
                poll = factory.run();
            }
            getCount.incrementAndGet();
            backCount.decrementAndGet();
            checkLeak();
            return poll;
        } finally {
            if (poll != null) {
                poll.release();
            }
        }
    }

    @Override
    public void back(T t) {
        Conditions.notNull(queue, "already closed!");
        Conditions.args(queue.offer(t), "give back error:{}", t);
        getCount.decrementAndGet();
        backCount.incrementAndGet();
        checkLeak();
    }

    private void checkLeak() {
        int get = getCount.getCount();
        int back = backCount.getCount();
        int sum = get + back;
        if (Math.abs(sum - initialSize) > initialSize * 25 / 100) {
            if (get > back) {
                LOGGER.warn("check leak:maybe get too fast or not give back?,get:{},:back:{},init:{}", get, back, initialSize);
            } else {
                LOGGER.warn("check leak:maybe give back the object not belong to this pool,get:{},:back:{},init:{}", get, back, initialSize);
            }
        }
    }

    @Override
    public void close() throws Exception {
        queue = null;
    }
}
