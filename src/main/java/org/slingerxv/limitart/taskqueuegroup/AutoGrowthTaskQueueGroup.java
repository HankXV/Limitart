/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.slingerxv.limitart.taskqueuegroup;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Func1;
import org.slingerxv.limitart.taskqueue.define.ITaskQueue;
import org.slingerxv.limitart.taskqueue.exception.TaskQueueException;
import org.slingerxv.limitart.taskqueuegroup.struct.AutoGrowthEntity;
import org.slingerxv.limitart.taskqueuegroup.struct.AutoGrowthSegment;

/**
 * 自动增长消息线程组
 * 
 * @author Hank
 *
 */
public class AutoGrowthTaskQueueGroup<T> {
	private static Logger log = LogManager.getLogger();
	private AtomicInteger threadId = new AtomicInteger(0);
	private ConcurrentHashMap<Integer, AutoGrowthSegment<T>> threads = new ConcurrentHashMap<>();
	private int entityCountPerThread;
	private int coreThreadCount;
	private int maxThreadCount;
	private Func1<Integer, ITaskQueue<T>> newTaskQueue;

	public AutoGrowthTaskQueueGroup(int entityCountPerThread, int coreThreadCount, int initThreadCount,
			int maxThreadCount, Func1<Integer, ITaskQueue<T>> newTaskQueue) throws Exception {
		Objects.requireNonNull(newTaskQueue, "taskQueueFactory");
		this.newTaskQueue = newTaskQueue;
		this.maxThreadCount = maxThreadCount;
		this.entityCountPerThread = entityCountPerThread;
		this.coreThreadCount = Math.min(coreThreadCount, this.maxThreadCount);
		if (initThreadCount > 10) {
			log.warn("initThreadCount is too large, less than 10 better!");
		}
		if (maxThreadCount > 50) {
			log.warn("maxThreadCount is too large,less than 50 better!");
		}
		int initCount = Math.min(initThreadCount, this.maxThreadCount);
		log.info("init,entityCountPerThread:" + this.entityCountPerThread + ",initThreadCount:" + initCount
				+ ",coreThreadCount:" + this.coreThreadCount + ",maxThreadCount:" + this.maxThreadCount);
		if (initCount > 0) {
			for (int i = 0; i < initCount; ++i) {
				newGrowthThread();
			}
		}
	}

	public synchronized void registerEntity(AutoGrowthEntity entity) throws Exception {
		if (entity.getThreadIndex() > 0) {
			throw new TaskQueueException("entity has already registered!");
		}
		AutoGrowthSegment<T> thread = null;
		if (threads.size() >= maxThreadCount && maxThreadCount > 0) {
			int min = Integer.MAX_VALUE;
			for (AutoGrowthSegment<T> temp : threads.values()) {
				int size = temp.getEntities().size();
				if (size < min) {
					min = size;
					thread = temp;
				}
			}
		} else {
			for (AutoGrowthSegment<T> temp : threads.values()) {
				if (temp.getEntities().size() < entityCountPerThread) {
					thread = temp;
					break;
				}
			}
		}
		if (thread == null) {
			thread = newGrowthThread();
		}
		thread.getEntities().add(entity);
		entity.setThreadIndex(thread.getThreadIndex());
	}

	public void addCommand(AutoGrowthEntity entity, T t) throws TaskQueueException {
		if (entity.getThreadIndex() == 0) {
			throw new TaskQueueException("entity does not register!");
		}
		this.threads.get(entity.getThreadIndex()).getThread().addCommand(t);
	}

	public synchronized void unregisterEntity(AutoGrowthEntity entity) throws Exception {
		int threadIndex = entity.getThreadIndex();
		if (threadIndex == 0) {
			return;
		}
		if (!threads.containsKey(threadIndex)) {
			throw new TaskQueueException("thread " + threadIndex + " already destroyed！");
		}
		AutoGrowthSegment<T> thread = threads.get(threadIndex);
		if (!thread.getEntities().contains(entity)) {
			throw new TaskQueueException("entity in thread " + threadIndex + " already destroyed！");
		}
		// 注销线程引用
		if (!thread.getEntities().remove(entity)) {
			throw new TaskQueueException("entity in thread " + threadIndex + " destroy failed！");
		}
		entity.setThreadIndex(0);
		log.info(thread.getThread().getThreadName() + " unregistered entity:" + entity);
		if (thread.getEntities().size() <= 0 && threads.size() > this.coreThreadCount) {
			AutoGrowthSegment<T> remove = threads.remove(threadIndex);
			if (remove != null) {
				remove.getThread().stopServer();
			}
		}
	}

	private AutoGrowthSegment<T> newGrowthThread() throws Exception {
		int id = threadId.incrementAndGet();
		AutoGrowthSegment<T> data = new AutoGrowthSegment<>();
		data.setThread(this.newTaskQueue.run(id));
		data.setThreadIndex(id);
		data.getThread().startServer();
		this.threads.put(data.getThreadIndex(), data);
		return data;
	}
}
