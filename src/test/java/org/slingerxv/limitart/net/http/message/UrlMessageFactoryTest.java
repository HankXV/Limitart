package org.slingerxv.limitart.net.http.message;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slingerxv.limitart.net.binary.message.exception.MessageIDDuplicatedException;
import org.slingerxv.limitart.net.http.handler.UrlMessageCaseHandler;

public class UrlMessageFactoryTest {
	private UrlMessageFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new UrlMessageFactory();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void registerMsg() {
		try {
			factory.registerMsg(UrlMessageCaseHandler.class);
		} catch (InstantiationException | IllegalAccessException | MessageIDDuplicatedException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void createByPackage() {
		try {
			UrlMessageFactory.createByPackage("org.slingerxv.limitart.net.http");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException
				| MessageIDDuplicatedException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
