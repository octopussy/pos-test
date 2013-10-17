package org.octopussy.geo;

/**
 * @author octopussy
 */
public class ENUCoordinateSystem {

	private final Vector3 mCenter;

	private final double sinLng;
	private final double cosLng;
	private final double sinLat;
	private final double cosLat;

	public ENUCoordinateSystem(double lat, double lng) {
		sinLng = Math.sin(lng);
		cosLng = Math.cos(lng);
		sinLat = Math.sin(lat);
		cosLat = Math.cos(lat);

		mCenter = GeoMath.fromLatLngToECEF(lat, lng);
	}

	public ENUCoordinateSystem(LatLng c) {
		this(c.getLat(), c.getLng());
	}

	public Vector3 fromLatLngToLocal(double lat, double lng) {
		Vector3 v = GeoMath.fromLatLngToECEF(lat, lng);

		double vx = v.getX() - mCenter.getX();
		double vy = v.getY() - mCenter.getY();
		double vz = v.getZ() - mCenter.getZ();

		return new Vector3(
			vx * -sinLng + vy * cosLng + vz * 0,
			vx * (-sinLat * cosLng) + vy * (-sinLat * sinLng) + vz * cosLat,
			vx * (cosLat * cosLng) + vy * (cosLat * sinLng) + vz * sinLat);
	}

	public Vector3 fromLatLngToLocal(LatLng coords) {
		return fromLatLngToLocal(coords.getLat(), coords.getLng());
	}

	public LatLng fromLocalToLatLng(Vector3 c) {
		Vector3 ecef = new Vector3(
			-sinLng * c.getX() + (-sinLat * cosLng) * c.getY() + (cosLat * cosLng) * c.getZ() + mCenter.getX(),
			cosLng * c.getX() + (-sinLat * sinLng) * c.getY() + (cosLat * sinLng) * c.getZ()+ mCenter.getY(),
			0 + cosLat * c.getY() + sinLat * c.getZ() + mCenter.getZ()
		);
		return GeoMath.fromECEFToLatLng(ecef);
	}
}
