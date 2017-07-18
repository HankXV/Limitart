package org.slingerxv.limitart.taskqueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slingerxv.limitart.funcs.Proc1;
import org.slingerxv.limitart.net.binary.message.MessageHandlerWrapper;

public class TaskQueueHandles {
	private static Logger log = LogManager.getLogger();
	public static final Proc1<MessageHandlerWrapper> MESSAGE_HANDLER = (wrapper) -> {
		long now = System.currentTimeMillis();
		try {
			wrapper.handle();
		} catch (Exception e) {
			log.error(e, e);
		}
		now = System.currentTimeMillis() - now;
		if (now > 100) {
			log.warn("handler:" + wrapper.getHandler().getClass().getName() + " handle time > 100");
		}
	};
	public static final Proc1<Runnable> RUNNABLE_HANDLER = new Proc1<Runnable>() {

		@Override
		public void run(Runnable t) {
			t.run();
		}
	};
}
