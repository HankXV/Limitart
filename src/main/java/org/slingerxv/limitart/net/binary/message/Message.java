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
package org.slingerxv.limitart.net.binary.message;


import org.slingerxv.limitart.net.binary.BinaryClient;
import org.slingerxv.limitart.net.binary.BinaryServer;

import io.netty.channel.Channel;

/**
 * 消息实体
 * 
 * @author Hank
 *
 */
public abstract class Message extends MessageMeta {
	// 消息由什么通道过来
	private transient Channel channel;
	// 当前Message接受的客户端
	private transient BinaryClient client;
	// 当前Message接收的服务器
	private transient BinaryServer server;
	// 预留参数
	private transient Object extra;
	private transient Object extra1;

	public abstract short getMessageId();

	public BinaryClient getClient() {
		return client;
	}

	public void setClient(BinaryClient client) {
		this.client = client;
	}

	public BinaryServer getServer() {
		return server;
	}

	public void setServer(BinaryServer server) {
		this.server = server;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Object getExtra() {
		return extra;
	}

	public void setExtra(Object extra) {
		this.extra = extra;
	}

	public Object getExtra1() {
		return extra1;
	}

	public void setExtra1(Object extra1) {
		this.extra1 = extra1;
	}
}