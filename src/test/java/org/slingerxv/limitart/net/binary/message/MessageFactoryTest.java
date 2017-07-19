package org.slingerxv.limitart.net.binary.message;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.net.binary.handler.MessageCaseHandler;

public class MessageFactoryTest {
	private MessageFactory messageFactory;

	@Before
	public void setUp() throws Exception {
		messageFactory = new MessageFactory();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void registerMsg() {
		try {
			messageFactory.registerMsg(new MessageCaseHandler());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void createByPackage() {
		try {
			MessageFactory.createByPackage("org.slingerxv.limitart.net.binary");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
