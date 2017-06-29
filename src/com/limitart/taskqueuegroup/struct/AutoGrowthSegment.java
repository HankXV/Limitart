package com.limitart.taskqueuegroup.struct;

import com.limitart.collections.ConcurrentHashSet;
import com.limitart.taskqueue.define.ITaskQueue;

/**
 * 线程容器
 * 
 * @author Hank
 *
 */
public class AutoGrowthSegment<T> {
	private int threadIndex;
	private ITaskQueue<T> thread;
	private ConcurrentHashSet<AutoGrowthEntity> entities = new ConcurrentHashSet<>();

	public ConcurrentHashSet<AutoGrowthEntity> getEntities() {
		return entities;
	}

	public void setEntities(ConcurrentHashSet<AutoGrowthEntity> entities) {
		this.entities = entities;
	}

	public int getThreadIndex() {
		return threadIndex;
	}

	public void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}

	public ITaskQueue<T> getThread() {
		return thread;
	}

	public void setThread(ITaskQueue<T> thread) {
		this.thread = thread;
	}

}
