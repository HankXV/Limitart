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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.*;
import top.limitart.mapping.Router;
import top.limitart.net.NettyEndPoint;
import top.limitart.net.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hank
 * @version 2018/10/9 0009 21:11
 */
public class BinaryEndPoint extends NettyEndPoint<ByteBuf, BinaryMessage> {
    private static Logger LOGGER = LoggerFactory.getLogger(BinaryEndPoint.class);
    private final BinaryDecoder decoder;
    private final BinaryEncoder encoder;
    private final Map<Short, Class<BinaryMessage>> id2Msg = new ConcurrentHashMap<>();

    private final Router<BinaryMessage, BinaryRequestParam> router;
    private final Proc3<Session<BinaryMessage, EventLoop>, BinaryMessage, Router<BinaryMessage, BinaryRequestParam>> onMessageIn;
    private final Proc2<Session<BinaryMessage, EventLoop>, Boolean> onConnected;
    private final Proc1<Session<BinaryMessage, EventLoop>> onBind;
    private final Proc2<Session<BinaryMessage, EventLoop>, Throwable> onExceptionThrown;

    public static Builder builder(boolean server) {
        return new Builder(server);
    }

    public BinaryEndPoint(Builder builder) {
        super(builder.name, builder.server, builder.autoReconnect);
        this.decoder = Conditions.notNull(builder.decoder, "decoder");
        this.encoder = Conditions.notNull(builder.encoder, "encoder");
        this.router = Conditions.notNull(builder.router, "router");
        this.onMessageIn = builder.onMessageIn;
        this.onConnected = builder.onConnected;
        this.onBind = builder.onBind;
        this.onExceptionThrown = builder.onExceptionThrown;
        //初始化消息
        router.foreachRequstClass(c -> {
            BinaryMessage binaryMessage = null;
            try {
                binaryMessage = router.requestInstance(c);
            } catch (Exception e) {
                LOGGER.error("mapping message id error", e);
            }
            id2Msg.put(binaryMessage.id(), c);
        });
    }

    @Override
    protected void beforeTranslatorPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(), decoder.getLengthFieldOffset(),
                decoder.getLengthFieldLength(), decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
    }

    @Override
    protected void afterTranslatorPipeline(ChannelPipeline pipeline) {
        //DO NOTHING
    }

    @Override
    protected void exceptionThrown(Session<BinaryMessage, EventLoop> session, Throwable cause) {
        Procs.invoke(onExceptionThrown, session, cause);
    }

    @Override
    protected void sessionActive(Session<BinaryMessage, EventLoop> session, boolean activeOrNot) {
        Procs.invoke(onConnected, session, activeOrNot);
    }

    @Override
    protected void messageReceived(Session<BinaryMessage, EventLoop> session, Object arg) throws Exception {
        BinaryMessage msg = (BinaryMessage) arg;
        if (onMessageIn != null) {
            try {
                onMessageIn.run(session, msg, router);
            } catch (Exception e) {
                LOGGER.error(session.remoteAddress() + " cause:", e);
                Procs.invoke(onExceptionThrown, session, e);
            }
        } else {
            router.request(msg, () ->
                    new BinaryRequestParam(session, msg), i -> i.invoke());
        }
    }

    @Override
    protected void onBind(Session<BinaryMessage, EventLoop> session) {
        Procs.invoke(onBind, session);
    }


    @Override
    public BinaryMessage toOutputFinal(ByteBuf byteBuf) throws Exception {
        // 消息id
        short messageId = decoder.readMessageId(byteBuf);
        Class<BinaryMessage> aClass = id2Msg.get(messageId);
        if (aClass == null) {
            throw new BinaryMessageCodecException(name() + " message empty,id:" + BinaryMessages.ID2String(messageId));
        }
        BinaryMessage msg = router.requestInstance(aClass);
        if (msg == null) {
            throw new BinaryMessageCodecException(name() + " message empty,id:" + BinaryMessages.ID2String(messageId));
        }
        msg.buffer(byteBuf);
        try {
            msg.decode();
        } catch (Exception e) {
            LOGGER.error("message id:" + BinaryMessages.ID2String(messageId) + " decode error!");
            throw new BinaryMessageCodecException(e);
        }
        msg.buffer(null);
        return msg;
    }

    @Override
    public ByteBuf toInputFinal(BinaryMessage msg) throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        encoder.beforeWriteBody(buffer, msg.id());
        msg.buffer(buffer);
        try {
            msg.encode();
        } catch (IllegalAccessException | BinaryMessageCodecException e) {
            LOGGER.error("message encode error!", e);
        }
        encoder.afterWriteBody(buffer);
        msg.buffer(null);
        return buffer;
    }

    public static class Builder {
        private String name;
        private boolean server;
        private int autoReconnect;
        private BinaryDecoder decoder;
        private BinaryEncoder encoder;
        private Router<BinaryMessage, BinaryRequestParam> router;
        private Proc3<Session<BinaryMessage, EventLoop>, BinaryMessage, Router<BinaryMessage, BinaryRequestParam>> onMessageIn;
        private Proc2<Session<BinaryMessage, EventLoop>, Boolean> onConnected;
        private Proc1<Session<BinaryMessage, EventLoop>> onBind;
        private Proc2<Session<BinaryMessage, EventLoop>, Throwable> onExceptionThrown;

        public Builder(boolean server) {
            this.server = server;
            this.name = "Limitart-Binary";
            this.decoder = BinaryDecoder.BinaryDefaultDecoder.ME;
            this.encoder = BinaryEncoder.BinaryDefaultEncoder.ME;
        }

        /**
         * 构建服务器
         *
         * @return
         * @throws Exception
         */
        public BinaryEndPoint build() {
            return new BinaryEndPoint(this);
        }

        /**
         * 自定义解码器
         *
         * @param decoder
         * @return
         */
        @Optional
        public Builder decoder(BinaryDecoder decoder) {
            this.decoder = decoder;
            return this;
        }


        /**
         * 自定义编码器
         *
         * @param encoder
         * @return
         */
        @Optional
        public Builder encoder(BinaryEncoder encoder) {
            this.encoder = encoder;
            return this;
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
        public Builder router(Router<BinaryMessage, BinaryRequestParam> router) {
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
        public Builder onMessageIn(Proc3<Session<BinaryMessage, EventLoop>, BinaryMessage, Router<BinaryMessage, BinaryRequestParam>> onMessageIn) {
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
        public Builder onConnected(Proc2<Session<BinaryMessage, EventLoop>, Boolean> onConnected) {
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
        public Builder onBind(Proc1<Session<BinaryMessage, EventLoop>> onBind) {
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
        public Builder onExceptionThrown(Proc2<Session<BinaryMessage, EventLoop>, Throwable> onExceptionThrown) {
            this.onExceptionThrown = onExceptionThrown;
            return this;
        }
    }
}
