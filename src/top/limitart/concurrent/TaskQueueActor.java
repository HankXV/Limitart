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
import top.limitart.base.Func;
import top.limitart.base.Proc;

import java.util.concurrent.*;

/**
 * 消息队列占用者
 *
 * @param <R> 消息队列资源域
 */
public class TaskQueueActor<R extends Actor.Place<TaskQueue>> extends AbstractThreadActor<TaskQueue, R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueActor.class);
    //同步执行最大阻塞时间
    private static final long SYNC_OVER_TIME = 2000;

    @Override
    public boolean sameThread(TaskQueue taskQueue) {
        return taskQueue.thread() == Thread.currentThread();
    }

    /**
     * 与其他消息队列协作任务(异步)
     * 当前线程不会等待指定线程执行完毕
     *
     * @param another   其他消息队列
     * @param process  其他消息队里执行的任务
     * @param onSuccess 在本队列执行的成功回调
     * @param onFail    在本队列执行的失败回调
     */
    public void onAnotherAsync(R another, Func<Boolean> process, Proc onSuccess, Proc onFail) {
        Conditions.notNull(another, "another");
        Conditions.notNull(process, "proccess");
        Conditions.notNull(onSuccess, "onSuccess");
        Conditions.notNull(onFail, "onFail");
        R where = where();
        Conditions.notNull(where, "no place to hold!");
        where.res().execute(() -> another.res().execute(() -> {
            if (process.run()) {
                where.res().execute(onSuccess::run);
            } else {
                where.res().execute(onFail::run);
            }
        }));
    }

    /**
     * 与其他消息队列协作任务(同步)
     * 保证在响应速度要求低的调用响应线程要求高的线程，阻塞前者以确保运行在前者线程的数据正确性
     *
     * @param another
     * @param process
     * @param onSuccess
     */
    public void onAnotherSync(R another, Func<Boolean> process, Proc onSuccess) {
        onAnotherSync(another, process, onSuccess, null);
    }

    public void onAnotherSync(R another, Func<Boolean> process, Proc onSuccess, Proc onFail) {
        Conditions.notNull(another, "another");
        Conditions.notNull(process, "process");
        Conditions.notNull(onSuccess, "onSuccess");
        R where = where();
        Conditions.notNull(where, "no place to hold!");
        where.res().execute(() -> {
            Future<Boolean> submit = another.res().submit(process::run);
            Boolean result = null;
            try {
                result = submit.get(SYNC_OVER_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.error("task queue " + another.res().thread().getName(), e);
            }
            if (result == null || !result) {
                if (onFail != null) {
                    onFail.run();
                }
            } else {
                onSuccess.run();
            }
        });
    }
}
