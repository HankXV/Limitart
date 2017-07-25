package org.slingerxv.limitart.net.binary.exception;

public class BinaryMessageDecodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public BinaryMessageDecodeException(String info) {
		super(info);
	}

	public BinaryMessageDecodeException(Throwable ex) {
		super(ex);
	}
}
