package com.limitart.math;

/**
 * 2D格子
 */
public class GridPoint2 {
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public GridPoint2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public GridPoint2(GridPoint2 point) {
		this.x = point.x;
		this.y = point.y;
	}

	public float distance2(GridPoint2 other) {
		int xd = other.x - x;
		int yd = other.y - y;
		return xd * xd + yd * yd;
	}

	public float distance2(int x, int y) {
		int xd = x - this.x;
		int yd = y - this.y;
		return xd * xd + yd * yd;
	}

	public float distance(GridPoint2 other) {
		int xd = other.x - x;
		int yd = other.y - y;
		return (float) Math.sqrt(xd * xd + yd * yd);
	}

	public float distance(int x, int y) {
		int xd = x - this.x;
		int yd = y - this.y;
		return (float) Math.sqrt(xd * xd + yd * yd);
	}

	public void add(GridPoint2 other) {
		x += other.x;
		y += other.y;
	}

	public void add(int x, int y) {
		this.x += x;
		this.y += y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || o.getClass() != this.getClass())
			return false;
		GridPoint2 g = (GridPoint2) o;
		return this.x == g.x && this.y == g.y;
	}

	@Override
	public int hashCode() {
		final int prime = 53;
		int result = 1;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
