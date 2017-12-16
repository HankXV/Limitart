package org.slingerxv.limitart.net.http.handler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.net.http.message.UrlMessageCase;

public class HttpHandlerTest {
	private UrlMessageCaseHandler handler;
	private UrlMessageCase message;

	@Before
	public void setUp() throws Exception {
		handler = new UrlMessageCaseHandler();
		message = new UrlMessageCase();
		message.init();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void doServer() {
		try {
			handler.doServer(message);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
