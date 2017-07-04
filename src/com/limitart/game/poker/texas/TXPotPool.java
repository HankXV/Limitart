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
			sidePot.roles = list;
			pots.put(key, sidePot);
		}
		sidePot.chips += min * list.size();
		calTrigger(bets);
	}

	/**
	 * 触发发奖
	 */
	public void flushAward() {
		for (TXPot pot : pots.values()) {
			List<Integer> result = new ArrayList<>();
			for (int index : pot.roles) {
				if (!listener.canGetAward(index)) {
					continue;
				}
				result.add(index);
			}
			for (int index : result) {
				listener.onAward(index, pot.chips / result.size());
			}
		}
	}

	private class TXPot {
		// 奖池数量
		private long chips;
		// 放入该边池筹码的玩家作为索引
		private List<Integer> roles;
	}
}
