package org.slingerxv.limitart.taskqueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.net.binary.handler.IHandler;
import org.slingerxv.limitart.net.binary.message.Message;

public class TaskQueueHandles {
	private static Logger log = LogManager.getLogger();
	public static final Proc1<Message> MESSAGE_HANDLER = new Proc1<Message>() {

		@Override
		public void run(Message t) {
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
	};
	public static final Proc1<Runnable> RUNNABLE_HANDLER = new Proc1<Runnable>() {

		@Override
		public void run(Runnable t) {
			t.run();
		}
	};
}
