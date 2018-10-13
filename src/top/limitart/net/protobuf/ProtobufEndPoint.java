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
package top.limitart.net.protobuf;

import com.google.protobuf.Message;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.*;
import top.limitart.mapping.Router;
import top.limitart.net.NettyEndPoint;
import top.limitart.net.Session;

import java.lang.reflect.InvocationTargetException;


/**
 * Protobuf端点实现
 *
 * @author hank
 * @version 2018/10/12 0012 17:07
 */
public class ProtobufEndPoint extends NettyEndPoint<Message, Message> {
    private static Logger LOGGER = LoggerFactory.getLogger(ProtobufEndPoint.class);

    private final Router<Message, ProtobufRequestParam> router;
    private final Proc3<Session<Message, EventLoop>, Message, Router<Message, ProtobufRequestParam>> onMessageIn;
    private final Proc2<Session<Message, EventLoop>, Boolean> onConnected;
    private final Proc1<Session<Message, EventLoop>> onBind;
    private final Proc2<Session<Message, EventLoop>, Throwable> onExceptionThrown;

    public ProtobufEndPoint(ProtobufEndPoint.Builder builder) {
        super(builder.name, builder.server, builder.autoReconnect);
        this.router = Conditions.notNull(builder.router, "router");
        this.onMessageIn = builder.onMessageIn;
        this.onConnected = builder.onConnected;
        this.onBind = builder.onBind;
        this.onExceptionThrown = builder.onExceptionThrown;
    }


    @Override
    protected void beforeTranlaterPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        //初始化消息
        router.foreachRequstClass(c -> {
            try {
                Message defaultInstance = (Message) c.getMethod("getDefaultInstance").invoke(null);
                pipeline.addLast(new ProtobufDecoder(defaultInstance));
                LOGGER.info("register protobuf decoder {}", c.getName());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("reflect getDefaultInstance error", e);
            }
        });
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());
    }

    @Override
    protected void afterTranslaterPipeline(ChannelPipeline pipeline) {

    }

    @Override
    protected void exceptionThrown(Session<Message, EventLoop> session, Throwable cause) {
        Procs.invoke(onExceptionThrown, session, cause);
    }

    @Override
    protected void sessionActive(Session<Message, EventLoop> session, boolean activeOrNot) {
        Procs.invoke(onConnected, session, activeOrNot);
    }

    @Override
    protected void messageReceived(Session<Message, EventLoop> session, Message msg) throws Exception {
        if (onMessageIn != null) {
            try {
                onMessageIn.run(session, msg, router);
            } catch (Exception e) {
                LOGGER.error(session.remoteAddress() + " cause:", e);
                Procs.invoke(onExceptionThrown, session, e);
            }
        } else {
            router.request(msg, () ->
                    new ProtobufRequestParam(session, msg), i -> i.invoke());
        }
    }

    @Override
    protected void onBind(Session<Message, EventLoop> session) {
        Procs.invoke(onBind, session);
    }


    @Override
    public Message toOutputFinal(Message message) throws Exception {
        return message;
    }

    @Override
    public Message toInputFinal(Message message) throws Exception {
        return message;
    }

    public static class Builder {
        private String name;
        private boolean server;
        private int autoReconnect;
        private Router<Message, ProtobufRequestParam> router;
        private Proc3<Session<Message, EventLoop>, Message, Router<Message, ProtobufRequestParam>> onMessageIn;
        private Proc2<Session<Message, EventLoop>, Boolean> onConnected;
        private Proc1<Session<Message, EventLoop>> onBind;
        private Proc2<Session<Message, EventLoop>, Throwable> onExceptionThrown;

        public Builder(boolean server) {
            this.server = server;
            this.name = "Limitart-Protobuf";
        }

        /**
         * 构建服务器
         *
         * @return
         * @throws Exception
         */
        public ProtobufEndPoint build() {
            return new ProtobufEndPoint(this);
        }


        /**
         * 名称
         *
         * @param name
         * @return
         */
        @Optional
        public ProtobufEndPoint.Builder name(String name) {
            this.name = name;
            return this;
        }


        /**
         * 消息工厂
         *
         * @param router
         * @return
         */
        @Necessary
        public ProtobufEndPoint.Builder router(Router<Message, ProtobufRequestParam> router) {
            this.router = router;
            return this;
        }


        /**
         * 消息接收处理
         *
         * @param onMessageIn
         * @return
         */
        @Optional
        public ProtobufEndPoint.Builder onMessageIn(Proc3<Session<Message, EventLoop>, Message, Router<Message, ProtobufRequestParam>> onMessageIn) {
            this.onMessageIn = onMessageIn;
            return this;
        }

        /**
         * 链接创建处理
         *
         * @param onConnected
         * @return
         */
        @Optional
        public ProtobufEndPoint.Builder onConnected(Proc2<Session<Message, EventLoop>, Boolean> onConnected) {
            this.onConnected = onConnected;
            return this;
        }

        /**
         * 服务器绑定处理
         *
         * @param onBind
         * @return
         */
        @Optional
        public ProtobufEndPoint.Builder onBind(Proc1<Session<Message, EventLoop>> onBind) {
            this.onBind = onBind;
            return this;
        }

        /**
         * 自动重连尝试间隔(秒)
         *
         * @param autoReconnect
         * @return
         */
        @Optional
        public ProtobufEndPoint.Builder autoReconnect(int autoReconnect) {
            this.autoReconnect = autoReconnect;
            return this;
        }

        /**
         * 服务器抛异常处理
         *
         * @param onExceptionThrown
         * @return
         */
        @Optional
        public ProtobufEndPoint.Builder onExceptionThrown(Proc2<Session<Message, EventLoop>, Throwable> onExceptionThrown) {
            this.onExceptionThrown = onExceptionThrown;
            return this;
        }
    }

}
