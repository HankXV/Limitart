package org.slingerxv.limitart.net.console;

public class CommandDuplicatedException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommandDuplicatedException(String info) {
		super(info);
	}
}
