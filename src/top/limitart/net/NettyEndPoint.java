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

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Conditions;
import top.limitart.concurrent.ThreadLocalHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Netty实现的端点
 *
 * @author hank
 * @version 2018/10/9 0009 19:43
 */
public abstract class NettyEndPoint<IN, OUT> implements EndPoint<IN, OUT> {
    private static Logger LOGGER = LoggerFactory.getLogger(NettyEndPoint.class);
    private static final AttributeKey<AddressPair> CLIENT_REMOTE_ADDR = AttributeKey.newInstance("CLIENT_REMOTE_ADDR");
    private String name;
    private final ThreadLocalHolder<Map<Channel, Session>> sessions = ThreadLocalHolder.create();
    protected AbstractBootstrap bootstrap;
    protected final static EventLoopGroup bossGroup;
    protected final static EventLoopGroup workerGroup;
    private final static Class<? extends ServerChannel> serverChannelClass;
    private final static Class<? extends Channel> clientChannelClass;

    private Session<OUT, EventLoop> endPointSession;
    private int autoReconnect;

    static {
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup();
            serverChannelClass = EpollServerSocketChannel.class;
            clientChannelClass = EpollSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            serverChannelClass = NioServerSocketChannel.class;
            clientChannelClass = NioSocketChannel.class;
        }
    }

    @Override
    public EndPoint start(AddressPair addressPair) {
        if (bootstrap instanceof ServerBootstrap) {
            bootstrap.bind(addressPair.getPort()).addListener((ChannelFuture arg0) -> {
                if (arg0.isSuccess()) {
                    LOGGER.info(name() + " bind at :" + addressPair);
                    endPointSession = new NettySession(arg0.channel());
                    onBind(endPointSession);
                } else {
                    LOGGER.error(name() + " bind error:" + arg0.cause());
                }
            });
        } else {
            if (endPointSession != null && endPointSession.writable()) {
                return this;
            }
            LOGGER.info(name() + " start connect server：" + addressPair + "...");
            try {
                ((Bootstrap) bootstrap).connect(addressPair.getIp(), addressPair.getPort()).sync().addListener((ChannelFutureListener) channelFuture -> {
                    endPointSession = new NettySession<>(channelFuture.channel());
                    channelFuture.channel().attr(CLIENT_REMOTE_ADDR).set(addressPair);
                    LOGGER.info(name() + " connect server：" + addressPair + " success！");
                });
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                if (autoReconnect > 0) {
                    tryReconnect(addressPair, autoReconnect);
                }
            }
        }
        return this;
    }

    protected void tryReconnect(AddressPair addressPair, int waitSeconds) {
        stop();
        LOGGER.info(name() + " try connect server：" + addressPair + " after " + waitSeconds + " seconds");
        if (waitSeconds > 0) {
            workerGroup.schedule(() -> start(addressPair), waitSeconds, TimeUnit.SECONDS);
        } else {
            start(addressPair);
        }
    }

    @Override
    public EndPoint stop() {
        if (endPointSession != null) {
            endPointSession.close();
            endPointSession = null;
        }
        return this;
    }


    public int getAutoReconnect() {
        return autoReconnect;
    }

    /**
     * 安插在IN_OUT转化器之前的处理链
     *
     * @param pipeline
     */
    protected abstract void beforeTranslatorPipeline(ChannelPipeline pipeline);

    /**
     * 安插在IN_OUT转化器之后的处理链
     *
     * @param pipeline
     */
    protected abstract void afterTranslatorPipeline(ChannelPipeline pipeline);

    protected abstract void exceptionThrown(Session<OUT, EventLoop> session, Throwable cause) throws Exception;

    protected abstract void sessionActive(Session<OUT, EventLoop> session, boolean activeOrNot) throws Exception;

    protected abstract void messageReceived(Session<OUT, EventLoop> session, Object msg) throws Exception;

    protected abstract void onBind(Session<OUT, EventLoop> session);

    protected Session<OUT, EventLoop> createSession(Channel channel) {
        return new NettySession<>(channel);
    }

    public NettyEndPoint(String name, boolean server, int autoReconnect) {
        Conditions.notNull(name, "name");
        this.name = name;
        this.autoReconnect = autoReconnect;
        if (server) {
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossGroup, workerGroup).channel(serverChannelClass);
            serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ReadTimeoutHandler(60));
                    beforeTranslatorPipeline(ch.pipeline());
                    ch.pipeline().addLast(new InOutTransfer());
                    afterTranslatorPipeline(ch.pipeline());
                    ch.pipeline().addLast(new ChannelStateHandler());
                }
            });
            if (Epoll.isAvailable()) {
                serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                LOGGER.info(name + " epoll init");
            } else {
                LOGGER.info(name + " nio init");
            }
            this.bootstrap = serverBootstrap;
        } else {
            Bootstrap clientBootstrap = new Bootstrap().group(workerGroup).channel(clientChannelClass);
            clientBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ReadTimeoutHandler(60));
                    beforeTranslatorPipeline(ch.pipeline());
                    ch.pipeline().addLast(new InOutTransfer());
                    afterTranslatorPipeline(ch.pipeline());
                    ch.pipeline().addLast(new ChannelStateHandler());
                }
            });
            if (Epoll.isAvailable()) {
                LOGGER.info(name + " epoll init");
            } else {
                LOGGER.info(name + " nio init");
            }
            this.bootstrap = clientBootstrap;
        }
    }

    @Override
    public String name() {
        return this.name;
    }


    /**
     * 通过channel映射Session
     *
     * @param channel 通道
     * @return 会话
     */
    protected Session<OUT, EventLoop> getSession(Channel channel) {
        Conditions.args(channel.eventLoop().inEventLoop(), "can not call this on another thread,must on it's own");
        Map<Channel, Session> channelSessionMap = sessions.get();
        if (channelSessionMap == null) {
            return null;
        }
        return channelSessionMap.get(channel);
    }

    protected Session<OUT, EventLoop> putSession(Channel channel) {
        Conditions.args(channel.eventLoop().inEventLoop(), "can not call this on another thread,must on it's own");
        Session<OUT, EventLoop> session = createSession(channel);
        sessions.getWithInitialize(HashMap::new).put(channel, session);
        return session;
    }

    protected Session<OUT, EventLoop> removeSession(Channel channel) {
        Conditions.args(channel.eventLoop().inEventLoop(), "can not call this on another thread,must on it's own");
        Map<Channel, Session> channelSessionMap = sessions.get();
        if (channelSessionMap == null) {
            return null;
        }
        return channelSessionMap.remove(channel);
    }

    @ChannelHandler.Sharable
    private class InOutTransfer extends MessageToMessageCodec<IN, OUT> {

        @Override
        protected void encode(ChannelHandlerContext ctx, OUT msg, List<Object> out) throws Exception {
            IN i = NettyEndPoint.this.toInputFinal(msg);
            out.add(i);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, IN msg, List<Object> out) throws Exception {
            OUT o = NettyEndPoint.this.toOutputFinal(msg);
            out.add(o);
        }
    }

    private class ChannelStateHandler extends ChannelInboundHandlerAdapter {
        @Override
        public boolean isSharable() {
            return true;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOGGER.error(ctx.channel() + " cause:", cause);
            exceptionThrown(getSession(ctx.channel()), cause);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info(ctx.channel().remoteAddress() + " connected！");
            sessionActive(putSession(ctx.channel()), true);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info(ctx.channel().remoteAddress() + " disconnected！");
            Session<OUT, EventLoop> remove = removeSession(ctx.channel());
            sessionActive(remove, false);
            if (bootstrap instanceof Bootstrap) {
                if (getAutoReconnect() > 0) {
                    AddressPair addressPair = ctx.channel().attr(CLIENT_REMOTE_ADDR).get();
                    tryReconnect(addressPair, getAutoReconnect());
                }
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            NettyEndPoint.this.messageReceived(getSession(ctx.channel()), (OUT) msg);
        }
    }
}
