package org.slingerxv.limitart.taskqueue.define;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;

public interface ITaskQueueHandler<T> {
	static Logger log = LogManager.getLogger();
	ITaskQueueHandler<Message> MESSAGE_HANDLER = new ITaskQueueHandler<Message>() {
		@Override
		public void handle(Message t) {
			IHandler<Message> handler = t.getHandler();
			if (handler == null) {
				log.error(t.getClass().getName() + " has no handler!");
				return;
			}
			long now = System.currentTimeMillis();
			try {
				handler.handle(t);
			} catch (Exception e) {
				log.error(e, e);
			}
			now = System.currentTimeMillis() - now;
			if (now > 100) {
				log.warn("handler:" + handler.getClass().getName() + " handle time > 100");
			}
		}

		@Override
		public boolean intercept(Message t) {
			return false;
		}
	};
	ITaskQueueHandler<Runnable> RUNNABLE_HANDLER = new ITaskQueueHandler<Runnable>() {

		@Override
		public boolean intercept(Runnable t) {
			return false;
		}

		@Override
		public void handle(Runnable t) {
			t.run();
		}
	};

	boolean intercept(T t);

	void handle(T t);
}
