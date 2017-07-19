package org.slingerxv.limitart.net.http.message;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UrlMessageTest {
	private UrlMessageCase origin;
	private UrlMessageCase empty;

	@Before
	public void setUp() throws Exception {
		origin = new UrlMessageCase();
		origin.init();
		empty = new UrlMessageCase();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void codec() {
		try {
			origin.encode();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		empty.putAll(origin);
		try {
			empty.decode();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertEquals(origin, empty);
	}

}
