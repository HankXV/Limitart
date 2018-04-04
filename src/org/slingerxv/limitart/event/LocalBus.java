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
package org.slingerxv.limitart.event;

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.Proc1;
import org.slingerxv.limitart.base.ThreadUnsafe;

import java.util.Set;

/**
 * 本地实现(post后直接在post方的线程立马相应)
 *
 * @author hank
 * @version 2018/4/12 0012 21:21
 */
@ThreadUnsafe
public class LocalBus extends AbstractBus {

    @Override
    public <E extends Event> void postEvent(final E event) {
        Set<Proc1> runnables = callbacks.get(event.getClass());
        Conditions.notNull(runnables, "no listeners to event %s", event.getClass().getName());
        runnables.forEach(c -> c.run(event));
    }
}
