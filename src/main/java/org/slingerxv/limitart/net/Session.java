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

import java.net.SocketAddress;
import java.util.Objects;

import org.slingerxv.limitart.collections.ConcurrentConstraintMap;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Procs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * 长链接会话
 * 
 * @author Hank
 *
 */
public class Session {
	private Channel channel;
	private String shortID;
	private String longID;
	private ConcurrentConstraintMap<Integer> params = new ConcurrentConstraintMap<>();

	public Session(Channel channel) {
		Objects.requireNonNull(channel, "channel");
		this.channel = channel;
		this.shortID = this.channel.id().asShortText();
		this.longID = this.channel.id().asLongText();
	}

	/**
	 * 立即写出数据
	 * 
	 * @param buf
	 * @param resultCallback
	 * @throws MessageIOException
	 */
	public void writeNow(ByteBuf buf, Proc2<Boolean, Throwable> resultCallback) {
		if (!this.channel.isWritable()) {
			Procs.invoke(resultCallback, false, new MessageIOException("unwritable"));
			return;
		}
		this.channel.writeAndFlush(buf).addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause());
		});
	}

	/**
	 * 立即写出数据
	 * 
	 * @param buf
	 * @throws MessageIOException
	 */
	public void writeNow(ByteBuf buf) {
		writeNow(buf, null);
	}

	/**
	 * 关闭会话
	 * 
	 * @param resultCallback
	 */
	public void close(Proc2<Boolean, Throwable> resultCallback) {
		this.channel.close().addListener((ChannelFutureListener) arg0 -> {
			Procs.invoke(resultCallback, arg0.isSuccess(), arg0.cause());
		});
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
	public SocketAddress removeAddress() {
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
	 * 短ID
	 * 
	 * @return
	 */
	public String shortID() {
		return this.shortID;
	}

	/**
	 * 长ID
	 * 
	 * @return
	 */
	public String longID() {
		return this.longID;
	}

	/**
	 * 获取自定义参数列表
	 * 
	 * @return the params
	 */
	public ConcurrentConstraintMap<Integer> params() {
		return params;
	}
}
