/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.taskqueue;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.funcs.Procs;
import org.slingerxv.limitart.funcs.Test1;
import org.slingerxv.limitart.funcs.Tests;
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
	private Test1<T> intercept;
	private Proc1<T> handle;
	private Proc2<T, Throwable> exception;

	public LinkedBlockingTaskQueue(String threadName) {
		setName(threadName);
	}

	public ITaskQueue<T> intercept(Test1<T> intercept) {
		this.intercept = intercept;
		return this;
	}

	public ITaskQueue<T> handle(Proc1<T> handle) {
		this.handle = handle;
		return this;
	}

	public ITaskQueue<T> exception(Proc2<T, Throwable> exception) {
		this.exception = exception;
		return this;
	}

	@Override
	public void run() {
		start = true;
		while (start || !queue.isEmpty()) {
			T take = null;
			try {
				take = queue.take();
				if (Tests.invoke(intercept, take)) {
					continue;
				}
				Procs.invoke(handle, take);
			} catch (Exception e) {
				log.error(e, e);
				Procs.invoke(exception, take, e);
			}
		}
	}

	@Override
	public void addCommand(T command) {
		Objects.requireNonNull(command, "command");
		queue.offer(command);
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
