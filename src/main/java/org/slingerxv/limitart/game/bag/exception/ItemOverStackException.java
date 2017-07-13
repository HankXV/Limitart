package org.slingerxv.limitart.game.bag.exception;

/**
 * 物品堆叠超过上限异常
 * 
 * @author hank
 *
 */
public class ItemOverStackException extends Exception {
	private static final long serialVersionUID = 1L;

	public ItemOverStackException(int capacity) {
		super(String.valueOf(capacity));
	}
}
