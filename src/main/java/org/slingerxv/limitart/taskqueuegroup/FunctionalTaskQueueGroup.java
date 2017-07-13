package org.slingerxv.limitart.taskqueuegroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slingerxv.limitart.thread.NamedThreadFactory;

/**
 * 功能性队列组
 * 
 * @author Hank
 *
 */
public class FunctionalTaskQueueGroup {
	private AtomicInteger ioCount = new AtomicInteger(0);
	private ExecutorService ioExecutor;
	private ExecutorService computationExecutor;
	private ExecutorService singleExecutor;
	private ScheduledThreadPoolExecutor scheduleExecutor;

	public FunctionalTaskQueueGroup(String threadNamePrefix) {
		this.ioExecutor = Executors.newCachedThreadPool(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, threadNamePrefix + "-IO" + "-" + ioCount.getAndIncrement());
			}
		});
		this.computationExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
				new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, threadNamePrefix + "-Computation");
					}
				});
		this.singleExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory() {

			@Override
			public String getThreadName() {
				return threadNamePrefix + "-Single";
			}
		});
		this.scheduleExecutor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory() {

			@Override
			public String getThreadName() {
				return threadNamePrefix + "-Schedule";
			}
		});
	}

	public void addIOCommand(Runnable runnable) {
		ioExecutor.execute(runnable);
	}

	public void addComputationCommand(Runnable runnable) {
		computationExecutor.execute(runnable);
	}

	public void addSingleTask(Runnable runnable) {
		singleExecutor.execute(runnable);
	}

	public void addNewThreadTask(String taskName, Runnable runnable) {
		new Thread(runnable, taskName).start();
	}

	public void addScheduleTask(Runnable runnable, long delayMills) {
		scheduleExecutor.schedule(runnable, delayMills, TimeUnit.MILLISECONDS);
	}
}
