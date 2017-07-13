package org.slingerxv.limitart.rpcx.center.struct;

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
