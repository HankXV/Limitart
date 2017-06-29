package com.limitart.math;

import com.limitart.math.util.MathUtil;

/**
 * 圆形
 */
public class Circle implements Shape2D {
	private float x, y;
	private float radius;

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getRadius() {
		return radius;
	}

	public Circle(float x, float y, float radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	public Circle(Vector2 position, float radius) {
		this.x = position.getX();
		this.y = position.getY();
		this.radius = radius;
	}

	public Circle(Circle circle) {
		this.x = circle.x;
		this.y = circle.y;
		this.radius = circle.radius;
	}

	public Circle(Vector2 center, Vector2 edge) {
		this.x = center.getX();
		this.y = center.getY();
		this.radius = new Vector2(center.getX() - edge.getX(), center.getY() - edge.getY()).len();
	}

	/**
	 * Sets a new location and radius for this circle, based upon another
	 * circle.
	 * 
	 * @param circle
	 *            The circle to copy the position and radius of.
	 */
	public void set(Circle circle) {
		this.x = circle.x;
		this.y = circle.y;
		this.radius = circle.radius;
	}

	public boolean contains(float x, float y) {
		x = this.x - x;
		y = this.y - y;
		return x * x + y * y <= radius * radius;
	}

	public boolean contains(Vector2 point) {
		float dx = x - point.getX();
		float dy = y - point.getY();
		return dx * dx + dy * dy <= radius * radius;
	}

	public boolean contains(Circle c) {
		final float radiusDiff = radius - c.radius;
		if (radiusDiff < 0f)
			return false; // Can't contain bigger circle
		final float dx = x - c.x;
		final float dy = y - c.y;
		final float dst = dx * dx + dy * dy;
		final float radiusSum = radius + c.radius;
		return (!(radiusDiff * radiusDiff < dst) && (dst < radiusSum * radiusSum));
	}

	public boolean overlaps(Circle c) {
		float dx = x - c.x;
		float dy = y - c.y;
		float distance = dx * dx + dy * dy;
		float radiusSum = radius + c.radius;
		return distance < radiusSum * radiusSum;
	}

	@Override
	public String toString() {
		return x + "," + y + "," + radius;
	}

	public float circumference() {
		return this.radius * MathUtil.PI_FLOAT_2X;
	}

	public float area() {
		return this.radius * this.radius * MathUtil.PI_FLOAT;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		Circle c = (Circle) o;
		return this.x == c.x && this.y == c.y && this.radius == c.radius;
	}

	@Override
	public int hashCode() {
		final int prime = 41;
		int result = 1;
		result = prime * result + Float.floatToRawIntBits(radius);
		result = prime * result + Float.floatToRawIntBits(x);
		result = prime * result + Float.floatToRawIntBits(y);
		return result;
	}
}
