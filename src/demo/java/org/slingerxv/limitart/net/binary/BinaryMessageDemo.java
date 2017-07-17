package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.binary.message.Message;

public class BinaryMessageDemo extends Message {
	// 传递的信息 transfer of information
	public String info;

	// 消息编号 message id
	@Override
	public short getMessageId() {
		return 1;
	}

	// 编码encode
	@Override
	public void encode() throws Exception {
		putString(this.info);
	}

	// 解码decode
	@Override
	public void decode() throws Exception {
		this.info = getString();
	}
}
