package org.slingerxv.limitart.net.binary.distributed.struct;

import org.slingerxv.limitart.net.binary.client.BinaryClient;

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
