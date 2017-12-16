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
package org.slingerxv.limitart.net.binary;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;
import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.net.AbstractNettyServer;
import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.Server;
import org.slingerxv.limitart.net.Session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 二进制通信服务器
 *
 * @author Hank
 */
public class BinaryServer extends AbstractNettyServer implements Server {
    private static Logger log = Loggers.create(BinaryServer.class);
    private AddressPair addressPair;
    private BinaryDecoder decoder;
    private BinaryEncoder encoder;
    private BinaryMessageFactory factory;
    // --listener
    private Proc3<Session, BinaryMessage, BinaryMessageFactory> onMessageIn;
    private Proc2<Session, Boolean> onConnected;
    private Proc1<Session> onBind;
    private Proc2<Session, Throwable> onExceptionTrown;
    private List<BinaryServerInterceptor> interceptors = new LinkedList<>();

    private BinaryServer(BinaryServerBuilder builder) {
        super(builder.serverName);
        this.addressPair = Conditions.notNull(builder.addressPair, "addressPair");
        this.decoder = Conditions.notNull(builder.decoder, "decoder");
        this.encoder = Conditions.notNull(builder.encoder, "encoder");
        this.factory = Conditions.notNull(builder.factory, "factory");
        this.onMessageIn = builder.onMessageIn;
        this.onConnected = builder.onConnected;
        this.onBind = builder.onBind;
        this.onExceptionTrown = builder.onExceptionTrown;
        interceptors.addAll(builder.interceptors);
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(), decoder.getLengthFieldOffset(),
                decoder.getLengthFieldLength(), decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
    }

    @Override
    protected void exceptionThrown(Session session, Throwable cause) throws Exception {
        Procs.invoke(onExceptionTrown, session, cause);
    }

    @Override
    protected void sessionActive(Session session, boolean activeOrNot) throws Exception {
        if (activeOrNot) {
            for (BinaryServerInterceptor interceptor : interceptors) {
                if (interceptor.onConnected(session)) {
                    log.info("session:" + session.remoteAddress() + " is intercepted by "
                            + interceptor.getClass().getName());
                    session.close();
                    return;
                }
            }
        }
        Procs.invoke(onConnected, session, activeOrNot);
    }

    @Override
    protected void messageReceived(Session session, ByteBuf buffer) {
        try {
            // 消息id
            short messageId = decoder.readMessageId(session, buffer);
            BinaryMessage msg = factory.msgInstance(messageId);
            if (msg == null) {
                throw new BinaryMessageCodecException(serverName() + " message empty,id:" + BinaryMessages.ID2String(messageId));
            }
            msg.buffer(buffer);
            try {
                msg.decode();
            } catch (Exception e) {
                log.error("message id:" + BinaryMessages.ID2String(messageId) + " decode error!");
                throw new BinaryMessageCodecException(e);
            }
            msg.buffer(null);
            for (BinaryServerInterceptor interceptor : interceptors) {
                if (interceptor.onMessageIn(session, msg)) {
                    return;
                }
            }
            if (onMessageIn != null) {
                try {
                    onMessageIn.run(session, msg, factory);
                } catch (Exception e) {
                    log.error(session.remoteAddress() + " cause:", e);
                    Procs.invoke(onExceptionTrown, session, e);
                }
            } else {
                msg.invokeSelf(factory, session);
            }
        } catch (Throwable e) {
            session.close();
            log.error("close session:" + session.remoteAddress(), e);
        } finally {
            buffer.release();
        }

    }

    @Override
    public void startServer() {
        bind(addressPair.getPort(), onBind);
    }

    @Override
    public void stopServer() {
        unbind();
    }

    /**
     * 发送消息
     *
     * @param session
     * @param msg
     */
    public void sendMessage(@NotNull Session session, @NotNull BinaryMessage msg) {
        sendMessage(session, msg, null);
    }

    /**
     * 发送消息
     *
     * @param session
     * @param msg
     * @param listener
     * @throws BinaryMessageCodecException
     */
    public void sendMessage(@NotNull Session session, @NotNull BinaryMessage msg, @Nullable Proc2<Boolean, Throwable> listener) {
        BinarySendMessageUtil.sendMessage(encoder, session, msg, (suc, cause) -> {
            Procs.invoke(listener, suc, cause);
            if (suc) {
                for (BinaryServerInterceptor si : interceptors) {
                    si.onMessageOut(session, msg);
                }
            }
        });
    }

    /**
     * 广播消息
     *
     * @param sessions
     * @param msg
     */
    public void sendMessage(@NotNull List<Session> sessions, @NotNull BinaryMessage msg) {
        sendMessage(sessions, msg, null);
    }

    /**
     * 广播消息
     *
     * @param sessions
     * @param msg
     * @param listener
     * @throws BinaryMessageCodecException
     */
    public void sendMessage(@NotNull List<Session> sessions, @NotNull BinaryMessage msg, @Nullable Proc2<Boolean, Throwable> listener) {
        BinarySendMessageUtil.sendMessage(encoder, sessions, msg, (session, suc, cause) -> {
            Procs.invoke(listener, suc, cause);
            if (suc) {
                for (BinaryServerInterceptor si : interceptors) {
                    si.onMessageOut(session, msg);
                }
            }
        });
    }

    /**
     * 获取服务器端口配置
     *
     * @return
     */
    public AddressPair getAddressPair() {
        return addressPair;
    }

    /**
     * 获取服务器解码器
     *
     * @return
     */
    public BinaryDecoder getDecoder() {
        return decoder;
    }

    /**
     * 获取服务器编码器
     *
     * @return
     */
    public BinaryEncoder getEncoder() {
        return encoder;
    }

    /**
     * 获取服务器消息工厂
     *
     * @return
     */
    public BinaryMessageFactory getFactory() {
        return factory;
    }

    public static class BinaryServerBuilder {
        private String serverName;
        private AddressPair addressPair;
        private BinaryDecoder decoder;
        private BinaryEncoder encoder;
        private BinaryMessageFactory factory;
        private Proc3<Session, BinaryMessage, BinaryMessageFactory> onMessageIn;
        private Proc2<Session, Boolean> onConnected;
        private Proc1<Session> onBind;
        private Proc2<Session, Throwable> onExceptionTrown;
        private List<BinaryServerInterceptor> interceptors = new LinkedList<>();

        public BinaryServerBuilder() {
            this.serverName = "Limitart-Binary-Server";
            this.addressPair = new AddressPair(8888);
            this.decoder = BinaryDefaultDecoder.ME;
            this.encoder = BinaryDefaultEncoder.ME;
        }

        /**
         * 构建服务器
         *
         * @return
         * @throws Exception
         */
        public BinaryServer build() throws Exception {
            return new BinaryServer(this);
        }

        /**
         * 自定义解码器
         *
         * @param decoder
         * @return
         */
        @Optional
        public BinaryServerBuilder decoder(BinaryDecoder decoder) {
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
        public BinaryServerBuilder encoder(BinaryEncoder encoder) {
            this.encoder = encoder;
            return this;
        }

        /**
         * 服务器名称
         *
         * @param serverName
         * @return
         */
        @Optional
        public BinaryServerBuilder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        /**
         * 绑定端口
         *
         * @param addressPair
         * @return
         */
        @Optional
        public BinaryServerBuilder addressPair(AddressPair addressPair) {
            this.addressPair = addressPair;
            return this;
        }

        /**
         * 消息工厂
         *
         * @param factory
         * @return
         */
        @Necessary
        public BinaryServerBuilder factory(BinaryMessageFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * 添加拦截器
         *
         * @param its
         * @return
         */
        @Optional
        public BinaryServerBuilder intercept(BinaryServerInterceptor... its) {
            Collections.addAll(interceptors, its);
            return this;
        }

        /**
         * 消息接收处理
         *
         * @param onMessageIn
         * @return
         */
        @Optional
        public BinaryServerBuilder onMessageIn(Proc3<Session, BinaryMessage, BinaryMessageFactory> onMessageIn) {
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
        public BinaryServerBuilder onConnected(Proc2<Session, Boolean> onConnected) {
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
        public BinaryServerBuilder onBind(Proc1<Session> onBind) {
            this.onBind = onBind;
            return this;
        }

        /**
         * 服务器抛异常处理
         *
         * @param onExceptionTrown
         * @return
         */
        @Optional
        public BinaryServerBuilder onExceptionTrown(Proc2<Session, Throwable> onExceptionTrown) {
            this.onExceptionTrown = onExceptionTrown;
            return this;
        }
    }
}
