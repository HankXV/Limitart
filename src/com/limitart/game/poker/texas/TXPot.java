package com.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TXPot {
	// 奖池数量
	private long chips;
	// 放入该边池筹码的玩家作为索引
	private List<Integer> roles;

	public long getChips() {
		return chips;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public static void calTrigger(long[] bets, Map<String, TXPot> pots) {
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
		calTrigger(bets, pots);
	}
}
