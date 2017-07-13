package org.slingerxv.limitart.taskqueuegroup.struct;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自动增长触发单位
 * 
 * @author Hank
 *
 */
public class AutoGrowthEntity {
	private transient AtomicInteger threadIndex = new AtomicInteger(0);

	public int getThreadIndex() {
		return threadIndex.get();
	}

	public void setThreadIndex(int threadIndex) {
		this.threadIndex.set(threadIndex);
	}
}
