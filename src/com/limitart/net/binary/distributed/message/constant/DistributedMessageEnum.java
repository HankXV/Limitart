package com.limitart.net.binary.distributed.message.constant;

public enum DistributedMessageEnum {
	ReqConnectionReportSlave2MasterMessage((short)-101),
	ReqServerLoadSlave2MasterMessage((short)-102),
	ResServerJoinMaster2SlaveMessage((short)-103),
	ResServerQuitMaster2SlaveMessage((short)-104);
	private short messageId;

	DistributedMessageEnum(short messageId) {
		this.messageId = messageId;
	}

	public short getValue() {
		return this.messageId;
	}
}
