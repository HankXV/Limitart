package org.slingerxv.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slingerxv.limitart.funcs.Proc3;
import org.slingerxv.limitart.funcs.Procs;

/**
 * 德州扑克奖池
 * 
 * @author hank
 *
 */
public class TXPotPool {
	private Map<String, TXPot> pots = new HashMap<>();

	/**
	 * 每轮触发的计算
	 * 
	 * @param bets
	 *            座位索引对应玩家当轮的注数
	 */
	/**
	 * 每轮触发的计算
	 * 
	 * @param bets
	 *            座位索引对应玩家当轮的注数
	 * @param smallBlind
	 *            小盲注数值
	 * @param times
	 *            递归次数，调用时为1
	 */
	public void calTrigger(long[] bets, long smallBlind, int times) {
		long min = Long.MAX_VALUE;
		for (long bet : bets) {
			if (bet == 0) {
				continue;
			}
			if (bet < min) {
				min = bet;
			}
		}
		long needReduce = 0;
		if (min == smallBlind && times == 1) {
			min = smallBlind * 2;
			needReduce = smallBlind;
		}
		StringBuilder buffer = new StringBuilder();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < bets.length; ++i) {
			if (bets[i] == 0) {
				continue;
			}
			bets[i] -= Math.min(bets[i], min);
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
		sidePot.chips += min * list.size() - needReduce;
		calTrigger(bets, smallBlind, ++times);
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
