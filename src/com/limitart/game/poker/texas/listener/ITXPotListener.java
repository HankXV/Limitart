package com.limitart.game.poker.texas.listener;

import java.util.List;

public interface ITXPotListener {
	/**
	 * 发奖
	 * 
	 * @param index
	 *            玩家位置索引
	 * @param chips
	 *            玩家得到的钱
	 * @param winOrReturn
	 *            是赢的还是返还的
	 */
	public void onAward(int index, long chips, boolean winOrReturn);

	public List<Integer> whoWins(List<Integer> roles);
}
