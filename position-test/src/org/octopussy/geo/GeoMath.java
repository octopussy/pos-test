package org.octopussy.geo;

/**
 * @author octopussy
 */
public class GeoMath {
	private static final double EARTH_RADIUS = 6371;

	public static Vector3 fromLatLngToECEF(double lat, double lng) {
		double rad_lat = Math.toRadians(lat);
		double rad_lng = Math.toRadians(lng);

		return new Vector3(EARTH_RADIUS * Math.cos(rad_lat) * Math.cos(rad_lng),
			EARTH_RADIUS * Math.cos(rad_lat) * Math.sin(rad_lng),
			EARTH_RADIUS * Math.sin(rad_lat));
	}

	public static Vector3 fromLatLngToECEF(LatLng geo) {
		return fromLatLngToECEF(geo.getLat(), geo.getLng());
	}

	public static LatLng fromECEFToLatLng(Vector3 ecef) {
		double p = Math.sqrt(ecef.getX() * ecef.getX() + ecef.getY() * ecef.getY());

		double lat = Math.atan(ecef.getZ() / (p));
		double lng = Math.atan(ecef.getY() / ecef.getX());
		return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));
	}
}
