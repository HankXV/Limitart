package com.limitart.taskqueue.define;

public interface ITaskQueueHandler<T> {

	boolean intercept(T t);

	void handle(T t);
}
