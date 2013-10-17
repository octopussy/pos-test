package org.octopussy.scene.impl;

import org.octopussy.scene.Scene;
import org.octopussy.scene.SceneObjectsListener;
import org.octopussy.scene.SceneObjectsProvider;

/**
 * @author octopussy
 */
public class DummySceneObjectsProviderImpl implements SceneObjectsProvider {
	@Override
	public void request(SceneObjectsListener listener) {
		Scene objects = new Scene();
		//objects.addBuilding(new Building(new V3[]{new V3(0, 0, 0), new V3(1, 0, 0), new V3(1, 0, 1), new V3(0, 0, 1)}));
		listener.onResult(objects);
	}
}
