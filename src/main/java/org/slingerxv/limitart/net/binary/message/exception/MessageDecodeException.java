package org.slingerxv.limitart.net.binary.message.exception;

public class MessageDecodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageDecodeException(String info) {
		super(info);
	}

	public MessageDecodeException(Throwable ex) {
		super(ex);
	}
}
