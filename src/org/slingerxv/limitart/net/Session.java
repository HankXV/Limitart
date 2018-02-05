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

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.Proc2;
import org.slingerxv.limitart.base.Procs;
import org.slingerxv.limitart.collections.ConstraintConcurrentMap;
import org.slingerxv.limitart.collections.ConstraintMap;
import org.slingerxv.limitart.net.binary.BinaryMessageIOException;

import java.net.SocketAddress;

/**
 * 长链接会话
 *
 * @author Hank
 */
public class Session {
    private Channel channel;
    private int ID;
    private ConstraintMap<Integer> params = new ConstraintConcurrentMap<>();

    public Session(int ID, Channel channel) {
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
    public void writeNow(ByteBuf buf, Proc2<Boolean, Throwable> resultCallback) {
        Conditions.notNull(buf, "buf");
        if (!this.channel.isWritable()) {
            Procs.invoke(resultCallback, false, new BinaryMessageIOException("unwritable"));
            return;
        }
        this.channel.writeAndFlush(buf).addListener((ChannelFutureListener) arg0 -> Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause()));
    }

    /**
     * 立即写出数据
     *
     * @param buf
     */
    public void writeNow(ByteBuf buf) {
        writeNow(buf, null);
    }

    /**
     * 是否可写
     *
     * @return
     */
    public boolean writable() {
        return this.channel.isWritable();
    }

    /**
     * 关闭会话
     *
     * @param resultCallback
     */
    public void close(Proc2<Boolean, Throwable> resultCallback) {
        this.channel.close().addListener((ChannelFutureListener) arg0 -> Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause()));
    }

    /**
     * 关闭会话
     */
    public void close() {
        close(null);
    }

    /**
     * 远程地址
     *
     * @return
     */
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }

    /**
     * 本地地址
     *
     * @return
     */
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    /**
     * ID
     *
     * @return
     */
    public int ID() {
        return this.ID;
    }

    /**
     * 获取自定义参数列表
     *
     * @return the params
     */
    public ConstraintMap<Integer> params() {
        return params;
    }

    @Override
    public String toString() {
        return channel.toString();
    }
}
