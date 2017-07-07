package com.limitart.game.innerserver;

public enum InnerMessageEnum {
	ReqConnectionReportGame2FightMessage((short)-101),
	ReqConnectionReportSlave2MasterMessage((short)-102),
	ReqServerLoadSlave2MasterMessage((short)-103),
	ResFightServerJoinMaster2GameMessage((short)-104),
	ResFightServerQuitMaster2GameMessage((short)-105);
	private short messageId;

	InnerMessageEnum(short messageId) {
		this.messageId = messageId;
	}

	public short getValue() {
		return this.messageId;
	}
}
