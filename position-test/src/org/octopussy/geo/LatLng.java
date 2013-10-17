package org.octopussy.geo;

import java.util.Locale;

/**
 * @author octopussy
 */
public class LatLng {
	private final double mLat;
	private final double mLng;

	public LatLng(double lat, double lng) {
		mLat = lat;
		mLng = lng;
	}

	public double getLat() {
		return mLat;
	}

	public double getLng() {
		return mLng;
	}


	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "%f, %f", mLat, mLng);
	}
}
