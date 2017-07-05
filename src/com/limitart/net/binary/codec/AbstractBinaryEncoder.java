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
			buf.writeShort(0);
			buf.writeShort(messageId);
		}

		@Override
		public void afterWriteBody(ByteBuf buf) {
			buf.setShort(0, buf.readableBytes() - Short.BYTES);
		}

	};

	public abstract void beforeWriteBody(ByteBuf buf, short messageId);

	public abstract void afterWriteBody(ByteBuf buf);
}
