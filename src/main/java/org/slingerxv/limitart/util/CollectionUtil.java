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

import java.util.Collection;

/**
 * 集合操作
 * 
 * @author hank
 *
 */
public final class CollectionUtil {
	private CollectionUtil() {
	}

	public static int contains(int[] value, int expect) {
		for (int i = 0; i < value.length; ++i) {
			if (value[i] == expect) {
				return i;
			}
		}
		return -1;
	}

	public static int contains(long[] value, long expect) {
		for (int i = 0; i < value.length; ++i) {
			if (value[i] == expect) {
				return i;
			}
		}
		return -1;
	}

	public static int contains(byte[] value, byte expect) {
		for (int i = 0; i < value.length; ++i) {
			if (value[i] == expect) {
				return i;
			}
		}
		return -1;
	}

	public static int contains(short[] value, short expect) {
		for (int i = 0; i < value.length; ++i) {
			if (value[i] == expect) {
				return i;
			}
		}
		return -1;
	}

	public static byte[] toByteArray(Collection<? extends Number> collection) {
		Object[] boxedArray = collection.toArray();
		int len = boxedArray.length;
		byte[] array = new byte[len];
		for (int i = 0; i < len; i++) {
			array[i] = ((Number) boxedArray[i]).byteValue();
		}
		return array;
	}

	public static int[] toIntArray(Collection<? extends Number> collection) {
		Object[] boxedArray = collection.toArray();
		int len = boxedArray.length;
		int[] array = new int[len];
		for (int i = 0; i < len; i++) {
			array[i] = ((Number) boxedArray[i]).intValue();
		}
		return array;
	}

	public static long[] toLongArray(Collection<? extends Number> collection) {
		Object[] boxedArray = collection.toArray();
		int len = boxedArray.length;
		long[] array = new long[len];
		for (int i = 0; i < len; i++) {
			array[i] = ((Number) boxedArray[i]).longValue();
		}
		return array;
	}

	public static short[] toShortArray(Collection<? extends Number> collection) {
		Object[] boxedArray = collection.toArray();
		int len = boxedArray.length;
		short[] array = new short[len];
		for (int i = 0; i < len; i++) {
			array[i] = ((Number) boxedArray[i]).shortValue();
		}
		return array;
	}

	public static double[] toDoubleArray(Collection<? extends Number> collection) {
		Object[] boxedArray = collection.toArray();
		int len = boxedArray.length;
		double[] array = new double[len];
		for (int i = 0; i < len; i++) {
			array[i] = ((Number) boxedArray[i]).doubleValue();
		}
		return array;
	}

	public static float[] toFloatArray(Collection<? extends Number> collection) {
		Object[] boxedArray = collection.toArray();
		int len = boxedArray.length;
		float[] array = new float[len];
		for (int i = 0; i < len; i++) {
			array[i] = ((Number) boxedArray[i]).floatValue();
		}
		return array;
	}
}
