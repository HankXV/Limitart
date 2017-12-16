package org.slingerxv.limitart.util;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.collections.ConcurrentHashSet;

public class UniqueIdUtilTest {
	private Set<Long> sets;
	private LongAdder adder;
	private CountDownLatch count;
	private static int THREAD_COUNT = 100;

	@Before
	public void setUp() throws Exception {
		adder = new LongAdder();
		sets = new ConcurrentHashSet<>();
		count = new CountDownLatch(THREAD_COUNT);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createUUIDLong() {
		for (int i = 0; i < THREAD_COUNT; ++i) {
			new Thread(() -> {
				insert();
			}).start();
		}
		try {
			count.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void insert() {
		for (int i = 0; i < 1000000; ++i) {
			long createUUID = UniqueIdUtil.createUUID(24, adder);
			if (sets.contains(createUUID)) {
				Assert.fail();
			}
		}
		// System.out.println(Thread.currentThread().getName() + " ok!");
		count.countDown();
	}
}
