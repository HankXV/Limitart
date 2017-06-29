package com.limitart.util;

import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

/**
 * 唯一编号生成器
 * 
 * @author hank
 *
 */
public class UniqueIdUtil {
	/**
	 * 生成唯一Id
	 * 
	 * @param areaId
	 *            区域Id(最多支持16位区域数量)
	 * @param adder
	 *            自增长器(支持每秒16位数量的并发)
	 * @return
	 */
	public static long createUUID(long areaId, LongAdder adder) {
		// 服务器编号16位+ 时间32+自增16
		adder.increment();
		long serverIdBit = (areaId << 48) & (0xFFFF000000000000L);
		long timeBit = ((System.currentTimeMillis() / 1000) << 32) & (0x0000FFFFFFFF0000L);
		return serverIdBit | timeBit | adder.longValue();
	}

	/**
	 * 生成全球唯一Id
	 * 
	 * @return
	 */
	public static String createUUID() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
}
