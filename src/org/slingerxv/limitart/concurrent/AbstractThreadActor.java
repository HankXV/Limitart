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
    public synchronized void leave(R oldPlace) {
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
    public synchronized void join(R newPlace, Proc onSuccess, Proc1<Exception> onFail) {
        R where = where();
        super.join(newPlace, () -> newPlace.res().execute(() -> onSuccess.run()), where != null ? (e) -> where.res().execute(() -> onFail.run(e)) : onFail);
    }
}
