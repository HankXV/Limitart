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
package top.limitart.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Conditions;
import top.limitart.base.Proc2;
import top.limitart.base.Procs;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 阻塞消息队列
 *
 * @author hank
 * @see DisruptorTaskQueue
 */
public class LinkedBlockingTaskQueue extends AbstractTaskQueue implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedBlockingTaskQueue.class);
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final SingletonThreadFactory threadFactory;
    private boolean start = false;
    private Proc2<Runnable, Throwable> exception;

    public static LinkedBlockingTaskQueue create(String threadName) {
        return new LinkedBlockingTaskQueue(threadName);
    }


    private LinkedBlockingTaskQueue(String threadName) {
        threadFactory = new SingletonThreadFactory() {
            @Override
            public String name() {
                return threadName;
            }
        };
        threadFactory.newThread(this);
        threadFactory.thread().start();
    }


    public LinkedBlockingTaskQueue exception(Proc2<Runnable, Throwable> exception) {
        this.exception = exception;
        return this;
    }

    @Override
    public void run() {
        start = true;
        while (start || !queue.isEmpty()) {
            Runnable take = null;
            try {
                take = queue.take();
                take.run();
            } catch (Exception e) {
                LOGGER.error("invoke error", e);
                Procs.invoke(exception, take, e);
            }
        }
    }


    @Override
    public void shutdown() {
        start = false;
    }

    @Override
    public Thread thread() {
        return threadFactory.thread();
    }

    @Override
    public void execute(Runnable command) {
        Conditions.notNull(command, "command");
        if (thread() == Thread.currentThread()) {
            command.run();
        }
        Conditions.args(queue.offer(command), "add command failed, command:%s", command.getClass().getName());
    }
}
