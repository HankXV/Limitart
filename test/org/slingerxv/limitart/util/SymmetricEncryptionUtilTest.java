package org.slingerxv.limitart.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SymmetricEncryptionUtilTest {
	private SymmetricEncryptionUtil utilEncode;
	private SymmetricEncryptionUtil utilDecode;
	private SymmetricEncryptionUtil utilDecodeError;

	@Before
	public void setUp() throws Exception {
		utilEncode = SymmetricEncryptionUtil.getEncodeInstance("SymmetricEncryptionUtilTest", "okwjeofijop2i3jr");
		utilDecode = SymmetricEncryptionUtil.getDecodeInstance("SymmetricEncryptionUtilTest");
		utilDecodeError = SymmetricEncryptionUtil.getDecodeInstance("2324ds");
	}

	@Test
	public void codec() {
		String source = "12o3j2ophfibjdhfbjk293rhosdb;w'2;'34.'.'1231'2,l123;1k2l;jm1l;3tm1";
		String encode = null;
		try {
			encode = utilEncode.encode(source);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		try {
			String decode = utilDecode.decode(encode);
			Assert.assertEquals(source, decode);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		try {
			String decodeError = utilDecodeError.decode(encode);
			Assert.assertNotEquals(source, decodeError);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
