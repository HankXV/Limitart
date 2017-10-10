package org.slingerxv.limitart.collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConstraintMapTest {
	private ConstraintMap<String> map;

	@Before
	public void setUp() throws Exception {
		map = ConstraintHashMap.empty();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		boolean boolVal = true;
		map.putBoolean("boolean", boolVal);
		Assert.assertEquals(boolVal, map.getBoolean("boolean"));
		Assert.assertEquals(false, map.getBoolean("boolean1"));
		byte byteVal = (byte) 251;
		map.putByte("byte", byteVal);
		Assert.assertEquals(byteVal, map.getByte("byte"));
		Assert.assertEquals(0, map.getByte("byte1"));
		short shortVal = 1241;
		map.putShort("short", shortVal);
		Assert.assertEquals(shortVal, map.getShort("short"));
		Assert.assertEquals(0, map.getShort("short1"));
		int intVal = 124231;
		map.putInt("int", intVal);
		Assert.assertEquals(intVal, map.getInt("int"));
		Assert.assertEquals(0, map.getInt("int1"));
		long longVal = 11251224231l;
		map.putLong("long", longVal);
		Assert.assertEquals(longVal, map.getLong("long"));
		Assert.assertEquals(0, map.getLong("long1"));
	}

}
