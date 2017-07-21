package org.slingerxv.limitart.game.poker.texas;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.util.RandomUtil;

@SuppressWarnings("unused")
public class TXPotPoolTest {
	private TXPotPool pool;
	private long[][] datas;
	private String key;
	private List<Integer> roles;
	private long chips;
	 
	@Before
	public void setUp() throws Exception {
		pool = new TXPotPool();
		datas = new long[100][];
		for(int i=0;i<100;++i){
			long[] data = new long[9];
			for(int j=0;j<data.length;++j){
				data[j] = RandomUtil.randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
			}
			datas[i] = data;
		}
		key = "";
		roles = new ArrayList<>();
		for(int i=0;i<5;++i){
			roles.add(RandomUtil.randomInt(0, 9));
			key += roles.get(i).toString();
		}
		chips = RandomUtil.randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	@Test
	public void testCalTrigger() {
		for(int i=0;i<datas.length;++i){
			long min = Long.MAX_VALUE;
			for(int j=0;j<datas[i].length;++j){
				if(min>datas[i][j]){
					min = datas[i][j];
				}
			}
			pool.calTrigger(datas[i], min, 1);
		}
	}

	@Test
	public void testGetSumChips() {
		for(int i=0;i<datas.length;++i){
			long min = Long.MAX_VALUE;
			for(int j=0;j<datas[i].length;++j){
				if(min>datas[i][j]){
					min = datas[i][j];
				}
			}
			pool.calTrigger(datas[i], min, 1);
			pool.getSumChips();
		}
	}

	@Test
	public void testFlushAward() {
		pool.flushAward((key, roles, chips) -> {
			System.out.println();
		});
	}

}
