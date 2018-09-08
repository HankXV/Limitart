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

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.*;


/**
 * 消息队列线程
 *
 * @author Hank
 */
public class DisruptorTaskQueue extends AbstractTaskQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorTaskQueue.class);
    private final Disruptor<Holder<Runnable>> disruptor;
    private final SingletonThreadFactory threadFactory;
    private Proc3<Runnable, Throwable, Long> exception;

    public static DisruptorTaskQueue create(String threadName) {
        return new DisruptorTaskQueue(threadName);
    }


    public static DisruptorTaskQueue create(String threadName, int bufferSize) {
        return new DisruptorTaskQueue(threadName, bufferSize);
    }

    private DisruptorTaskQueue(String threadName) {
        this(threadName, 2 << 12);// 4096
    }

    /**
     * 构造函数
     *
     * @param threadName
     * @param bufferSize 指定RingBuffer的大小
     */
    @SuppressWarnings("unchecked")
    private DisruptorTaskQueue(String threadName, int bufferSize) {
        this.threadFactory = new SingletonThreadFactory() {

            @Override
            public String name() {
                return threadName;
            }
        };
        disruptor = new Disruptor<>(Holder::empty, bufferSize, threadFactory, ProducerType.MULTI,
                new BlockingWaitStrategy());
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            try {
                event.get().run();
            } catch (Exception e) {
                LOGGER.error("invoke handler error", e);
            } finally {
                event.set(null);
            }
        });
        // prevent Worker Threads from dying
        disruptor.setDefaultExceptionHandler(new ExceptionHandler<Holder<Runnable>>() {

            @Override
            public void handleEventException(Throwable ex, long sequence, Holder<Runnable> event) {
                LOGGER.error("sequence " + sequence + " error!", ex);
                Procs.invoke(exception, event.get(), ex, sequence);
            }

            @Override
            public void handleOnStartException(final Throwable ex) {
                LOGGER.error("Exception during onStart()", ex);
            }

            @Override
            public void handleOnShutdownException(final Throwable ex) {
                LOGGER.error("Exception during onShutdown()", ex);
            }
        });
        disruptor.start();
        LOGGER.info("thread " + threadFactory.name() + " start!");
    }


    /**
     * 错误处理
     *
     * @param exception
     * @return
     */
    public DisruptorTaskQueue exception(Proc3<Runnable, Throwable, Long> exception) {
        this.exception = exception;
        return this;
    }


    @Override
    public void execute(Runnable runnable) {
        Conditions.notNull(runnable, "command");
        if (thread() == Thread.currentThread()) {
            runnable.run();
        }
        disruptor.getRingBuffer().publishEvent((event, sequence) -> event.set(runnable));
    }

    @Override
    public Thread thread() {
        return threadFactory.thread();
    }

    @Override
    public void shutdown() {
        if (disruptor != null) {
            disruptor.shutdown();
            LOGGER.info("thread " + threadFactory.name() + " stop!");
        }
    }
}
