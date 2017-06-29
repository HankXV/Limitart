package com.limitart.game.org.exception;

public class AuthIDDuplicatedException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthIDDuplicatedException(String info) {
		super(info);
	}
}
