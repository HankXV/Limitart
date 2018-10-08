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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import top.limitart.base.Conditions;
import top.limitart.base.Proc2;
import top.limitart.base.Procs;
import top.limitart.net.binary.BinaryMessageIOException;

import java.net.SocketAddress;

/**
 * @author hank
 * @version 2018/10/8 0008 17:06
 */
public class NettySession<M> extends AbstractSession<M, EventLoop> {
    private final Channel channel;
    private final int ID;

    public NettySession(int ID, Channel channel) {
        Conditions.notNull(channel, "channel");
        this.channel = channel;
        this.ID = ID;
    }

    /**
     * 立即写出数据
     *
     * @param buf
     * @param resultCallback
     */
    @Override
    public void writeNow(M buf, Proc2<Boolean, Throwable> resultCallback) {
        Conditions.notNull(buf, "buf");
        if (!writable()) {
            Procs.invoke(resultCallback, false, new BinaryMessageIOException("unwritable"));
            return;
        }
        this.channel.writeAndFlush(buf).addListener((ChannelFutureListener) arg0 -> Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause()));
    }

    @Override
    public void writeNow(byte[] buf, Proc2<Boolean, Throwable> resultCallback) {
        Conditions.notNull(buf, "buf");
        if (!writable()) {
            Procs.invoke(resultCallback, false, new BinaryMessageIOException("unwritable"));
            return;
        }
        this.channel.writeAndFlush(buf).addListener((ChannelFutureListener) arg0 -> Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause()));
    }

    /**
     * 是否可写
     *
     * @return
     */
    @Override
    public boolean writable() {
        return this.channel.isWritable();
    }

    /**
     * 关闭会话
     *
     * @param resultCallback
     */
    @Override
    public void close(Proc2<Boolean, Throwable> resultCallback) {
        this.channel.close().addListener((ChannelFutureListener) arg0 -> Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause()));
    }


    /**
     * 远程地址
     *
     * @return
     */
    @Override
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }

    /**
     * 本地地址
     *
     * @return
     */
    @Override
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    /**
     * ID
     *
     * @return
     */
    @Override
    public int ID() {
        return this.ID;
    }

    @Override
    public EventLoop thread() {
        return channel.eventLoop();
    }

    @Override
    public String toString() {
        return channel.toString();
    }
}
