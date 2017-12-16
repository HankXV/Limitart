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
package org.slingerxv.limitart.net.binary.distributed.struct;


import org.slingerxv.limitart.net.binary.BinaryClient;

import io.netty.channel.Channel;

/**
 * 服务器数据
 * 
 * @author Hank
 *
 */
public class InnerServerData {
	// 服务器Id
	private int serverId;
	private int serverType;
	// 对客户端开放服务器Ip
	private String outIp;
	// 对客户端开放端口
	private int outPort;
	// 对内部服务器开放端口
	private int innerPort;
	// 对客户端链接验证
	private String outPass;
	// 服务器负载指数
	private int serverLoad;
	private Channel channel;
	private BinaryClient binaryClient;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getOutIp() {
		return outIp;
	}

	public void setOutIp(String outIp) {
		this.outIp = outIp;
	}

	public int getOutPort() {
		return outPort;
	}

	public void setOutPort(int outPort) {
		this.outPort = outPort;
	}

	public int getInnerPort() {
		return innerPort;
	}

	public void setInnerPort(int innerPort) {
		this.innerPort = innerPort;
	}

	public String getOutPass() {
		return outPass;
	}

	public void setOutPass(String outPass) {
		this.outPass = outPass;
	}

	public int getServerLoad() {
		return serverLoad;
	}

	public void setServerLoad(int serverLoad) {
		this.serverLoad = serverLoad;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "InnerServerData [serverId=" + serverId + ", outIp=" + outIp + ", outPort=" + outPort + ", innerPort="
				+ innerPort + ", outPass=" + outPass + ", serverLoad=" + serverLoad + ", channel=" + channel + "]";
	}

	public BinaryClient getBinaryClient() {
		return binaryClient;
	}

	public void setBinaryClient(BinaryClient binaryClient) {
		this.binaryClient = binaryClient;
	}

	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

}
