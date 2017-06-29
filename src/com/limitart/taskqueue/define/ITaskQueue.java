package com.limitart.taskqueue.define;

import com.limitart.taskqueue.exception.TaskQueueException;

public interface ITaskQueue<T> {
	void startServer();

	void stopServer();

	void addCommand(T t) throws TaskQueueException;

	String getThreadName();

}