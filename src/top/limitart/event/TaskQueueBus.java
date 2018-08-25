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
package top.limitart.event;


import top.limitart.base.Proc1;
import top.limitart.concurrent.TaskQueue;

/**
 * 单线程总线
 *
 * @author hank
 * @version 2018/4/12 0012 21:34
 */
public class TaskQueueBus extends LocalBus {
    private final TaskQueue queue;

    public TaskQueueBus(TaskQueue queue) {
        this.queue = queue;
    }

    @Override
    public void postEvent(Event event) {
        queue.execute(() -> super.postEvent(event));
    }

    @Override
    public <E extends Event> void addListener(Class<E> eventType, Proc1<E> callback) {
        queue.execute(() -> super.addListener(eventType, callback));
    }
}
