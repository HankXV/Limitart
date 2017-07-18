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
	public String toString() {
		return "InnerServerInfo [serverType=" + serverType + ", serverId=" + serverId + ", outIp=" + outIp
				+ ", outPort=" + outPort + ", innerPort=" + innerPort + ", outPass=" + outPass + "]";
	}
}
