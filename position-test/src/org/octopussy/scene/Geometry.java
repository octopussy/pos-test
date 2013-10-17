package org.octopussy.scene;

import org.octopussy.geo.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author octopussy
 */
public class Geometry {
	private final List<Vector3> mPoints = new ArrayList<Vector3>();

	public void addPoint(Vector3 vector3) {
		mPoints.add(vector3);
	}

	public List<Vector3> getPoints() {
		return mPoints;
	}
}
