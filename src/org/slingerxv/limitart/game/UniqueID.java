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
package org.slingerxv.limitart.game;

import org.slingerxv.limitart.base.Conditions;
import org.slingerxv.limitart.base.ThreadSafe;
import org.slingerxv.limitart.util.TimeUtil;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 唯一编号生成器
 *
 * @author hank
 */
@ThreadSafe
public final class UniqueID {
    private static Seed DEFAULT_ID_ADDER = Seed.create();

    private UniqueID() {
    }

    /**
     * 生成唯一ID
     *
     * @param areaID
     * @param seed
     * @return
     */
    public static long nextID(int areaID, Seed seed) {
        Conditions.args(areaID <= 0x00FFFFFF && areaID >= 1, "1<=areaID<=0x00FFFFFF");
        //areaID24+时间29+自增11
        return (((long) (areaID & 0x00FFFFFF)) << 40) | seed.next();
    }

    public static long nextID(ServerKey serverKey, Seed seed) {
        return nextID(serverKey.serverKey(), seed);
    }

    public static long nextID(ServerKey serverKey) {
        return nextID(serverKey, DEFAULT_ID_ADDER);
    }

    public static long nextID(int areaID) {
        return nextID(areaID, DEFAULT_ID_ADDER);
    }


    public static long nextID() {
        return nextID(1);
    }

    /**
     * 生成全球唯一Id
     *
     * @return
     */
    public static String UUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 种子
     */
    public static class Seed {
        private transient volatile AtomicLong origin = new AtomicLong(((TimeUtil.now() / 1000) & 0x000000001FFFFFFFL) << 11);

        public static Seed create() {
            return new Seed();
        }

        private Seed() {
        }

        private long next() {
            return origin.getAndIncrement();
        }
    }
}
