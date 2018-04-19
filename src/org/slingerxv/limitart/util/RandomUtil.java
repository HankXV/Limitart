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

import java.util.List;
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
    public static int nextInt(int start, int end) {
        if (start >= end) {
            return start;
        }
        return ThreadLocalRandom.current().nextInt(end - start + 1) + start;
    }

    /**
     * 是否命中1-10
     *
     * @param value
     * @return
     */
    public static boolean h10(int value) {
        if (value < 1) {
            return false;
        } else if (value > 10) {
            return true;
        }
        return nextInt(1, 10) <= value;
    }

    /**
     * 是否命中1-100
     *
     * @param value
     * @return
     */
    public static boolean h100(int value) {
        if (value < 1) {
            return false;
        } else if (value > 100) {
            return true;
        }
        return nextInt(1, 100) <= value;
    }

    /**
     * 是否命中1-1000
     *
     * @param value
     * @return
     */
    public static boolean h1000(int value) {
        if (value < 1) {
            return false;
        } else if (value > 1000) {
            return true;
        }
        return nextInt(1, 1000) <= value;
    }

    /**
     * 是否命中1-10000
     *
     * @param value
     * @return
     */
    public static boolean h10000(int value) {
        if (value < 1) {
            return false;
        } else if (value > 10000) {
            return true;
        }
        return nextInt(1, 10000) <= value;
    }

    /**
     * 返回long型(包含边界)
     *
     * @param start
     * @param end
     * @return
     */
    public static long nextLong(long start, long end) {
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
    public static float nextFloat(float start, float end) {
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
    public static int nextOne() {
        return 1 | (ThreadLocalRandom.current().nextInt() >> 31);
    }

    /**
     * 随机一个布尔值
     *
     * @return
     */
    public static boolean nextBool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 随机一个元素
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T element(List<T> list) {
        if (list == null) {
            return null;
        }
        return list.get(nextInt(0, list.size() - 1));
    }

    /**
     * 通过数组值的权重分布来随机数组索引
     *
     * @param weight
     * @return 数组索引
     */
    public static int weight(int[] weight) {
        int sumProb = 0;
        for (int prob : weight) {
            sumProb += prob;
        }
        int randomInt = nextInt(1, sumProb);
        int step = 0;
        for (int i = 0; i < weight.length; ++i) {
            step += weight[i];
            if (randomInt <= step) {
                return i;
            }
        }
        throw new IllegalArgumentException("at least one weight > 0");
    }

    public static void main(String[] args) {
        int[] weight = new int[]{1, 0};
        int count = 0;
        for (int i = 0; i < 100000000; ++i) {
            int i1 = weight(weight);
            if (i1 == (weight.length - 1)) {
                ++count;
            }
        }
        System.out.println(count);
    }
}
