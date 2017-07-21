package org.slingerxv.limitart.taskqueue;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Func1;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.funcs.Proc2;
import org.slingerxv.limitart.taskqueue.define.ITaskQueue;
import org.slingerxv.limitart.taskqueue.exception.TaskQueueException;
import org.slingerxv.limitart.thread.NamedThreadFactory;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 消息队列线程
 * 
 * @author Hank
 *
 */
public class DisruptorTaskQueue<T> implements ITaskQueue<T> {
	private static Logger log = LogManager.getLogger();
	private Disruptor<DisruptorTaskQueueEvent> disruptor;
	private NamedThreadFactory threadFactory;
	private TaskQueueEventProducerWithTraslator traslator;
	private Func1<T, Boolean> intercept;
	private Proc1<T> handle;
	private Proc2<T, Throwable> exception;

	public DisruptorTaskQueue(String threadName) {
		this(threadName, 2048);
	}

	/**
	 * 构造函数
	 * 
	 * @param threadName
	 * @param bufferSize
	 *            指定RingBuffer的大小
	 * @param handler
	 */
	@SuppressWarnings("unchecked")
	public DisruptorTaskQueue(String threadName, int bufferSize) {
		this.threadFactory = new NamedThreadFactory() {

			@Override
			public String getThreadName() {
				return threadName;
			}
		};
		disruptor = new Disruptor<>((EventFactory<DisruptorTaskQueueEvent>) () -> new DisruptorTaskQueueEvent(),
				bufferSize, this.threadFactory, ProducerType.MULTI,
				new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.SECONDS));
		disruptor.handleEventsWith(new TaskQueueEventHandler());
		disruptor.handleExceptionsFor(new EventHandler<DisruptorTaskQueueEvent>() {

			@Override
			public void onEvent(DisruptorTaskQueueEvent arg0, long arg1, boolean arg3) throws Exception {
				Exception e = new Exception("exception catched:" + arg0.getMsg().getClass());
				log.error(e, e);
				if (exception != null) {
					exception.run(arg0.msg, e);
				}
			}
		});
		traslator = new TaskQueueEventProducerWithTraslator(disruptor.getRingBuffer());
	}

	public ITaskQueue<T> intercept(Func1<T, Boolean> intercept) {
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
	public void startServer() {
		disruptor.start();
		log.info("thread " + threadFactory.getThreadName() + " start!");
	}

	@Override
	public void stopServer() {
		if (disruptor != null) {
			disruptor.shutdown();
			log.info("thread " + threadFactory.getThreadName() + " stop!");
			disruptor = null;
			traslator = null;
			threadFactory = null;
		}
	}

	@Override
	public void addCommand(T command) throws TaskQueueException {
		Objects.requireNonNull(command, "command");
		if (this.disruptor == null) {
			throw new TaskQueueException(getThreadName() + " has not start yet!");
		}
		this.traslator.onData(command);
	}

	@Override
	public String getThreadName() {
		return threadFactory.getThreadName();
	}

	/**
	 * 消息转换器
	 * 
	 * @author Hank
	 *
	 */
	private class TaskQueueEventProducerWithTraslator {
		private EventTranslatorOneArg<DisruptorTaskQueueEvent, T> translatorOneArg = (arg0, arg1, arg2) -> arg0
				.setMsg(arg2);
		private final RingBuffer<DisruptorTaskQueueEvent> ringBuffer;

		public TaskQueueEventProducerWithTraslator(RingBuffer<DisruptorTaskQueueEvent> ringBuffer) {
			this.ringBuffer = ringBuffer;
		}

		public void onData(T t) {
			ringBuffer.publishEvent(translatorOneArg, t);
		}
	}

	private class TaskQueueEventHandler implements EventHandler<DisruptorTaskQueueEvent> {

		@Override
		public void onEvent(DisruptorTaskQueueEvent event, long paramLong, boolean paramBoolean) throws Exception {
			if (DisruptorTaskQueue.this.intercept != null && DisruptorTaskQueue.this.intercept.run(event.getMsg())) {
				return;
			}
			try {
				if (DisruptorTaskQueue.this.handle != null) {
					DisruptorTaskQueue.this.handle.run(event.getMsg());
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	private class DisruptorTaskQueueEvent {
		private T msg;

		public T getMsg() {
			return msg;
		}

		public void setMsg(T msg) {
			this.msg = msg;
		}
	}
}
