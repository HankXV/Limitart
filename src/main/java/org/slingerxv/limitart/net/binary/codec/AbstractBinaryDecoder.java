package org.slingerxv.limitart.net.binary.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * 二进制解码器
 * 
 * @author hank
 *
 */
public abstract class AbstractBinaryDecoder {
	public static final AbstractBinaryDecoder DEFAULT_DECODER = new AbstractBinaryDecoder(Short.MAX_VALUE, 0, 2, 0, 2) {

		@Override
		public short readMessageId(Channel channel, ByteBuf buffer) {
			return buffer.readShort();
		}
	};
	private int maxFrameLength;
	private int lengthFieldOffset;
	private int lengthFieldLength;
	private int lengthAdjustment;
	private int initialBytesToStrip;

	/**
	 * 构造
	 * 
	 * @param maxFrameLength
	 *            每个单位的消息最大长度
	 * @param lengthFieldOffset
	 *            表示长度字节的位在消息段中的偏移量
	 * @param lengthFieldLength
	 *            用了多长的字节表示了长度属性
	 * @param lengthAdjustment
	 *            长度调整(假如编码的长度信息比实际消息体的长度少2，那么此项设置为-2)
	 * @param initialBytesToStrip
	 *            正式解码消息时跳过原始二进制序列的几个字节
	 */
	public AbstractBinaryDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
			int initialBytesToStrip) {
		this.maxFrameLength = maxFrameLength;
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthAdjustment = lengthAdjustment;
		this.initialBytesToStrip = initialBytesToStrip;
	}

	/**
	 * 自定义处理完buffer后返回消息Id
	 * 
	 * @param channel
	 * @param buffer
	 * @return
	 */
	public abstract short readMessageId(Channel channel, ByteBuf buffer);

	public int getMaxFrameLength() {
		return maxFrameLength;
	}

	public int getLengthFieldOffset() {
		return lengthFieldOffset;
	}

	public int getLengthFieldLength() {
		return lengthFieldLength;
	}

	public int getLengthAdjustment() {
		return lengthAdjustment;
	}

	public int getInitialBytesToStrip() {
		return initialBytesToStrip;
	}
}
