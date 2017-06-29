package com.limitart.math;

/**
 * 矩形
 */
public class Rect implements Shape2D {
	private float x, y;
	private float width, height;

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

	public Rect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rect(Rect rect) {
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
	}

	public boolean contains(float x, float y) {
		return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
	}

	public boolean contains(Vector2 point) {
		return contains(point.getX(), point.getY());
	}

	public boolean contains(Circle circle) {
		return (circle.getX() - circle.getRadius() >= x) && (circle.getX() + circle.getRadius() <= x + width)
				&& (circle.getY() - circle.getRadius() >= y) && (circle.getY() + circle.getRadius() <= y + height);
	}

	public boolean contains(Rect rectangle) {
		float xmin = rectangle.x;
		float xmax = xmin + rectangle.width;
		float ymin = rectangle.y;
		float ymax = ymin + rectangle.height;
		return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
				&& ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
	}

	public boolean overlaps(Rect r) {
		return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
	}

	public void merge(Rect rect) {
		float minX = Math.min(x, rect.x);
		float maxX = Math.max(x + width, rect.x + rect.width);
		x = minX;
		width = maxX - minX;
		float minY = Math.min(y, rect.y);
		float maxY = Math.max(y + height, rect.y + rect.height);
		y = minY;
		height = maxY - minY;
	}

	public void merge(float x, float y) {
		float minX = Math.min(this.x, x);
		float maxX = Math.max(this.x + width, x);
		this.x = minX;
		this.width = maxX - minX;
		float minY = Math.min(this.y, y);
		float maxY = Math.max(this.y + height, y);
		this.y = minY;
		this.height = maxY - minY;
	}

	public void merge(Vector2 vec) {
		merge(vec.getX(), vec.getY());
	}

	public float getAspectRatio() {
		return (height == 0) ? Float.NaN : width / height;
	}

	public Vector2 getCenter() {
		Vector2 temp = new Vector2();
		temp.setX(x + width / 2);
		temp.setY(y + height / 2);
		return temp;
	}

	public void setCenter(float x, float y) {
		setX(x - width / 2);
		setY(y - height / 2);
	}

	public void setCenter(Vector2 position) {
		setX(position.getX() - width / 2);
		setY(position.getY() - height / 2);
	}

	public void fitOutside(Rect rect) {
		float ratio = getAspectRatio();

		if (ratio > rect.getAspectRatio()) {
			// Wider than tall
			setWidth(rect.height * ratio);
			setHeight(rect.height);
		} else {
			// Taller than wide
			setWidth(rect.width);
			setHeight(rect.width / ratio);
		}
		setX((rect.x + rect.width / 2) - width / 2);
		setY((rect.y + rect.height / 2) - height / 2);
	}

	public void fitInside(Rect rect) {
		float ratio = getAspectRatio();
		if (ratio < rect.getAspectRatio()) {
			// Taller than wide
			setWidth(rect.height * ratio);
			setHeight(rect.height);
		} else {
			// Wider than tall
			setWidth(rect.width);
			setHeight(rect.width / ratio);
		}
		setX((rect.x + rect.width / 2) - width / 2);
		setY((rect.y + rect.height / 2) - height / 2);
	}

	public String toString() {
		return "[" + x + "," + y + "," + width + "," + height + "]";
	}

	public float area() {
		return this.width * this.height;
	}

	public float perimeter() {
		return 2 * (this.width + this.height);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToRawIntBits(height);
		result = prime * result + Float.floatToRawIntBits(width);
		result = prime * result + Float.floatToRawIntBits(x);
		result = prime * result + Float.floatToRawIntBits(y);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rect other = (Rect) obj;
		if (Float.floatToRawIntBits(height) != Float.floatToRawIntBits(other.height))
			return false;
		if (Float.floatToRawIntBits(width) != Float.floatToRawIntBits(other.width))
			return false;
		if (Float.floatToRawIntBits(x) != Float.floatToRawIntBits(other.x))
			return false;
		if (Float.floatToRawIntBits(y) != Float.floatToRawIntBits(other.y))
			return false;
		return true;
	}

}