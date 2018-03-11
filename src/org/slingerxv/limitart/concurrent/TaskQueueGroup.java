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

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.Func1;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息队列组
 *
 * @author hank
 */
public class TaskQueueGroup {
    private final AtomicInteger curIndex = new AtomicInteger();
    private final TaskQueue[] taskQueues;

    public TaskQueueGroup(String groupName, Func1<String, TaskQueue> taskQueueFactory) {
        this(groupName, Runtime.getRuntime().availableProcessors() * 2, taskQueueFactory);
    }

    public TaskQueueGroup(String groupName, int num, Func1<String, TaskQueue> taskQueueFactory) {
        Conditions.positive(num);
        taskQueues = new TaskQueue[num];
        for (int i = 0; i < taskQueues.length; i++) {
            TaskQueue taskQueue = taskQueueFactory.run(groupName + "-" + i);
            Conditions.notNull(taskQueue);
            taskQueues[i] = taskQueue;
        }
    }

    /**
     * 获取下一个线程(轮流策略)
     *
     * @return
     */
    public TaskQueue next() {
        return taskQueues[Math.abs(curIndex.getAndIncrement()) % taskQueues.length];
    }
}
