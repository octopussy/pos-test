package org.octopussy.scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author octopussy
 */
public class Scene {
	private final Collection<Building> mBuildings = new ArrayList<Building>();
	private final Collection<Road> mRoads = new ArrayList<Road>();

	public void addBuilding(Building building){
		mBuildings.add(building);
	}

	public Collection<Building> getBuildings() {
		return Collections.unmodifiableCollection(mBuildings);
	}
}
