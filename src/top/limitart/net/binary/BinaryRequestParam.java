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
package top.limitart.net.binary;


import io.netty.channel.EventLoop;
import top.limitart.base.Conditions;
import top.limitart.base.NotNull;
import top.limitart.mapping.RequestContext;
import top.limitart.net.Session;

/**
 * 消息处理方法参数(使用虚拟类的原因是可能需要通过这个参数来区分消息的类型或者添加额外的变量，比如玩家实体)
 *
 * @author hank
 */
public abstract class BinaryRequestParam extends RequestContext<BinaryMessage> {
    private final Session<BinaryMessage, EventLoop> session;

    public BinaryRequestParam(@NotNull Session session, @NotNull BinaryMessage msg) {
        super(msg);
        Conditions.notNull(session, "session");
        Conditions.notNull(msg, "msg");
        this.session = session;
    }


    public @NotNull
    Session<BinaryMessage, EventLoop> session() {
        return this.session;
    }
}
