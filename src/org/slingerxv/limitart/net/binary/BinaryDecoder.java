/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.net.binary;

import org.slingerxv.limitart.net.Session;

import io.netty.buffer.ByteBuf;

/**
 * 二进制解码器
 * 
 * @author hank
 *
 */
public abstract class BinaryDecoder {
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
	public BinaryDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
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
	 * @param session
	 * @param buffer
	 * @return
	 */
	public abstract short readMessageId(Session session, ByteBuf buffer);

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
