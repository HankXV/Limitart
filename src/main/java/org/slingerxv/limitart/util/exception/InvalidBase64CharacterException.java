package org.slingerxv.limitart.util.exception;

public class InvalidBase64CharacterException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidBase64CharacterException(String message) {
		super(message);
	}
}