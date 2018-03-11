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

import java.util.concurrent.Executor;

/**
 * 抽象线程资源占有者
 *
 * @author hank
 */
public abstract class AbstractThreadActor<T extends Executor, R extends Place<T>> extends AbstractActor<T, R> {
    /**
     * 调用线程是否为当前线程
     *
     * @param t
     * @return
     */
    protected abstract boolean sameThread(T t);

    /**
     * 离开(必须在旧资源线程离开)
     *
     * @param oldPlace 旧资源
     */
    @Override
    public void leave(R oldPlace) {
        Conditions.args(sameThread(oldPlace.res()), "must leave when you are there!");
        super.leave(oldPlace);
    }

    /**
     * 占有
     *
     * @param newPlace  新线程
     * @param onSuccess 在新线程执行的成功回调
     * @param onFail    失败回调，原有线程为空则当前线程触发，否则在原有线程触发
     */
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
