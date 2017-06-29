package com.limitart.rpcx.center.struct;

import io.netty.channel.Channel;

/**
 * RPC客户端会话
 * 
 * @author Hank
 *
 */
public class ServiceXClientSession {
	// 客户端Socket
	private Channel channel;

	public Channel getSession() {
		return channel;
	}

	public void setSession(Channel channel) {
		this.channel = channel;
	}

}
