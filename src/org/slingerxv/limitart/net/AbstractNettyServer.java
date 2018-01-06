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
package org.slingerxv.limitart.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.Proc1;
import org.slingerxv.limitart.base.Procs;
import org.slingerxv.limitart.logging.Logger;
import org.slingerxv.limitart.logging.Loggers;

/**
 * 抽象Netty服务器
 *
 * @author hank
 */
public abstract class AbstractNettyServer {
    private static Logger log = Loggers.create();
    private static final AttributeKey<Integer> SESSION_ID_KEY = AttributeKey.newInstance("SESSION_ID_KEY");
    private static AtomicInteger SESSION_ID_CREATOR = new AtomicInteger();
    protected final static EventLoopGroup bossGroup;
    protected final static EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private Session serverSession;
    private String serverName;
    private ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();

    static {
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup();
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
        }
    }

    protected AbstractNettyServer(String serverName) {
        this.serverName = Conditions.notNull(serverName, "server name");
        bootstrap = new ServerBootstrap();
        if (Epoll.isAvailable()) {
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024).channel(EpollServerSocketChannel.class)
                    .childOption(ChannelOption.SO_LINGER, 0).childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            log.info(serverName + " epoll init");
        } else {
            bootstrap.channel(NioServerSocketChannel.class);
            log.info(serverName + " nio init");
        }
        bootstrap.group(bossGroup, workerGroup).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) {
                initPipeline(ch.pipeline());
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public boolean isSharable() {
                        return true;
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        log.error(ctx.channel() + " cause:", cause);
                        exceptionThrown(session(ctx.channel()), cause);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        log.info(ctx.channel().remoteAddress() + " connected！");
                        int ID = SESSION_ID_CREATOR.incrementAndGet();
                        ctx.channel().attr(SESSION_ID_KEY).set(ID);
                        Session session = new Session(ID, ctx.channel());
                        sessions.put(session.ID(), session);
                        sessionActive(session, true);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        log.info(ctx.channel().remoteAddress() + " disconnected！");
                        Session remove = sessions.remove(ctx.channel().attr(SESSION_ID_KEY).get());
                        sessionActive(remove, false);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        messageReceived(session(ctx.channel()), (ByteBuf) msg);
                    }
                });
            }
        });
    }

    protected abstract void initPipeline(ChannelPipeline pipeline);

    protected abstract void exceptionThrown(Session session, Throwable cause) throws Exception;

    protected abstract void sessionActive(Session session, boolean activeOrNot) throws Exception;

    protected abstract void messageReceived(Session session, ByteBuf buf) throws Exception;

    /**
     * bind without block
     *
     * @param port     端口
     * @param listener 绑定回调
     */
    protected void bind(int port, Proc1<Session> listener) {
        bootstrap.bind(port).addListener((ChannelFuture arg0) -> {
            if (arg0.isSuccess()) {
                log.info(serverName + " bind at port:" + port);
                serverSession = new Session(0, arg0.channel());
                Procs.invoke(listener, serverSession);
            } else {
                log.error(serverName + " bind at port error:" + arg0.cause());
            }
        });
    }

    /**
     * unbind port
     */
    protected void unbind() {
        if (serverSession != null) {
            serverSession.close();
        }
    }

    /**
     * 获取服务器名称
     *
     * @return 服务器名称
     */
    public String serverName() {
        return serverName;
    }

    /**
     * 通过channel映射Session
     *
     * @param channel 通道
     * @return 会话
     */
    protected Session session(Channel channel) {
        return sessions.get(channel.attr(SESSION_ID_KEY).get());
    }
}
