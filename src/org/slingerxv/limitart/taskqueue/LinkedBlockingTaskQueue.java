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
package org.slingerxv.limitart.taskqueue;

import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 阻塞消息队列
 *
 * @param <T>
 * @author hank
 * @see DisruptorTaskQueue
 */
public class LinkedBlockingTaskQueue<T> extends Thread implements TaskQueue<T> {
    private static Logger log = Loggers.create();
    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private boolean start = false;
    private Test1<T> intercept;
    private Proc1<T> handle;
    private Proc2<T, Throwable> exception;

    public static <T> LinkedBlockingTaskQueue<T> create(String threadName) {
        return new LinkedBlockingTaskQueue<>(threadName);
    }

    private LinkedBlockingTaskQueue(String threadName) {
        setName(threadName);
    }

    public LinkedBlockingTaskQueue<T> intercept(Test1<T> intercept) {
        this.intercept = intercept;
        return this;
    }

    public LinkedBlockingTaskQueue<T> handle(Proc1<T> handle) {
        this.handle = handle;
        return this;
    }

    public LinkedBlockingTaskQueue<T> exception(Proc2<T, Throwable> exception) {
        this.exception = exception;
        return this;
    }

    @Override
    public void run() {
        start = true;
        while (start || !queue.isEmpty()) {
            T take = null;
            try {
                take = queue.take();
                if (Tests.invoke(intercept, take)) {
                    continue;
                }
                Procs.invoke(handle, take);
            } catch (Exception e) {
                log.error("invoke error", e);
                Procs.invoke(exception, take, e);
            }
        }
    }

    @ThreadSafe
    @Override
    public void addCommand(T command) {
        Conditions.notNull(command, "command");
        queue.offer(command);
    }

    @Override
    public void stopServer() {
        start = false;
    }

    @Override
    public void startServer() {
        start();
    }

    @Override
    public String serverName() {
        return getName();
    }
}
