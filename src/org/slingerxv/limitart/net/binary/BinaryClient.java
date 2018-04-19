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


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slingerxv.limitart.base.*;
import org.slingerxv.limitart.net.AbstractNettyClient;
import org.slingerxv.limitart.net.AddressPair;
import org.slingerxv.limitart.net.Client;

/**
 * 二进制通信客户端
 *
 * @author hank
 */
public class BinaryClient extends AbstractNettyClient implements Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryClient.class);
    // ----config
    private final AddressPair remoteAddress;
    private final BinaryDecoder decoder;
    private final BinaryEncoder encoder;
    private final BinaryMessageFactory factory;
    // ----listener
    private final Proc2<BinaryClient, Boolean> onConnected;
    private final Proc2<BinaryClient, Throwable> onExceptionCaught;

    private BinaryClient(BinaryClientBuilder builder) {
        super(builder.clientName, builder.autoReconnect);
        this.remoteAddress = Conditions.notNull(builder.remoteAddress, "remoteAddress");
        this.decoder = Conditions.notNull(builder.decoder, "decoder");
        this.encoder = Conditions.notNull(builder.encoder, "encoder");
        this.factory = Conditions.notNull(builder.factory, "factory");
        this.onConnected = builder.onConnected;
        this.onExceptionCaught = builder.onExceptionCaught;
    }

    /**
     * 向服务器发送消息
     *
     * @param msg 消息实例
     */
    public void sendMessage(@NotNull BinaryMessage msg) {
        sendMessage(msg, null);
    }

    /**
     * 向服务器发送消息
     *
     * @param msg      消息实例
     * @param listener 消息发送结果监听
     * @throws BinaryMessageCodecException 消息编码异常
     */
    public void sendMessage(@NotNull BinaryMessage msg, @Nullable Proc2<Boolean, Throwable> listener) {
        BinarySendMessageUtil.sendMessage(encoder, session(), msg, listener);
    }

    /**
     * 连接服务器
     */
    @Override
    public void connect() {
        tryReconnect(remoteAddress.getIp(), remoteAddress.getPort(), 0);
    }

    /**
     * 与服务器断开连接
     */
    @Override
    public void disConnect() {
        tryDisConnect();
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new LengthFieldBasedFrameDecoder(decoder.getMaxFrameLength(), decoder.getLengthFieldOffset(),
                decoder.getLengthFieldLength(), decoder.getLengthAdjustment(), decoder.getInitialBytesToStrip()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object arg) {
        ByteBuf buffer = (ByteBuf) arg;
        try {
            // 消息id
            short messageId = decoder.readMessageId(session(), buffer);
            BinaryMessage msg = factory.msgInstance(messageId);
            if (msg == null) {
                throw new BinaryMessageCodecException(clientName() + " message empty,id:" + BinaryMessages.ID2String(messageId));
            }
            msg.buffer(buffer);
            try {
                msg.decode();
            } catch (Exception e) {
                throw new BinaryMessageCodecException(e);
            }
            msg.buffer(null);
            factory.invokeMethod(session(), msg);
        } catch (Throwable e) {
            LOGGER.error("decode error", e);
        } finally {
            buffer.release();
        }

    }

    @Override
    protected void channelInactive0(ChannelHandlerContext ctx) {
        Procs.invoke(onConnected, BinaryClient.this, false);
        if (getAutoReconnect() > 0) {
            tryReconnect(remoteAddress.getIp(), remoteAddress.getPort(), getAutoReconnect());
        }
    }

    @Override
    protected void channelActive0(ChannelHandlerContext ctx) {
        Procs.invoke(onConnected, BinaryClient.this, true);
    }

    @Override
    protected void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) {
        Procs.invoke(onExceptionCaught, BinaryClient.this, cause);
    }

    /**
     * 获取解码器
     *
     * @return 解码器
     */
    public BinaryDecoder getDecoder() {
        return decoder;
    }

    /**
     * 获取编码器
     *
     * @return 编码器
     */
    public BinaryEncoder getEncoder() {
        return encoder;
    }

    /**
     * 获取消息工厂
     *
     * @return 消息工厂
     */
    public BinaryMessageFactory getFactory() {
        return factory;
    }

    public static class BinaryClientBuilder {
        private String clientName;
        private AddressPair remoteAddress;
        private int autoReconnect;
        private BinaryDecoder decoder;
        private BinaryEncoder encoder;
        private BinaryMessageFactory factory;
        // ----listener
        private Proc2<BinaryClient, Boolean> onConnected;
        private Proc2<BinaryClient, Throwable> onExceptionCaught;

        public BinaryClientBuilder() {
            this.clientName = "Limitart-Binary-Client";
            this.remoteAddress = new AddressPair("127.0.0.1", 8888);
            this.autoReconnect = 0;
            this.decoder = BinaryDefaultDecoder.ME;
            this.encoder = BinaryDefaultEncoder.ME;
        }

        /**
         * 构建配置
         *
         * @return
         * @throws Exception
         */
        public BinaryClient build() {
            return new BinaryClient(this);
        }

        /**
         * 指定解码器
         *
         * @param decoder
         * @return
         */
        @Optional
        public BinaryClientBuilder decoder(BinaryDecoder decoder) {
            this.decoder = decoder;
            return this;
        }

        /**
         * 指定编码器
         *
         * @param encoder
         * @return
         */
        @Optional
        public BinaryClientBuilder encoder(BinaryEncoder encoder) {
            this.encoder = encoder;
            return this;
        }

        /**
         * 客户端名称
         *
         * @param clientName
         * @return
         */
        @Optional
        public BinaryClientBuilder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        /**
         * 服务器IP
         *
         * @param remoteAddress
         * @return
         */
        @Optional
        public BinaryClientBuilder remoteAddress(AddressPair remoteAddress) {
            this.remoteAddress = remoteAddress;
            return this;
        }

        /**
         * 自动重连尝试间隔(秒)
         *
         * @param autoReconnect
         * @return
         */
        @Optional
        public BinaryClientBuilder autoReconnect(int autoReconnect) {
            this.autoReconnect = autoReconnect;
            return this;
        }

        /**
         * 指定消息工厂
         *
         * @param factory
         * @return
         */
        @Necessary
        public BinaryClientBuilder factory(BinaryMessageFactory factory) {
            this.factory = factory;
            return this;
        }

        /**
         * 链接状态变化监听
         *
         * @param onConnected
         * @return
         */
        @Optional
        public BinaryClientBuilder onConnected(Proc2<BinaryClient, Boolean> onConnected) {
            this.onConnected = onConnected;
            return this;
        }

        /**
         * 异常处理
         *
         * @param onExceptionCaught
         * @return
         */
        @Optional
        public BinaryClientBuilder onExceptionCaught(Proc2<BinaryClient, Throwable> onExceptionCaught) {
            this.onExceptionCaught = onExceptionCaught;
            return this;
        }
    }
}
