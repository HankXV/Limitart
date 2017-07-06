package com.limitart.taskqueue;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.taskqueue.define.ITaskQueue;
import com.limitart.taskqueue.define.ITaskQueueHandler;
import com.limitart.taskqueue.exception.TaskQueueException;
import com.limitart.thread.NamedThreadFactory;
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
	private Thread handlerThread;
	private TaskQueueEventProducerWithTraslator traslator;
	private ITaskQueueHandler<T> handler;

	public DisruptorTaskQueue(String threadName, ITaskQueueHandler<T> handler) {
		this(threadName, 2048, handler);
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
	public DisruptorTaskQueue(String threadName, int bufferSize, ITaskQueueHandler<T> handler) {
		if (handler == null) {
			throw new NullPointerException("handler");
		}
		this.handler = handler;
		disruptor = new Disruptor<>(new EventFactory<DisruptorTaskQueueEvent>() {

			@Override
			public DisruptorTaskQueueEvent newInstance() {
				return new DisruptorTaskQueueEvent();
			}

		}, bufferSize, new NamedThreadFactory() {

			@Override
			public String getThreadName() {
				return threadName;
			}
		}, ProducerType.MULTI, new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.SECONDS));
		disruptor.handleEventsWith(new TaskQueueEventHandler());
		disruptor.handleExceptionsFor(new EventHandler<DisruptorTaskQueueEvent>() {

			@Override
			public void onEvent(DisruptorTaskQueueEvent arg0, long arg1, boolean arg3) throws Exception {
				log.error(new Exception("exception cathed:" + arg0.getMsg().getClass()));
			}
		});
		traslator = new TaskQueueEventProducerWithTraslator(disruptor.getRingBuffer());
	}

	@Override
	public void startServer() {
		disruptor.start();
		log.info("thread " + handlerThread.getName() + " start!");
	}

	@Override
	public void stopServer() {
		if (disruptor != null) {
			disruptor.shutdown();
			log.info("thread " + handlerThread.getName() + " stop!");
			disruptor = null;
			traslator = null;
			handlerThread = null;
		}
	}

	@Override
	public void addCommand(T t) throws TaskQueueException {
		if (t == null) {
			throw new NullPointerException("t");
		}
		if (this.disruptor == null) {
			throw new TaskQueueException(getThreadName() + " has not start yet!");
		}
		this.traslator.onData(t);
	}

	@Override
	public String getThreadName() {
		if (handlerThread == null) {
			return "";
		}
		return handlerThread.getName();
	}

	/**
	 * 消息转换器
	 * 
	 * @author Hank
	 *
	 */
	private class TaskQueueEventProducerWithTraslator {
		private EventTranslatorOneArg<DisruptorTaskQueueEvent, T> translatorOneArg = new EventTranslatorOneArg<DisruptorTaskQueueEvent, T>() {

			@Override
			public void translateTo(DisruptorTaskQueueEvent arg0, long arg1, T arg2) {
				arg0.setMsg(arg2);
			}
		};
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
			if (DisruptorTaskQueue.this.handler.intercept(event.getMsg())) {
				return;
			}
			DisruptorTaskQueue.this.handler.handle(event.getMsg());
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
