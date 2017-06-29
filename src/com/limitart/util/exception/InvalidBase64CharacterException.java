package com.limitart.util.exception;

public class InvalidBase64CharacterException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public InvalidBase64CharacterException(String message) {
		super(message);
	}
}