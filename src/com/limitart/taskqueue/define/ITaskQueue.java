package com.limitart.taskqueue.define;

import com.limitart.net.define.IServer;
import com.limitart.taskqueue.exception.TaskQueueException;

public interface ITaskQueue<T> extends IServer {

	void addCommand(T t) throws TaskQueueException;

	String getThreadName();

}