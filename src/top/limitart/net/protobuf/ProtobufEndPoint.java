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
import top.limitart.net.NettyEndPointType;
import top.limitart.net.Session;
import top.limitart.net.binary.BinaryEndPoint;

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
    private final Proc2<Session<Message, EventLoop>, Throwable> onExceptionThrown;

    public static Builder client() {
        return builder(false);
    }

    public static Builder server() {
        return builder(true);
    }

    public static Builder builder(boolean server) {
        return new Builder(server);
    }

    public static Builder builder(NettyEndPointType type) {
        return new Builder(true);
    }

    public ProtobufEndPoint(ProtobufEndPoint.Builder builder) {
        super(builder.name, builder.type, builder.autoReconnect, builder.timeoutSeconds);
        this.router = Conditions.notNull(builder.router, "router");
        this.onMessageIn = builder.onMessageIn;
        this.onConnected = builder.onConnected;
        this.onExceptionThrown = builder.onExceptionThrown;
    }


    @Override
    protected void beforeTranslatorPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        //初始化消息
        router.foreachRequestClass(c -> {
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
    protected void afterTranslatorPipeline(ChannelPipeline pipeline) {

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
    protected void messageReceived(Session<Message, EventLoop> session, Object arg) throws Exception {
        Message msg = (Message) arg;
        if (onMessageIn != null) {
            try {
                onMessageIn.run(session, msg, router);
            } catch (Exception e) {
                LOGGER.error(session.remoteAddress() + " cause:", e);
                Procs.invoke(onExceptionThrown, session, e);
            }
        } else {
            router.request(msg, () ->
                    new ProtobufRequestParam(session, msg) {
                    }, i -> i.invoke());
            LOGGER.warn("can not find handler to deal msg!!!");
//            throw new IllegalArgumentException("can not find handler to deal msg");
        }
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
        private NettyEndPointType type;
        private int autoReconnect;
        private int timeoutSeconds;
        private Router<Message, ProtobufRequestParam> router;
        private Proc3<Session<Message, EventLoop>, Message, Router<Message, ProtobufRequestParam>> onMessageIn;
        private Proc2<Session<Message, EventLoop>, Boolean> onConnected;
        private Proc2<Session<Message, EventLoop>, Throwable> onExceptionThrown;

        public Builder(NettyEndPointType type) {
            this.type = type;
            this.name = "Limitart-Protobuf";
            this.timeoutSeconds = 60;
        }

        public Builder(boolean server) {
            this(server ? NettyEndPointType.defaultServer() : NettyEndPointType.defaultClient());
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
        public Builder name(String name) {
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
        public Builder router(Router<Message, ProtobufRequestParam> router) {
            this.router = router;
            return this;
        }

        @Optional
        public Builder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        /**
         * 消息接收处理
         *
         * @param onMessageIn
         * @return
         */
        @Optional
        public Builder onMessageIn(Proc3<Session<Message, EventLoop>, Message, Router<Message, ProtobufRequestParam>> onMessageIn) {
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
        public Builder onConnected(Proc2<Session<Message, EventLoop>, Boolean> onConnected) {
            this.onConnected = onConnected;
            return this;
        }


        /**
         * 自动重连尝试间隔(秒)
         *
         * @param autoReconnect
         * @return
         */
        @Optional
        public Builder autoReconnect(int autoReconnect) {
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
        public Builder onExceptionThrown(Proc2<Session<Message, EventLoop>, Throwable> onExceptionThrown) {
            this.onExceptionThrown = onExceptionThrown;
            return this;
        }
    }

}
