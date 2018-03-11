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
import org.slingerxv.limitart.base.Func;
import org.slingerxv.limitart.base.Proc;
import org.slingerxv.limitart.base.Proc1;

/**
 * 消息队列占用者
 *
 * @param <R> 消息队列资源域
 */
public class TaskQueueActor<R extends Place<TaskQueue>> extends AbstractActor<TaskQueue, R> {
    @Override
    public void leave(R oldPlace) {
        Conditions.sameThread(oldPlace.res().thread(), "must leave when you are there!");
        super.leave(oldPlace);
    }

    @Override
    public void join(R newPlace, Proc onSuccess, Proc1<Exception> onFail) {
        R where = where();
        super.join(newPlace, () -> newPlace.res().execute(() -> onSuccess.run()), where != null ? (e) -> where.res().execute(() -> onFail.run(e)) : onFail);
    }

    /**
     * 与其他消息队列协作任务
     *
     * @param another   其他消息队列
     * @param proccess  其他消息队里执行的任务
     * @param onSuccess 在本队列执行的成功回调
     * @param onFail    在本队列执行的失败回调
     */
    public void onAnother(R another, Func<Boolean> proccess, Proc onSuccess, Proc onFail) {
        Conditions.notNull(another, "another");
        Conditions.notNull(proccess, "proccess");
        Conditions.notNull(onSuccess, "onSuccess");
        Conditions.notNull(onFail, "onFail");
        R where = where();
        Conditions.notNull(where, "no place to hold!");
        another.res().execute(() -> {
            if (proccess.run()) {
                where.res().execute(() -> onSuccess.run());
            } else {
                where.res().execute(() -> onFail.run());
            }
        });
    }
}
