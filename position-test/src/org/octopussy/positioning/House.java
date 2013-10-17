package org.octopussy.positioning;

import org.octopussy.V3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author octopussy
 */
public class House {
	private List<V3> mPoints;
	public House(V3[] points) {
		mPoints = new ArrayList<V3>(Arrays.asList(points));
	}

	public List<V3> getPoints() {
		return mPoints;
	}
}
