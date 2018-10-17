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
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
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
import top.limitart.base.Proc2;
import top.limitart.base.Proc3;
import top.limitart.base.Procs;
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
    private NettyEndPointType type;

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
    public EndPoint start(AddressPair addressPair, Proc3<Session<OUT, EventLoop>, Boolean, Throwable> listener) {
        if (bootstrap instanceof ServerBootstrap) {
            ChannelFuture future;
            if (type.local()) {
                future = bootstrap.bind(new LocalAddress(addressPair.toString()));
            } else {
                future = bootstrap.bind(addressPair.getPort());
            }
            future.addListener((ChannelFuture arg0) -> {
                if (arg0.isSuccess()) {
                    LOGGER.info("{} bind at {} {}", name(), type.local() ? "local" : "", addressPair);
                    endPointSession = createSession(arg0.channel());
                    Procs.invoke(listener, endPointSession, true, null);
                } else {
                    LOGGER.error("{} bind error:{}", name(), arg0.cause());
                    Procs.invoke(listener, null, false, arg0.cause());
                }
            });
        } else {
            if (endPointSession != null && endPointSession.writable()) {
                return this;
            }
            LOGGER.info("{} start connect to {} server： {}...", name(), type.local() ? "local" : "", addressPair);
            try {
                ChannelFuture future;
                if (type.local()) {
                    future = ((Bootstrap) bootstrap).connect(new LocalAddress(addressPair.toString()));
                } else {
                    future = ((Bootstrap) bootstrap).connect(addressPair.getIp(), addressPair.getPort());
                }
                future.sync().addListener((ChannelFutureListener) channelFuture -> {
                    endPointSession = createSession(channelFuture.channel());
                    channelFuture.channel().attr(CLIENT_REMOTE_ADDR).set(addressPair);
                    LOGGER.info("{} connect {} server: {} success！", name(), type.local() ? "local" : "", addressPair);
                    Procs.invoke(listener, endPointSession, true, null);
                });
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                Procs.invoke(listener, null, false, e);
                if (autoReconnect > 0) {
                    tryReconnect(addressPair, autoReconnect, listener);
                }
            }
        }
        return this;
    }

    protected void tryReconnect(AddressPair addressPair, int waitSeconds, Proc3<Session<OUT, EventLoop>, Boolean, Throwable> listener) {
        stop();
        LOGGER.info("{} try connect {} server： {} after {} seconds...", name(), type.local() ? "local" : "", addressPair, waitSeconds);
        if (waitSeconds > 0) {
            workerGroup.schedule(() -> start(addressPair, listener), waitSeconds, TimeUnit.SECONDS);
        } else {
            start(addressPair, listener);
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

    protected Session<OUT, EventLoop> createSession(Channel channel) {
        return new NettySession<>(channel);
    }

    public NettyEndPoint(String name, NettyEndPointType type, int autoReconnect, int timeoutSeconds) {
        Conditions.notNull(name, "name");
        this.name = name;
        this.autoReconnect = autoReconnect;
        this.type = type;
        if (type.server()) {
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(type.local() ? new DefaultEventLoopGroup(1) : bossGroup, type.local() ? new DefaultEventLoop() : workerGroup).channel(type.local() ? LocalServerChannel.class : serverChannelClass);
            if (!type.local()) {
                serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            }
            serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) {
                            initPipeline(ch.pipeline(), timeoutSeconds);
                        }
                    });
            if (Epoll.isAvailable()) {
                serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                LOGGER.info("{} epoll init", name);
            } else {
                LOGGER.info("{} nio init", name);
            }
            this.bootstrap = serverBootstrap;
        } else {
            Bootstrap clientBootstrap = new Bootstrap().group(type.local() ? new DefaultEventLoop() : workerGroup).channel(type.local() ? LocalChannel.class : clientChannelClass);
            clientBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) {
                    initPipeline(ch.pipeline(), timeoutSeconds);
                }
            });
            if (Epoll.isAvailable()) {
                LOGGER.info("{} epoll init", name);
            } else {
                LOGGER.info("{} nio init", name);
            }
            this.bootstrap = clientBootstrap;
        }
    }

    private void initPipeline(ChannelPipeline pipeline, int timeoutSeconds) {
        if (timeoutSeconds > 0) {
            pipeline.addLast(new ReadTimeoutHandler(timeoutSeconds));
        }
        beforeTranslatorPipeline(pipeline);
        pipeline.addLast(new InOutTransfer());
        afterTranslatorPipeline(pipeline);
        pipeline.addLast(new ChannelStateHandler());
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
        Conditions.args(channel.eventLoop().inEventLoop(), name + " can not call this on another thread,must on it's own");
        Map<Channel, Session> channelSessionMap = sessions.get();
        if (channelSessionMap == null) {
            return null;
        }
        return channelSessionMap.get(channel);
    }

    protected Session<OUT, EventLoop> putSession(Channel channel) {
        Conditions.args(channel.eventLoop().inEventLoop(), name + " can not call this on another thread,must on it's own");
        Session<OUT, EventLoop> session = createSession(channel);
        sessions.getWithInitialize(HashMap::new).put(channel, session);
        return session;
    }

    protected Session<OUT, EventLoop> removeSession(Channel channel) {
        Conditions.args(channel.eventLoop().inEventLoop(), name + " can not call this on another thread,must on it's own");
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
            LOGGER.error("{}, {} cause:", name(), ctx.channel(), cause);
            exceptionThrown(getSession(ctx.channel()), cause);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("{},{} connected！", name(), ctx.channel().remoteAddress());
            sessionActive(putSession(ctx.channel()), true);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("{},{} disconnected！", name(), ctx.channel().remoteAddress());
            Session<OUT, EventLoop> remove = removeSession(ctx.channel());
            sessionActive(remove, false);
            if (bootstrap instanceof Bootstrap) {
                if (getAutoReconnect() > 0) {
                    AddressPair addressPair = ctx.channel().attr(CLIENT_REMOTE_ADDR).get();
                    tryReconnect(addressPair, getAutoReconnect(), null);
                }
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            NettyEndPoint.this.messageReceived(getSession(ctx.channel()), (OUT) msg);
        }
    }
}
