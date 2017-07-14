package org.slingerxv.limitart.taskqueue;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Func1;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.taskqueue.define.ITaskQueue;

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
	private Func1<T, Boolean> intercept;
	private Proc1<T> handle;

	public LinkedBlockingTaskQueue(String threadName) {
		setName(threadName);
	}

	public ITaskQueue<T> intercept(Func1<T, Boolean> intercept) {
		this.intercept = intercept;
		return this;
	}

	public ITaskQueue<T> handle(Proc1<T> handle) {
		this.handle = handle;
		return this;
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
			if (intercept != null && intercept.run(take)) {
				continue;
			}
			try {
				if (handle != null) {
					handle.run(take);
				}
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
