package org.slingerxv.limitart.net.binary.taskqueue;

import java.util.logging.LogManager;

import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.taskqueue.define.ITaskQueueHandler;

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
