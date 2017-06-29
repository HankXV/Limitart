package com.limitart.rpcx.message.service;

import com.limitart.net.binary.message.Message;
import com.limitart.rpcx.message.constant.RpcMessageEnum;

public class NoticeProviderDisconnectedServiceCenterMessage extends Message {
	private int providerUID;

	public int getProviderUID() {
		return providerUID;
	}

	public void setProviderUID(int providerUID) {
		this.providerUID = providerUID;
	}

	@Override
	public short getMessageId() {
		return RpcMessageEnum.NoticeProviderDisconnectedServiceCenterMessage.getValue();
	}

	@Override
	public void encode() throws Exception {
		putInt(this.providerUID);
	}

	@Override
	public void decode() throws Exception {
		this.providerUID = getInt();
	}

}
