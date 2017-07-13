package org.slingerxv.limitart.net.binary.message.exception;

public class MessageIDDuplicatedException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageIDDuplicatedException(String info) {
		super(info);
	}
}
