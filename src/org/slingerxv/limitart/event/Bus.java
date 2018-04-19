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


import org.slingerxv.limitart.base.Proc1;
import org.slingerxv.limitart.concurrent.TaskQueue;

/**
 * 事件总线
 *
 * @author hank
 * @version 2018/4/12 0012 20:59
 */
public interface Bus {
    static Bus create() {
        return new LocalBus();
    }

    static Bus create(TaskQueue queue) {
        return new TaskQueueBus(queue);
    }

    /**
     * 投递事件
     *
     * @param event
     */
    <E extends Event> void postEvent(final E event);

    /**
     * 监听事件
     *
     * @param eventType
     * @param callback
     */
    <E extends Event> void addListener(final Class<E> eventType, final Proc1<E> callback);
}
