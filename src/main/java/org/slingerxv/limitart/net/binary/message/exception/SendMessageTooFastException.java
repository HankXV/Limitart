package org.slingerxv.limitart.net.binary.message.exception;

import io.netty.channel.Channel;

public class SendMessageTooFastException extends Exception {

	private static final long serialVersionUID = 1L;

	public SendMessageTooFastException(Channel channel, int allow, int real) {
		super(channel + " send message too fast,allow:" + allow + ",real:" + real);
	}
}
