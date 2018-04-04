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
package org.slingerxv.limitart.concurrent;


import java.util.concurrent.*;

/**
 * 任务队列接口
 *
 * @author hank
 */
public interface TaskQueue extends Executor {
    static TaskQueue create(String threadName) {
        return DisruptorTaskQueue.create(threadName);
    }

    ScheduledFuture<?> schedule(Runnable command,
                                long delay, TimeUnit unit);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                           long initialDelay,
                                           long period,
                                           TimeUnit unit);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                              long initialDelay,
                                              long delay,
                                              TimeUnit unit);

    <T> Future<T> submit(Callable<T> task);

    <T> Future<T> submit(Runnable task, T result);

    Future<?> submit(Runnable task);

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 获取队列的线程
     *
     * @return
     */
    Thread thread();
}