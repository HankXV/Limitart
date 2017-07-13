package org.slingerxv.limitart.taskqueue;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.taskqueue.define.ITaskQueue;
import org.slingerxv.limitart.taskqueue.define.ITaskQueueHandler;

/**
 * 普通消息队列(TaskQueue更好)
 * 
 * @author hank
 * @see DisruptorTaskQueue
 * @param <T>
 */
public class LinkedBlockingTaskQueue<T> extends Thread implements ITaskQueue<T> {
	private static Logger log = LogManager.getLogger();
	private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();
	private boolean start = false;
	private ITaskQueueHandler<T> handler;

	public LinkedBlockingTaskQueue(String threadName, ITaskQueueHandler<T> handler) {
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		this.handler = handler;
		setName(threadName);
	}

	@Override
	public void run() {
		start = true;
		while (start || !queue.isEmpty()) {
			T take = null;
			try {
				take = queue.take();
			} catch (InterruptedException e) {
				log.error(e, e);
			}
			try {
				handler.handle(take);
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	@Override
	public void addCommand(T t) {
		if (t == null) {
			throw new NullPointerException("t");
		}
		queue.offer(t);
	}

	@Override
	public void stopServer() {
		start = false;
	}

	@Override
	public void startServer() {
		start();
	}

	@Override
	public String getThreadName() {
		return getName();
	}
}
