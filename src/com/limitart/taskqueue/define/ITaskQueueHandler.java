package com.limitart.taskqueue.define;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;

public interface ITaskQueueHandler<T> {
	Logger log = LogManager.getLogger();
	ITaskQueueHandler<Message> MESSAGE_HANDLER = new ITaskQueueHandler<Message>() {

		@Override
		public void handle(Message t) {
			IHandler handler = t.getHandler();
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

	boolean intercept(T t);

	void handle(T t);
}
