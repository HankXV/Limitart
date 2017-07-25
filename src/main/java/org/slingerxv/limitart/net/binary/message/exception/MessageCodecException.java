package org.slingerxv.limitart.net.binary.message.exception;

public class MessageCodecException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageCodecException(String info) {
		super(info);
	}

	public MessageCodecException(Throwable ex) {
		super(ex);
	}
}
