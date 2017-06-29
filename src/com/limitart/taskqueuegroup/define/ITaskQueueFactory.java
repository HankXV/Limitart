package com.limitart.taskqueuegroup.define;

import com.limitart.taskqueue.define.ITaskQueue;

public interface ITaskQueueFactory<T> {
	public ITaskQueue<T> newTaskQueue(int threadId);
}
