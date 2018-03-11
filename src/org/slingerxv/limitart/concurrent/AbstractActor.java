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
import org.slingerxv.limitart.base.Proc;
import org.slingerxv.limitart.base.Proc1;
import org.slingerxv.limitart.base.WeakRefHolder;

/**
 * 抽象资源占有者
 *
 * @author hank
 */
public abstract class AbstractActor<T, R extends Place<T>> implements Actor<T, R> {
    private transient WeakRefHolder<R> weakRefHolder = WeakRefHolder.empty();

    @Override
    public void leave(R oldPlace) {
        Conditions.notNull(oldPlace, "no old place!");
        R where = where();
        Conditions.notNull(where, "no place to hold in current state,so can not leave!");
        Conditions.args(oldPlace == where, "incorrect place to give!old:%s,yours:%s", oldPlace, where);
        weakRefHolder.set(null);
    }

    @Override
    public void join(R newPlace, Proc onSuccess, Proc1<Exception> onFail) {
        Conditions.notNull(newPlace, "no new res!");
        R where = where();
        if (where != null && where != newPlace) {
            onFail.run(new IllegalStateException("already has a place!"));
            return;
        }
        weakRefHolder.set(newPlace);
        onSuccess.run();
    }

    @Override
    public final R where() {
        return weakRefHolder.get();
    }
}
