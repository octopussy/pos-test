package org.octopussy.positioning.impl;

import org.octopussy.V3;
import org.octopussy.positioning.House;
import org.octopussy.positioning.SceneObjects;
import org.octopussy.positioning.SceneObjectsListener;
import org.octopussy.positioning.SceneObjectsProvider;

/**
 * @author octopussy
 */
public class DummySceneObjectsProviderImpl implements SceneObjectsProvider {
	@Override
	public void request(SceneObjectsListener listener) {
		SceneObjects objects = new SceneObjects();
		objects.addHouse(new House(new V3[]{new V3(0, 0, 0), new V3(1, 0, 0), new V3(1, 0, 1), new V3(0, 0, 1)}));
		listener.onResult(objects);
	}
}
