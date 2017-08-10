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
package org.slingerxv.limitart.rpcx.center.struct;

import io.netty.channel.Channel;

/**
 * RPC服务器会话
 * 
 * @author Hank
 *
 */
public class ServiceXServerSession {
	// 服务器Id
	private int providerId;
	// RPC服务器Ip
	private String serverIp;
	// RPC服务器端口
	private int serverPort;
	// Socket链接
	private Channel channel;

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public Channel getSession() {
		return channel;
	}

	public void setSession(Channel channel) {
		this.channel = channel;
	}

}
