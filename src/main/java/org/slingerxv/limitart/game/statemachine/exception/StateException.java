package org.slingerxv.limitart.game.statemachine.exception;

/**
 * 状态机异常
 * 
 * @author hank
 *
 */
public class StateException extends Exception {
	private static final long serialVersionUID = 1L;

	public StateException(String info) {
		super(info);
	}
}
