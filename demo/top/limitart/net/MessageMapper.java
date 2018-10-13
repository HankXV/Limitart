/*
 *
 *  * Copyright (c) 2016-present The Limitart Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package top.limitart.net;

import com.google.protobuf.Message;
import io.netty.channel.EventLoop;
import top.limitart.mapping.Mapper;
import top.limitart.mapping.MapperClass;
import top.limitart.net.binary.BinaryMessage;
import top.limitart.net.binary.BinaryRequestParam;
import top.limitart.net.protobuf.ProtobufRequestParam;

/**
 * Created by Hank on 2018/10/13
 */
@MapperClass
public class MessageMapper {
    @Mapper(BinaryMessageDemo.class)
    public void doMessageDemo(BinaryRequestParam param) {
        BinaryMessageDemo msg = param.msg();
        Session<BinaryMessage, EventLoop> session = param.session();
        System.out.println(msg.content);
        session.writeNow(msg);
    }

    @Mapper(ProtobufMessageDemo.Demo2.class)
    public void doMessageDemo2(ProtobufRequestParam param) {
        ProtobufMessageDemo.Demo2 msg = param.msg();
        Session<Message, EventLoop> session = param.session();
        System.out.println("demo2:" + msg.getId());
    }

    @Mapper(ProtobufMessageDemo.Demo1.class)
    public void doMessageDemo1(ProtobufRequestParam param) {
        ProtobufMessageDemo.Demo1 msg = param.msg();
        Session<Message, EventLoop> session = param.session();
        System.out.println("demo1:" + msg.getName());
    }
}
