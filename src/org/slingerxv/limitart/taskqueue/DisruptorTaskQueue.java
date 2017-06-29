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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.util.NamedThreadFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 消息队列线程
 *
 * @author Hank
 */
public class DisruptorTaskQueue<T> implements ITaskQueue<T> {
    private static Logger log = LoggerFactory.getLogger(DisruptorTaskQueue.class);
    private Disruptor<DisruptorTaskQueueEvent> disruptor;
    private NamedThreadFactory threadFactory;
    private Test1<T> intercept;
    private Proc1<T> handle;
    private Proc3<DisruptorTaskQueueEvent, Throwable, Long> exception;

    public DisruptorTaskQueue(String threadName) {
        this(threadName, 2 << 12);// 4096
    }

    /**
     * 构造函数
     *
     * @param threadName
     * @param bufferSize 指定RingBuffer的大小
     */
    @SuppressWarnings("unchecked")
    public DisruptorTaskQueue(String threadName, int bufferSize) {
        this.threadFactory = new NamedThreadFactory() {

            @Override
            public String getThreadName() {
                return threadName;
            }
        };
        disruptor = new Disruptor<>(DisruptorTaskQueueEvent::new, bufferSize, this.threadFactory, ProducerType.MULTI,
                new BlockingWaitStrategy());
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            if (Tests.invoke(DisruptorTaskQueue.this.intercept, event.getMsg())) {
                return;
            }
            try {
                Procs.invoke(DisruptorTaskQueue.this.handle, event.getMsg());
            } catch (Exception e) {
                log.error("invoke handler error", e);
            } finally {
                event.setMsg(null);
            }
        });
        // prevent Worker Threads from dying
        disruptor.setDefaultExceptionHandler(new ExceptionHandler<DisruptorTaskQueueEvent>() {

            @Override
            public void handleEventException(Throwable ex, long sequence, DisruptorTaskQueueEvent event) {
                log.error("sequence " + sequence + " error!", ex);
                Procs.invoke(exception, event, ex, sequence);
            }

            @Override
            public void handleOnStartException(final Throwable ex) {
                log.error("Exception during onStart()", ex);
            }

            @Override
            public void handleOnShutdownException(final Throwable ex) {
                log.error("Exception during onShutdown()", ex);
            }
        });
    }

    /**
     * 消息拦截
     *
     * @param intercept
     * @return
     */
    public ITaskQueue<T> intercept(Test1<T> intercept) {
        this.intercept = intercept;
        return this;
    }

    /**
     * 消息处理
     *
     * @param handle
     * @return
     */
    public ITaskQueue<T> handle(Proc1<T> handle) {
        this.handle = handle;
        return this;
    }

    /**
     * 错误处理
     *
     * @param exception
     * @return
     */
    public ITaskQueue<T> exception(Proc3<DisruptorTaskQueueEvent, Throwable, Long> exception) {
        this.exception = exception;
        return this;
    }

    @Override
    public void startServer() {
        disruptor.start();
        log.info("thread " + threadFactory.getThreadName() + " start!");
    }

    @Override
    public void stopServer() {
        if (disruptor != null) {
            disruptor.shutdown();
            log.info("thread " + threadFactory.getThreadName() + " stop!");
            disruptor = null;
            threadFactory = null;
        }
    }

    @ThreadSafe
    @Override
    public void addCommand(T command) throws TaskQueueException {
        if (this.disruptor == null) {
            throw new TaskQueueException(serverName() + " has not start yet!");
        }
        disruptor.getRingBuffer().publishEvent((event, sequence) -> event.setMsg(command));
    }

    private class DisruptorTaskQueueEvent {
        private T msg;

        public T getMsg() {
            return msg;
        }

        public void setMsg(T msg) {
            this.msg = msg;
        }
    }

    @Override
    public String serverName() {
        return threadFactory.getThreadName();
    }
}