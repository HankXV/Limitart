package org.slingerxv.limitart.taskqueue.define;

import org.slingerxv.limitart.net.define.IServer;
import org.slingerxv.limitart.taskqueue.exception.TaskQueueException;

public interface ITaskQueue<T> extends IServer {

	void addCommand(T t) throws TaskQueueException;

	String getThreadName();
}