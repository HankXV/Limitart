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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 随机工具
 *
 * @author Hank
 */
public final class RandomUtil {
	public static Random DEFAULT;
	public static Random SECURITY;
	static {
		DEFAULT = new Random();
		try {
			SECURITY = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
		}
	}

	private RandomUtil() {
	}

	/**
	 * 随机整数（包含边界值）
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(int min, int max) {
		return randomInt(DEFAULT, min, max);
	}

	public static int randomIntSecure(int min, int max) {
		return randomInt(SECURITY, min, max);
	}

	/**
	 * 随机整数（包含边界值）
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static int randomInt(Random random, int start, int end) {
		if (start >= end) {
			return start;
		}
		return random.nextInt(end - start + 1) + start;
	}

	/**
	 * 返回long型(包含边界)
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static long randomLong(long start, long end) {
		return randomLong(DEFAULT, start, end);
	}

	public static long randomLongSecure(long start, long end) {
		return randomLong(SECURITY, start, end);
	}

	/**
	 * 返回long型(包含边界)
	 *
	 * @param random
	 * @param start
	 * @param end
	 * @return
	 */
	public static long randomLong(Random random, long start, long end) {
		if (start >= end) {
			return start;
		}
		return start + (long) (random.nextDouble() * (end - start));
	}

	/**
	 * 随机浮点数(包含边界)
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public static float randomFloat(float start, float end) {
		return randomFloat(DEFAULT, start, end);
	}

	public static float randomFloatSecure(float start, float end) {
		return randomFloat(SECURITY, start, end);
	}

	/**
	 * 随机浮点数(包含边界)
	 *
	 * @param random
	 * @param start
	 * @param end
	 * @return
	 */
	public static float randomFloat(Random random, float start, float end) {
		if (start >= end) {
			return start;
		}
		return start + random.nextFloat() * (end - start);
	}

	/**
	 * 返回1或-1
	 *
	 * @return
	 */
	public static int randomOne() {
		return randomOne(DEFAULT);
	}

	public static int randomOneSecure() {
		return randomOne(SECURITY);
	}

	/**
	 * 返回1或-1
	 *
	 * @return
	 */
	public static int randomOne(Random random) {
		return 1 | (random.nextInt() >> 31);
	}

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
