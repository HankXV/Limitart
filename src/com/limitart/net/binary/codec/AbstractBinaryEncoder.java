package com.limitart.net.binary.codec;

import io.netty.buffer.ByteBuf;

/**
 * 二进制编码器
 * 
 * @author hank
 *
 */
public abstract class AbstractBinaryEncoder {
	public static final AbstractBinaryEncoder DEFAULT_ENCODER = new AbstractBinaryEncoder() {

		@Override
		public void beforeWriteBody(ByteBuf buf, short messageId) {
			// 消息长度(包括消息Id)
			buf.writeShort(0);
			// 消息Id
			buf.writeShort(messageId);
		}

		@Override
		public void afterWriteBody(ByteBuf buf) {
			// 重设消息长度
			buf.setShort(0, buf.readableBytes() - Short.BYTES);
		}

	};

	public abstract void beforeWriteBody(ByteBuf buf, short messageId);

	public abstract void afterWriteBody(ByteBuf buf);
}
