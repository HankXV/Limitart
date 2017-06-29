package com.limitart.game.bag.exception;

/**
 * 背包格子被占用异常
 * 
 * @author hank
 *
 */
public class BagGridOcuppiedException extends Exception {

	private static final long serialVersionUID = 1L;

	public BagGridOcuppiedException(int gridId) {
		super(String.valueOf(gridId));
	}
}
