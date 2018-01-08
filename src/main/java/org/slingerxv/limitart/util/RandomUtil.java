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

import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机工具
 *
 * @author Hank
 */
public final class RandomUtil {
    private RandomUtil() {
    }

    /**
     * 随机整数（包含边界值）
     *
     * @param start
     * @param end
     * @return
     */
    public static int randomInt(int start, int end) {
        if (start >= end) {
            return start;
        }
        return ThreadLocalRandom.current().nextInt(end - start + 1) + start;
    }

    /**
     * 返回long型(包含边界)
     *
     * @param start
     * @param end
     * @return
     */
    public static long randomLong(long start, long end) {
        if (start >= end) {
            return start;
        }
        return start + (long) (ThreadLocalRandom.current().nextDouble() * (end - start));
    }

    /**
     * 随机浮点数(包含边界)
     *
     * @param start
     * @param end
     * @return
     */
    public static float randomFloat(float start, float end) {
        if (start >= end) {
            return start;
        }
        return start + ThreadLocalRandom.current().nextFloat() * (end - start);
    }

    /**
     * 返回1或-1
     *
     * @return
     */
    public static int randomOne() {
        return 1 | (ThreadLocalRandom.current().nextInt() >> 31);
    }

    /**
     * 通过数组值的权重分布来随机数组索引
     *
     * @param weight
     * @return 数组索引
     */
    public static int randomWeight(int[] weight) {
        int sumProb = 0;
        for (int prob : weight) {
            sumProb += prob;
        }
        int randomInt = randomInt(0, sumProb);
        int step = 0;
        for (int i = 0; i < weight.length; ++i) {
            step += weight[i];
            if (randomInt <= step) {
                return i;
            }
        }
        return 0;
    }
}
