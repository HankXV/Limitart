package org.slingerxv.limitart.net.binary.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HandlerTest {
	private MessageCaseHandler handler;
	private MessageCase message;

	@Before
	public void setUp() throws Exception {
		handler = new MessageCaseHandler();
		message = new MessageCase();
		message.info = "test";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void handle() {
		handler.handle(message);
	}
}
