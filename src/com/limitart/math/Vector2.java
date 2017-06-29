package com.limitart.math;

import com.limitart.math.util.MathUtil;

public final class Vector2 {
	public final static Vector2 X = new Vector2(1, 0);
	public final static Vector2 Y = new Vector2(0, 1);
	public final static Vector2 Zero = new Vector2(0, 0);
	private float x;
	private float y;

	public Vector2() {
	}

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float len2() {
		return x * x + y * y;
	}

	public Vector2 normalize() {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public float dot(Vector2 v) {
		return x * v.x + y * v.y;
	}

	public float dot(float ox, float oy) {
		return x * ox + y * oy;
	}

	public Vector2 scale(float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	public Vector2 scale(float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	public Vector2 scale(Vector2 v) {
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	public Vector2 mulAdd(Vector2 vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		return this;
	}

	public Vector2 mulAdd(Vector2 vec, Vector2 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		return this;
	}

	public float distance(Vector2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public float distance(float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public float distance2(Vector2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	public float distance2(float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}

	public Vector2 limit(float limit) {
		return limit2(limit * limit);
	}

	public Vector2 limit2(float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			return scale((float) Math.sqrt(limit2 / len2));
		}
		return this;
	}

	public Vector2 clamp(float min, float max) {
		final float len2 = len2();
		if (len2 == 0f)
			return this;
		float max2 = max * max;
		if (len2 > max2)
			return scale((float) Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2)
			return scale((float) Math.sqrt(min2 / len2));
		return this;
	}

	public Vector2 setLength(float len) {
		return setLength2(len * len);
	}

	public Vector2 setLength2(float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scale((float) Math.sqrt(len2 / oldLen2));
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public float crs(Vector2 v) {
		return this.x * v.y - this.y * v.x;
	}

	public float crs(float x, float y) {
		return this.x * y - this.y * x;
	}

	public float angle() {
		float angle = (float) Math.atan2(y, x) * MathUtil.R2D;
		if (angle < 0)
			angle += 360;
		return angle;
	}

	public float angle(Vector2 reference) {
		return (float) Math.atan2(crs(reference), dot(reference)) * MathUtil.R2D;
	}

	public float angleRad() {
		return (float) Math.atan2(y, x);
	}

	public float angleRad(Vector2 reference) {
		return (float) Math.atan2(crs(reference), dot(reference));
	}

	public Vector2 setAngle(float degrees) {
		return setAngleRad(degrees * MathUtil.D2R);
	}

	public Vector2 setAngleRad(float radians) {
		this.x = len();
		this.y = 0f;
		this.rotateRad(radians);

		return this;
	}

	public Vector2 rotate(float degrees) {
		return rotateRad(degrees * MathUtil.D2R);
	}

	public Vector2 rotateRad(float radians) {
		float cos = (float) Math.cos(radians);
		float sin = (float) Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	public Vector2 rotate90(int dir) {
		float x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
		}
		return this;
	}

	public Vector2 lerp(Vector2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		this.x = (x * invAlpha) + (target.x * alpha);
		this.y = (y * invAlpha) + (target.y * alpha);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2 other = (Vector2) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	public boolean isUnit() {
		return isUnit(0.000000001f);
	}

	public boolean isUnit(final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	public boolean isZero() {
		return x == 0 && y == 0;
	}

	public boolean isZero(final float margin) {
		return len2() < margin;
	}

	public boolean isOnLine(Vector2 other) {
		return MathUtil.isZero(x * other.y - y * other.x);
	}

	public boolean isOnLine(Vector2 other, float epsilon) {
		return MathUtil.isZero(x * other.y - y * other.x, epsilon);
	}

	public boolean isCollinear(Vector2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) > 0f;
	}

	public boolean isCollinear(Vector2 other) {
		return isOnLine(other) && dot(other) > 0f;
	}

	public boolean isCollinearOpposite(Vector2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) < 0f;
	}

	public boolean isCollinearOpposite(Vector2 other) {
		return isOnLine(other) && dot(other) < 0f;
	}

	public boolean isPerpendicular(Vector2 vector) {
		return MathUtil.isZero(dot(vector));
	}

	public boolean isPerpendicular(Vector2 vector, float epsilon) {
		return MathUtil.isZero(dot(vector), epsilon);
	}

	public boolean hasSameDirection(Vector2 vector) {
		return dot(vector) > 0;
	}

	public boolean hasOppositeDirection(Vector2 vector) {
		return dot(vector) < 0;
	}

	public Vector2 setZero() {
		this.x = 0;
		this.y = 0;
		return this;
	}
}
