package com.limitart.math;

import com.limitart.math.util.MathUtil;

/**
 * 椭圆
 */
public class Ellipse implements Shape2D {

	public float x, y;
	public float width, height;

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

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public Ellipse(Ellipse ellipse) {
		this.x = ellipse.x;
		this.y = ellipse.y;
		this.width = ellipse.width;
		this.height = ellipse.height;
	}

	public Ellipse(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Ellipse(Vector2 position, float width, float height) {
		this.x = position.getX();
		this.y = position.getY();
		this.width = width;
		this.height = height;
	}

	public Ellipse(Vector2 position, Vector2 size) {
		this.x = position.getX();
		this.y = position.getY();
		this.width = size.getX();
		this.height = size.getY();
	}

	public Ellipse(Circle circle) {
		this.x = circle.getX();
		this.y = circle.getY();
		this.width = circle.getRadius();
		this.height = circle.getRadius();
	}

	public boolean contains(float x, float y) {
		x = x - this.x;
		y = y - this.y;
		return (x * x) / (width * 0.5f * width * 0.5f) + (y * y) / (height * 0.5f * height * 0.5f) <= 1.0f;
	}

	public boolean contains(Vector2 point) {
		return contains(point.getX(), point.getY());
	}

	public float area() {
		return MathUtil.PI_FLOAT * (this.width * this.height) / 4;
	}

	public float circumference() {
		float a = this.width / 2;
		float b = this.height / 2;
		if (a * 3 > b || b * 3 > a) {
			// If one dimension is three times as long as the other...
			return (float) (MathUtil.PI_FLOAT * ((3 * (a + b)) - Math.sqrt((3 * a + b) * (a + 3 * b))));
		} else {
			// We can use the simpler approximation, then
			return (float) (MathUtil.PI_FLOAT_2X * Math.sqrt((a * a + b * b) / 2));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		Ellipse e = (Ellipse) o;
		return this.x == e.x && this.y == e.y && this.width == e.width && this.height == e.height;
	}

	@Override
	public int hashCode() {
		final int prime = 53;
		int result = 1;
		result = prime * result + Float.floatToRawIntBits(this.height);
		result = prime * result + Float.floatToRawIntBits(this.width);
		result = prime * result + Float.floatToRawIntBits(this.x);
		result = prime * result + Float.floatToRawIntBits(this.y);
		return result;
	}
}
