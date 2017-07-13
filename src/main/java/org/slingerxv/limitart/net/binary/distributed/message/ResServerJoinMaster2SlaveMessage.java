package org.slingerxv.limitart.net.binary.distributed.message;

import java.util.ArrayList;
import java.util.List;

import org.slingerxv.limitart.net.binary.distributed.message.constant.DistributedMessageEnum;

public class ResServerJoinMaster2SlaveMessage extends Message {
	public List<InnerServerInfo> infos = new ArrayList<>();

	@Override
	public short getMessageId() {
		return DistributedMessageEnum.ResServerJoinMaster2SlaveMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putMessageMetaList(this.infos);
	}

	@Override
	public void decode() throws Exception {
		this.infos = getMessageMetaList(InnerServerInfo.class);
	}

}
