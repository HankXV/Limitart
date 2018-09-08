/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package top.limitart.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.limitart.base.Conditions;

import java.util.concurrent.TimeUnit;

/**
 * 抽象Netty客户端
 *
 * @author hank
 */
public abstract class AbstractNettyClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNettyClient.class);
    protected final static EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private String clientName;
    private Session session;
    private int autoReconnect;

    static {
        workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    }

    protected AbstractNettyClient(String clientName, int autoReconnect) {
        this.clientName = Conditions.notNull(clientName, "client name");
        this.autoReconnect = autoReconnect;
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        if (Epoll.isAvailable()) {
            bootstrap.channel(EpollSocketChannel.class);
            LOGGER.info(clientName + " epoll init");
        } else {
            bootstrap.channel(NioSocketChannel.class);
            LOGGER.info(clientName + " nio init");
        }
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) {
                        initPipeline(ch.pipeline());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public boolean isSharable() {
                                return true;
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                channelRead0(ctx, msg);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                LOGGER.info(clientName() + " disconnected!");
                                channelInactive0(ctx);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                session = new Session(0, ctx.channel());
                                LOGGER.info(clientName() + " connected!");
                                channelActive0(ctx);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                LOGGER.error(ctx.channel() + " cause:", cause);
                                exceptionCaught0(ctx, cause);
                            }
                        });
                    }
                });
    }

    protected abstract void initPipeline(ChannelPipeline pipeline);

    protected abstract void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception;

    protected abstract void channelInactive0(ChannelHandlerContext ctx) throws Exception;

    protected abstract void channelActive0(ChannelHandlerContext ctx) throws Exception;

    protected abstract void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) throws Exception;

    protected AbstractNettyClient tryDisConnect() {
        if (session != null) {
            session.close();
            session = null;
        }
        return this;
    }

    private void connect0(String ip, int port) {
        if (session != null && session.writable()) {
            return;
        }
        LOGGER.info(clientName + " start connect server：" + ip + ":" + port + "...");
        try {
            bootstrap.connect(ip, port).sync().addListener((ChannelFutureListener) channelFuture -> LOGGER.info(clientName + " connect server：" + ip + ":" + port + " success！"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (autoReconnect > 0) {
                tryReconnect(ip, port, autoReconnect);
            }
        }
    }

    protected void tryReconnect(String ip, int port, int waitSeconds) {
        tryDisConnect();
        LOGGER.info(clientName + " try connect server：" + ip + ":" + port + " after " + waitSeconds + " seconds");
        if (waitSeconds > 0) {
            workerGroup.schedule(() -> connect0(ip, port), waitSeconds, TimeUnit.SECONDS);
        } else {
            connect0(ip, port);
        }
    }

    public String clientName() {
        return clientName;
    }

    public int getAutoReconnect() {
        return autoReconnect;
    }

    public Session session() {
        return session;
    }
}
