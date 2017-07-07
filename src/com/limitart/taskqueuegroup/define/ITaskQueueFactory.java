package com.limitart.taskqueuegroup.define;

import com.limitart.taskqueue.define.ITaskQueue;

public interface ITaskQueueFactory<T> {
	ITaskQueue<T> newTaskQueue(int threadId);
}
