package com.limitart.net.binary.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 二进制解码器
 * 
 * @author hank
 *
 */
public abstract class AbstractBinaryDecoder extends LengthFieldBasedFrameDecoder {
	public static final AbstractBinaryDecoder DEFAULT_DECODER = new AbstractBinaryDecoder(Short.MAX_VALUE, 0, 2, 0, 2) {

		@Override
		public short readMessageId(Channel channel, ByteBuf buffer) {
			return buffer.readShort();
		}
	};

	public AbstractBinaryDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
	}

	/**
	 * 自定义处理完buffer后返回消息Id
	 * 
	 * @param channel
	 * @param buffer
	 * @return
	 */
	public abstract short readMessageId(Channel channel, ByteBuf buffer);
}
