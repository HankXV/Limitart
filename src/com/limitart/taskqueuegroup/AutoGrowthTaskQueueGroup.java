package com.limitart.taskqueuegroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.taskqueue.exception.TaskQueueException;
import com.limitart.taskqueuegroup.define.ITaskQueueFactory;
import com.limitart.taskqueuegroup.struct.AutoGrowthEntity;
import com.limitart.taskqueuegroup.struct.AutoGrowthSegment;

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
	private int initThreadCount;
	private int coreThreadCount;
	private int maxThreadCount;
	private ITaskQueueFactory<T> taskQueueFactory;

	public AutoGrowthTaskQueueGroup(int entityCountPerThread, int coreThreadCount, int initThreadCount,
			int maxThreadCount, ITaskQueueFactory<T> taskQueueFactory) {
		if (taskQueueFactory == null) {
			throw new NullPointerException("taskQueueFactory");
		}
		this.taskQueueFactory = taskQueueFactory;
		this.maxThreadCount = maxThreadCount;
		this.initThreadCount = Math.min(initThreadCount, this.maxThreadCount);
		this.entityCountPerThread = entityCountPerThread;
		this.coreThreadCount = Math.min(coreThreadCount, this.maxThreadCount);
		if (initThreadCount > 10) {
			log.warn("initThreadCount is too large, less than 10 better!");
		}
		if (maxThreadCount > 50) {
			log.warn("maxThreadCount is too large,less than 50 better!");
		}
		log.info("init,entityCountPerThread:" + this.entityCountPerThread + ",initThreadCount:" + this.initThreadCount
				+ ",coreThreadCount:" + this.coreThreadCount + ",maxThreadCount:" + this.maxThreadCount);
		if (this.initThreadCount > 0) {
			for (int i = 0; i < this.initThreadCount; ++i) {
				newGrowthThread();
			}
		}
	}

	public synchronized void registerEntity(AutoGrowthEntity entity) throws TaskQueueException {
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

	public synchronized void unregisterEntity(AutoGrowthEntity entity) throws TaskQueueException {
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
		if (thread.getEntities().size() <= 0) {
			if (threads.size() > this.coreThreadCount) {
				AutoGrowthSegment<T> remove = threads.remove(threadIndex);
				if (remove != null) {
					remove.getThread().stopServer();
				}
			}
		}
	}

	private AutoGrowthSegment<T> newGrowthThread() {
		int id = threadId.incrementAndGet();
		AutoGrowthSegment<T> data = new AutoGrowthSegment<>();
		data.setThread(this.taskQueueFactory.newTaskQueue(id));
		data.setThreadIndex(id);
		data.getThread().startServer();
		this.threads.put(data.getThreadIndex(), data);
		return data;
	}
}
