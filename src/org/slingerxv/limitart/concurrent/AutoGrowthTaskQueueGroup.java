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
import org.slingerxv.limitart.base.NotNull;
import org.slingerxv.limitart.base.ThreadSafe;
import org.slingerxv.limitart.collections.ConcurrentHashSet;
import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自动增长消息线程组
 *
 * @author Hank
 * @see TaskQueueGroup
 * @deprecated 在游戏中实际上不需要很频繁的创建销毁线程，所以这个东西并不适用，而且不优雅
 */
@ThreadSafe
@Deprecated
public class AutoGrowthTaskQueueGroup {
    private static final Logger LOGGER = Loggers.create();
    private final AtomicInteger threadId = new AtomicInteger(0);
    private final Map<Integer, AutoGrowthSegment> threads = new ConcurrentHashMap<>();
    private final int entityCountPerThread;
    private final int coreThreadCount;
    private final int maxThreadCount;
    private final Func1<Integer, TaskQueue> newTaskQueue;

    public AutoGrowthTaskQueueGroup(int entityCountPerThread, int coreThreadCount, int initThreadCount,
                                    int maxThreadCount, @NotNull Func1<Integer, TaskQueue> newTaskQueue) {
        Conditions.notNull(newTaskQueue, "taskQueueFactory");
        this.newTaskQueue = newTaskQueue;
        this.maxThreadCount = maxThreadCount;
        this.entityCountPerThread = entityCountPerThread;
        this.coreThreadCount = Math.min(coreThreadCount, this.maxThreadCount);
        if (initThreadCount > 10) {
            LOGGER.warn("initThreadCount is too large, less than 10 better!");
        }
        if (maxThreadCount > 50) {
            LOGGER.warn("maxThreadCount is too large,less than 50 better!");
        }
        int initCount = Math.min(initThreadCount, this.maxThreadCount);
        LOGGER.info("init,entityCountPerThread:" + this.entityCountPerThread + ",initThreadCount:" + initCount
                + ",coreThreadCount:" + this.coreThreadCount + ",maxThreadCount:" + this.maxThreadCount);
        if (initCount > 0) {
            for (int i = 0; i < initCount; ++i) {
                newGrowthThread();
            }
        }
    }

    /**
     * 注册一个实体
     *
     * @param entity
     * @throws TaskQueueException
     */
    @ThreadSafe
    public synchronized AutoGrowthTaskQueueGroup registerEntity(@NotNull AutoGrowthEntity entity) throws TaskQueueException {
        if (entity.getThreadIndex() > 0) {
            throw new TaskQueueException("entity has already registered!");
        }
        AutoGrowthSegment thread = null;
        if (threads.size() >= maxThreadCount && maxThreadCount > 0) {
            int min = Integer.MAX_VALUE;
            for (AutoGrowthSegment temp : threads.values()) {
                int size = temp.entities.size();
                if (size < min) {
                    min = size;
                    thread = temp;
                }
            }
        } else {
            for (AutoGrowthSegment temp : threads.values()) {
                if (temp.entities.size() < entityCountPerThread) {
                    thread = temp;
                    break;
                }
            }
        }
        if (thread == null) {
            thread = newGrowthThread();
        }
        thread.entities.add(entity);
        entity.setThreadIndex(thread.threadIndex);
        return this;
    }

    /**
     * 为实体添加任务
     *
     * @param entity
     * @param t
     * @throws TaskQueueException
     */
    @ThreadSafe
    public AutoGrowthTaskQueueGroup addCommand(@NotNull AutoGrowthEntity entity, @NotNull Runnable t) throws TaskQueueException {
        findTaskQueue(entity).execute(t);
        return this;
    }

    /**
     * 为实体寻找响应的任务队列
     *
     * @param entity
     * @return
     * @throws TaskQueueException
     */
    public TaskQueue findTaskQueue(@NotNull AutoGrowthEntity entity) throws TaskQueueException {
        if (entity.getThreadIndex() == 0) {
            throw new TaskQueueException("entity does not register!");
        }
        return this.threads.get(entity.getThreadIndex()).thread;
    }

    /**
     * 注销实体
     *
     * @param entity
     * @throws TaskQueueException
     */
    @ThreadSafe
    public synchronized AutoGrowthTaskQueueGroup unregisterEntity(@NotNull AutoGrowthEntity entity) throws TaskQueueException {
        int threadIndex = entity.getThreadIndex();
        if (threadIndex == 0) {
            return this;
        }
        if (!threads.containsKey(threadIndex)) {
            throw new TaskQueueException("thread " + threadIndex + " already destroyed！");
        }
        AutoGrowthSegment thread = threads.get(threadIndex);
        if (!thread.entities.contains(entity)) {
            throw new TaskQueueException("entity in thread " + threadIndex + " already destroyed！");
        }
        // 注销线程引用
        if (!thread.entities.remove(entity)) {
            throw new TaskQueueException("entity in thread " + threadIndex + " destroy failed！");
        }
        entity.setThreadIndex(0);
        LOGGER.info(thread.thread.thread().getName() + " unregistered entity:" + entity);
        if (thread.entities.size() <= 0 && threads.size() > this.coreThreadCount) {
            AutoGrowthSegment remove = threads.remove(threadIndex);
            if (remove != null) {
                remove.thread.shutdown();
            }
        }
        return this;
    }

    private AutoGrowthSegment newGrowthThread() {
        int id = threadId.incrementAndGet();
        AutoGrowthSegment data = new AutoGrowthSegment();
        data.thread = this.newTaskQueue.run(id);
        data.threadIndex = id;
        this.threads.put(data.threadIndex, data);
        return data;
    }

    /**
     * 线程容器
     *
     * @author Hank
     */
    @Deprecated
    private static class AutoGrowthSegment {
        private int threadIndex;
        private TaskQueue thread;
        private final Set<AutoGrowthEntity> entities = new ConcurrentHashSet<>();
    }
}
