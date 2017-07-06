package com.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.limitart.game.poker.texas.listener.ITXPotListener;

/**
 * 德州扑克奖池
 * 
 * @author hank
 *
 */
public class TXPotPool {
	Map<String, TXPot> pots = new HashMap<>();
	ITXPotListener listener;

	public TXPotPool(ITXPotListener listener) {
		this.listener = listener;
	}

	/**
	 * 每轮触发的计算
	 * 
	 * @param bets
	 *            座位索引对应玩家当轮的注数
	 */
	public void calTrigger(long[] bets) {
		long min = Long.MAX_VALUE;
		for (long bet : bets) {
			if (bet == 0) {
				continue;
			}
			if (bet < min) {
				min = bet;
			}
		}
		StringBuilder buffer = new StringBuilder();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < bets.length; ++i) {
			if (bets[i] == 0) {
				continue;
			}
			bets[i] -= min;
			list.add(i);
			buffer.append(i);
		}
		if (list.isEmpty()) {
			return;
		}
		String key = buffer.toString();
		TXPot sidePot = pots.get(key);
		if (sidePot == null) {
			sidePot = new TXPot();
			sidePot.key = key;
			sidePot.roles = list;
			pots.put(key, sidePot);
		}
		sidePot.chips += min * list.size();
		calTrigger(bets);
	}

	/**
	 * 获取奖池总奖金
	 * 
	 * @return long[0] 有效奖金 long[1] 无效奖金(退还奖金)
	 */
	public long[] getSumChips() {
		long[] result = new long[2];
		for (TXPot pot : pots.values()) {
			if (pot.roles.size() > 1) {
				result[0] += pot.chips;
			} else {
				result[1] += pot.chips;
			}
		}
		return result;
	}

	/**
	 * 触发发奖
	 */
	public void flushAward() {
		for (TXPot pot : pots.values()) {
			listener.onAward(pot.key, pot.roles, pot.chips);
		}
	}

	private class TXPot {
		private String key;
		// 奖池数量
		private long chips;
		// 放入该边池筹码的玩家作为索引
		private List<Integer> roles;
	}
}
