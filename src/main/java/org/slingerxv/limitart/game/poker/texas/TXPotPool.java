package org.slingerxv.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.util.Beta;

/**
 * 德州扑克奖池
 * 
 * @author hank
 *
 */
@Beta
public class TXPotPool {
	private Map<String, TXPot> pots = new HashMap<>();
	private List<List<Long>> composition = new ArrayList<>();

	public void calTrigger(long[] bets, HashSet<Integer> allInSeats, long maxBet) {
		long min = Long.MAX_VALUE;
		long tempMin = Long.MAX_VALUE;
		long oldMin = 0;
		for (int i = 0; i < bets.length; ++i) {
			if (bets[i] < min && bets[i] > 0) {
				if (bets[i] < tempMin && bets[i] > oldMin) {
					tempMin = bets[i];
				}
				if (allInSeats.contains(i)) {
					min = bets[i];
				}
			}
			if (i == bets.length - 1 && tempMin == maxBet) {
				min = tempMin;
				break;
			}
			if (i == bets.length - 1 && min == Long.MAX_VALUE) {
				i = 0;
				oldMin = tempMin;
				tempMin = Long.MAX_VALUE;
			}
		}
		TXPot pot = new TXPot();
		List<Integer> roles = new ArrayList<>();
		List<Long> chips = new ArrayList<>();
		StringBuffer key = new StringBuffer();
		for (int i = 0; i < bets.length; ++i) {
			if (bets[i] == 0) {
				chips.add(i, (long) 0);
				continue;
			}
			key.append(i);
			pot.chips += Math.min(min, bets[i]);
			roles.add(i);
			chips.add(i, Math.min(min, bets[i]));
			bets[i] -= Math.min(min, bets[i]);
		}
		pot.key = key.toString();
		pot.roles = roles;
		composition.add(chips);
		if (pots.containsKey(pot.key)) {
			pots.get(pot.key).chips += pot.chips;
		} else {
			pots.put(pot.key, pot);
		}
		if (min == maxBet) {
			return;
		}
		calTrigger(bets, allInSeats, maxBet - min);
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
	public void flushAward(Proc3<String, List<Integer>, Long> listener) {
		for (TXPot pot : pots.values()) {
			Procs.invoke(listener, pot.key, pot.roles, pot.chips);
		}
	}

	public Map<String, TXPot> getPots() {
		return pots;
	}

	public List<List<Long>> getComposition() {
		return composition;
	}

	public class TXPot {
		private String key;
		// 奖池数量
		private long chips;
		// 放入该边池筹码的玩家作为索引
		private List<Integer> roles;

		public String getKey() {
			return key;
		}

		public long getChips() {
			return chips;
		}

		public List<Integer> getRoles() {
			return roles;
		}
	}
}
