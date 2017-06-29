package com.limitart.math.util;

import java.util.Random;

/**
 * 随机工具
 * 
 * @author Hank
 *
 */
public final class RandomUtil {
	public static Random DEFAULT = new RandomXS128();

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

	/**
	 * 返回1或-1
	 * 
	 * @return
	 */
	public static int randomOne(Random random) {
		return 1 | (random.nextInt() >> 31);
	}
}
