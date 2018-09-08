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
package top.limitart.net;

import io.netty.channel.EventLoop;
import top.limitart.concurrent.AbstractThreadActor;

/**
 * Sesson资源占用者，可以用其他对象来持有Netty的网络线程资源
 *
 * @author hank
 */
public class SessionActor extends AbstractThreadActor<EventLoop, Session> {
    @Override
    protected boolean sameThread(EventLoop eventExecutors) {
        return eventExecutors.inEventLoop();
    }
}
