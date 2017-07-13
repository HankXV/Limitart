package org.slingerxv.limitart.thread;

import java.util.concurrent.ThreadFactory;

public abstract class NamedThreadFactory implements ThreadFactory {

	public abstract String getThreadName();

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, this.getThreadName());
		return t;
	}

}
