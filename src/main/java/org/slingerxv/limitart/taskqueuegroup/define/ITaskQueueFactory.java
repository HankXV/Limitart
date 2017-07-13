package org.slingerxv.limitart.taskqueuegroup.define;

import org.slingerxv.limitart.taskqueue.define.ITaskQueue;

public interface ITaskQueueFactory<T> {
	ITaskQueue<T> newTaskQueue(int threadId);
}
