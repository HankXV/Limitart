/*
 * Copyright (c) 2016-present The Limitart Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slingerxv.limitart.util;

import org.slingerxv.limitart.base.ThreadSafe;

import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

/**
 * 唯一编号生成器
 *
 * @author hank
 */
@ThreadSafe
public final class UniqueIDUtil {
    private static LongAdder DEFAULT_ID_ADDER = new LongAdder();
    private static long DEFAULT_AREA_ID = 1;

    private UniqueIDUtil() {
    }

    /**
     * 创建默认自增器和默认区域ID的唯一ID
     *
     * @return
     */
    public static long nextID() {
        return nextID(DEFAULT_AREA_ID);
    }

    /**
     * 创建默认自增器产生的唯一ID
     *
     * @param areaID
     * @return
     */
    public static long nextID(long areaID) {
        return nextID(areaID, DEFAULT_ID_ADDER);
    }

    /**
     * 生成唯一Id
     *
     * @param areaID 区域Id(最多支持16位区域数量)
     * @param adder  自增长器(支持每秒16位数量的并发)
     * @return
     */
    public static long nextID(long areaID, LongAdder adder) {
        // 服务器编号16位+ 时间32+自增16
        adder.increment();
        long serverIdBit = (areaID << 48) & (0xFFFF000000000000L);
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
