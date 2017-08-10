/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
