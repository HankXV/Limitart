package com.limitart.net.binary.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 消息解码器
 * 
 * @author Hank
 *
 */
public class ByteDecoder extends LengthFieldBasedFrameDecoder {

	public ByteDecoder() {
		this(1024 * 1024);
	}

	public ByteDecoder(int dataMaxLength) {
		super(dataMaxLength, 0, 2, 0, 2);
	}
}
