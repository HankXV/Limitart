package com.limitart.net.binary.taskqueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.limitart.net.binary.handler.IHandler;
import com.limitart.net.binary.message.Message;
import com.limitart.taskqueue.define.ITaskQueueHandler;

/**
 * 消息任务队列
 * 
 * @author Hank
 *
 */
public class MessageTaskQueue implements ITaskQueueHandler<Message> {
	private static Logger log = LogManager.getLogger();

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
}
