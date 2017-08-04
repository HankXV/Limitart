package org.slingerxv.limitart.net.binary.message.exception;

import io.netty.channel.Channel;

public class HeartNotAnswerException extends Exception {
	private static final long serialVersionUID = 1L;

	public HeartNotAnswerException(Channel channel, long first, long last, int realCount) {
		super(channel + ",first heart:" + first + ",last:" + last + ",realCount:" + realCount);
	}
}
