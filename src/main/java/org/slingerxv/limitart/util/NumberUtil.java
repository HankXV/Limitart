package org.slingerxv.limitart.util;

import java.util.Collection;

public final class NumberUtil {
	private NumberUtil() {
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
