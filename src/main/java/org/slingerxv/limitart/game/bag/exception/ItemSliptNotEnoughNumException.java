package org.slingerxv.limitart.game.bag.exception;

/**
 * 拆分数量不足异常
 * 
 * @author hank
 *
 */
public class ItemSliptNotEnoughNumException extends Exception {
	private static final long serialVersionUID = 1L;

	public ItemSliptNotEnoughNumException(int haveNum, int yourNum) {
		super("have num:" + haveNum + ",your num:" + yourNum);
	}
}
