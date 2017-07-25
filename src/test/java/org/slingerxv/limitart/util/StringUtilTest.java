package org.slingerxv.limitart.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest {

	@Test
	public void isEmptyOrNull() {
		assertTrue(StringUtil.isEmptyOrNull(null));
		assertTrue(StringUtil.isEmptyOrNull(""));
	}

	@Test
	public void isIp() {
		assertTrue(StringUtil.isIp4("156.246.255.255"));
		assertFalse(StringUtil.isIp4("156.246.354.256"));
	}

	@Test
	public void isInnerIp() {
		assertTrue(StringUtil.isInnerIp4("192.168.0.1"));
		assertTrue(StringUtil.isInnerIp4("172.16.0.0"));
		assertTrue(StringUtil.isInnerIp4("10.0.0.0"));
		assertFalse(StringUtil.isInnerIp4("156.246.255.255"));
	}
}