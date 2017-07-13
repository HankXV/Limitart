package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.Message;

public class BinaryMessageDemo extends Message {
	public String info;

	// 消息编号
	@Override
	public short getMessageId() {
		return 1;
	}

	// 编码
	@Override
	public void encode() throws Exception {
		putString(this.info);
	}

	// 解码
	@Override
	public void decode() throws Exception {
		this.info = getString();
	}

}
