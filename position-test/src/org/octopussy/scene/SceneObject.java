package org.octopussy.scene;

/**
 * @author octopussy
 */
public class SceneObject {
	private final Geometry mGeometry;

	public SceneObject(Geometry geometry) {
		mGeometry = geometry;
	}

	public Geometry getGeometry() {
		return mGeometry;
	}
}
