package org.slingerxv.limitart.net.binary.message;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * MessageMeta各种数据类型测试
 * 
 * @author hank
 *
 */
public class MessageMetaTest {
	private MessageMetaEntity meta;
	private MessageMetaEntity metaEmpty;
	private ByteBuf buffer;

	@Before
	public void setUp() throws Exception {
		meta = new MessageMetaEntity();
		meta.init();
		metaEmpty = new MessageMetaEntity();
		buffer = Unpooled.directBuffer(256);
		meta.buffer(buffer);
		metaEmpty.buffer(buffer);
	}

	@After
	public void tearDown() throws Exception {
		meta.buffer(null);
		metaEmpty.buffer(null);
		buffer.release();
	}

	@Test
	public void codec() {
		try {
			meta.encode();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
		try {
			metaEmpty.decode();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.toString());
		}
		Assert.assertEquals(metaEmpty, meta);
	}
}
