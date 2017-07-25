package org.slingerxv.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.util.RandomUtil;

@SuppressWarnings("unused")
public class TXPotPoolTest {
	private TXPotPool pool;
	private long[] data;
	private long min;
	private String key;
	private List<Integer> roles;
	private long chips;
	private long sum;
	private int times;

	@Before
	public void setUp() throws Exception {
		pool = new TXPotPool();
		data = new long[9];
		roles = new ArrayList<>();
		sum = 0;
		for (int i = 0; i < 9; ++i) {
			if (i == 0) {
				min = i + 1;
			}
			if (i == 8) {
				key = "" + i;
			}
			data[i] = i + 1;
			roles.add(i);
			sum += i;
		}
		chips = RandomUtil.randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Test
	public void testGetSumChips() {
		pool.calTrigger(data, new HashSet<>(), data[data.length-1]);
		long[] sumChips = pool.getSumChips();
		Assert.assertFalse((sumChips[0] <= 0 || sumChips[1] <= 0) && sumChips[0] + sumChips[1] == sum);
	}

	@Test
	public void testFlushAward() {
		HashSet<Integer> hashSet = new HashSet<>();
		for(int i=0;i<data.length-2;++i){
			hashSet.add(i);
		}
		pool.calTrigger(data, hashSet, data[data.length-1]);
		times = 0;
		pool.flushAward((key, roles, chips) -> {
			++times;
		});
		Assert.assertFalse(times != data.length - 1);
	}

}
