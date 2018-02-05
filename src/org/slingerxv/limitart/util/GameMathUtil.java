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

import org.slingerxv.limitart.base.Func1;
import org.slingerxv.limitart.base.Proc2;

import java.util.ArrayList;
import java.util.List;


/**
 * 数学相关
 *
 * @author hank
 */
public final class GameMathUtil {
    private GameMathUtil() {
    }

    /**
     * 安全相加,如果越界返回int最大值
     *
     * @param x
     * @param y
     * @return
     */
    public static int safeAdd(int x, int y) {
        int r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            return Integer.MAX_VALUE;
        }
        return r;
    }

    /**
     * 安全相减,如果越界返回int最小值
     *
     * @param x
     * @param y
     * @return
     */
    public static int safeSub(int x, int y) {
        int r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            return Integer.MIN_VALUE;
        }
        return r;
    }

    /**
     * 安全相加,如果越界返回long最大值
     *
     * @param x
     * @param y
     * @return
     */
    public static long safeAdd(long x, long y) {
        long r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            return Long.MAX_VALUE;
        }
        return r;
    }

    /**
     * 安全相减,如果越界返回long最小值
     *
     * @param x
     * @param y
     * @return
     */
    public static long safeSub(long x, long y) {
        long r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            return Long.MIN_VALUE;
        }
        return r;
    }


    /**
     * 安全相乘,越界返回int最大值
     *
     * @param x
     * @param y
     * @return
     */
    public static int safelyMulti(int x, int y) {
        long r = (long) x * (long) y;
        if ((int) r != r) {
            return Integer.MAX_VALUE;
        }
        return (int) r;
    }


    /**
     * 安全相乘,越界返回long最大值
     *
     * @param x
     * @param y
     * @return
     */
    public static long safelyMulti(long x, long y) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {
            if (((y != 0) && (r / y != x)) ||
                    (x == Long.MIN_VALUE && y == -1)) {
                return Long.MAX_VALUE;
            }
        }
        return r;
    }

    /**
     * 保证给出数字在条件之间
     *
     * @param value
     * @param low
     * @param high
     * @return
     */
    public static int fixedBetween(int value, int low, int high) {
        return value > high ? high : (value < low ? low : value);
    }

    /**
     * 保证给出数字在条件之间
     *
     * @param value
     * @param low
     * @param high
     * @return
     */
    public static long fixedBetween(long value, long low, long high) {
        return value > high ? high : (value < low ? low : high);
    }

    /**
     * 升级操作
     *
     * @param startLevel 开始等级
     * @param currentExp 当前经验
     * @param inputExp   输入的经验
     * @param needExp    所传输的等级需要多少经验升级
     * @param onLevelUp  当升级的时候(当前等级，剩余多少经验未消耗)
     * @return
     */
    public static int levelUp(int startLevel, long currentExp, long inputExp, Func1<Integer, Long> needExp,
                              Proc2<Integer, Long> onLevelUp) {
        int start = startLevel;
        long expPool = inputExp;
        long curNeed;
        long curExp = currentExp;
        while (needExp.run(start + 1) > 0 && (curNeed = (needExp.run(start + 1) - curExp)) <= expPool) {
            expPool -= curNeed;
            start += 1;
            curExp = 0;
            onLevelUp.run(start, expPool);
        }
        return start;
    }

    /**
     * 数字加成
     *
     * @param source
     * @param addition    分比分子
     * @param percentUnit 分比单位
     * @return
     */
    public static int numberAddition(int source, int addition, int percentUnit) {
        return (int) (source * (1 + addition / (double) percentUnit));
    }

    /**
     * 计算某个数字百分比加成
     *
     * @param source
     * @param addition
     * @return
     */
    public static int numberAddition(int source, int addition) {
        return numberAddition(source, addition, 100);
    }

    /**
     * 数字加成
     *
     * @param source
     * @param addition    分比分子
     * @param percentUnit 分比单位
     * @return
     */
    public static long numberAddition(long source, int addition, int percentUnit) {
        return (long) (source * (1 + addition / (double) percentUnit));
    }

    /**
     * 计算某个数字百分比加成
     *
     * @param source
     * @param addition
     * @return
     */
    public static long numberAddition(long source, int addition) {
        return numberAddition(source, addition, 100);
    }

    /**
     * 从N个元素中选出指定数量的所有组合
     *
     * @param source
     * @param getNum
     * @return
     */
    public static <E> List<List<E>> CNM(List<E> source, int getNum) {
        List<List<E>> result = new ArrayList<>();
        int pickNum = getNum;
        if (pickNum < 0) {
            pickNum = 1;
        }
        if (pickNum >= source.size()) {
            result.add(source);
        } else {
            // 第几个位置
            CNM0(result, pickNum, new ArrayList<>(), source, pickNum);
        }
        return result;
    }

    private static <E> void CNM0(List<List<E>> result, int originN, List<E> tempResult, List<E> source, int pickNum) {
        if (pickNum == 1) {
            for (E aSource : source) {
                List<E> temp = new ArrayList<>(tempResult);
                temp.add(aSource);
                result.add(temp);
            }
            return;
        }
        for (int j = 0; j < source.size() - (pickNum - 1); j++) {
            List<E> newIa = source.subList(j + 1, source.size());
            if (originN != pickNum) {
                tempResult.add(source.get(j));
                CNM0(result, originN, tempResult, newIa, pickNum - 1);
                for (int a = tempResult.size() - 1; a >= originN - pickNum; --a) {
                    tempResult.remove(a);
                }

            } else {
                List<E> ab = new ArrayList<>();
                // 第几个位置确定的数
                ab.add(source.get(j));
                CNM0(result, originN, ab, newIa, pickNum - 1);
            }
        }
    }
}
