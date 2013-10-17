package org.octopussy.geo;

/**
 * @author octopussy
 */
public class Vector3 {
	private final double mX;
	private final double mY;
	private final double mZ;

	public Vector3(double x, double y, double z) {
		mX = x;
		mY = y;
		mZ = z;
	}

	public double getX() {
		return mX;
	}

	public double getY() {
		return mY;
	}

	public double getZ() {
		return mZ;
	}

	@Override
	public String toString() {
		return String.format("[%f %f %f]", mX, mY, mZ);
	}
}
