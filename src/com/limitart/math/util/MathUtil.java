package com.limitart.math.util;

public class MathUtil {
	public final static float PI_FLOAT = 3.1415927f;
	public static final float PI_FLOAT_2X = PI_FLOAT * 2;
	public static final float PI_FLOAT_HALF = PI_FLOAT / 2;
	public final static float R2D = 180f / PI_FLOAT;
	public final static float D2R = PI_FLOAT / 180;
	private final static float FLOAT_ROUNDING_ERROR = 0.000001f;
	public final static float E = 2.7182818f;
	private final static int SIN_BITS = 14; // 16KB. Adjust for accuracy.
	private final static int SIN_MASK = ~(-1 << SIN_BITS);
	private final static int SIN_COUNT = SIN_MASK + 1;
	private static final float radFull = PI_FLOAT * 2;
	private static final float degFull = 360;
	private static final float radToIndex = SIN_COUNT / radFull;
	private static final float degToIndex = SIN_COUNT / degFull;

	static private class Sin {
		static final float[] table = new float[SIN_COUNT];

		static {
			for (int i = 0; i < SIN_COUNT; i++)
				table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
			for (int i = 0; i < 360; i += 90)
				table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * D2R);
		}
	}

	static public float sin(float radians) {
		return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
	}

	static public float cos(float radians) {
		return Sin.table[(int) ((radians + PI_FLOAT_HALF) * radToIndex) & SIN_MASK];
	}

	static public float sinDeg(float degrees) {
		return Sin.table[(int) (degrees * degToIndex) & SIN_MASK];
	}

	static public float cosDeg(float degrees) {
		return Sin.table[(int) ((degrees + 90) * degToIndex) & SIN_MASK];
	}

	static public float atan2(float y, float x) {
		if (x == 0f) {
			if (y > 0f)
				return PI_FLOAT_HALF;
			if (y == 0f)
				return 0f;
			return -PI_FLOAT_HALF;
		}
		final float atan, z = y / x;
		if (Math.abs(z) < 1f) {
			atan = z / (1f + 0.28f * z * z);
			if (x < 0f)
				return atan + (y < 0f ? -PI_FLOAT : PI_FLOAT);
			return atan;
		}
		atan = PI_FLOAT_HALF - z / (z * z + 0.28f);
		return y < 0f ? atan - PI_FLOAT : atan;
	}

	static public short clamp(short value, short min, short max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public int clamp(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public long clamp(long value, long min, long max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public float clamp(float value, float min, float max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public double clamp(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	static public float lerp(float fromValue, float toValue, float progress) {
		return fromValue + (toValue - fromValue) * progress;
	}

	public static float lerpAngle(float fromRadians, float toRadians, float progress) {
		float delta = ((toRadians - fromRadians + PI_FLOAT_2X + PI_FLOAT) % PI_FLOAT_2X) - PI_FLOAT;
		return (fromRadians + delta * progress + PI_FLOAT_2X) % PI_FLOAT_2X;
	}

	public static float lerpAngleDeg(float fromDegrees, float toDegrees, float progress) {
		float delta = ((toDegrees - fromDegrees + 360 + 180) % 360) - 180;
		return (fromDegrees + delta * progress + 360) % 360;
	}

	static private final int BIG_ENOUGH_INT = 16 * 1024;
	static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
	static private final double CEIL = 0.9999999;
	static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

	static public int floor(float value) {
		return (int) (value + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
	}

	static public int floorPositive(float value) {
		return (int) value;
	}

	static public int ceil(float value) {
		return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - value);
	}

	static public int ceilPositive(float value) {
		return (int) (value + CEIL);
	}

	static public int round(float value) {
		return (int) (value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
	}

	static public int roundPositive(float value) {
		return (int) (value + 0.5f);
	}

	static public boolean isZero(float value) {
		return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
	}

	static public boolean isZero(float value, float tolerance) {
		return Math.abs(value) <= tolerance;
	}

	static public boolean isEqual(float a, float b) {
		return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
	}

	static public boolean isEqual(float a, float b, float tolerance) {
		return Math.abs(a - b) <= tolerance;
	}

	static public float log(float a, float value) {
		return (float) (Math.log(value) / Math.log(a));
	}

	static public float log2(float value) {
		return log(2, value);
	}
}
