package org.slingerxv.limitart.net.binary.distributed.message;

import org.slingerxv.limitart.net.binary.message.MessageMeta;

public class InnerServerInfo extends MessageMeta {
	public int serverType;
	public int serverId;
	public String outIp;
	public int outPort;
	public int innerPort;
	public String outPass;

	@Override
	public void encode() throws Exception {
		putInt(this.serverType);
		putInt(this.serverId);
		putString(this.outIp);
		putInt(this.outPort);
		putInt(this.innerPort);
		putString(this.outPass);
	}

	@Override
	public void decode() throws Exception {
		this.serverType = getInt();
		this.serverId = getInt();
		this.outIp = getString();
		this.outPort = getInt();
		this.innerPort = getInt();
		this.outPass = getString();
	}

	@Override
	public String toString() {
		return "InnerServerInfo [serverType=" + serverType + ", serverId=" + serverId + ", outIp=" + outIp
				+ ", outPort=" + outPort + ", innerPort=" + innerPort + ", outPass=" + outPass + "]";
	}
}
