package org.slingerxv.limitart.net.binary.message.exception;

import io.netty.channel.Channel;

public class HeartTooQuickException extends Exception {

	private static final long serialVersionUID = 1L;

	public HeartTooQuickException(Channel channel, long first, long now, int realCount, int theoreticalValue) {
		super(channel + ",first heart:" + first + ",now:" + now + ",realCount:" + realCount + ",theoreticalValue:"
				+ theoreticalValue + ",delta:" + (realCount - theoreticalValue));
	}
}
