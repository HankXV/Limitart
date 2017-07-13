package org.slingerxv.limitart.rpcx.center.struct;

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
