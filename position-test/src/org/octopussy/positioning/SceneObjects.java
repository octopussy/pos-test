package org.octopussy.positioning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author octopussy
 */
public class SceneObjects {
	private final Collection<House> mHouses = new ArrayList<House>();

	public void addHouse(House h){
		mHouses.add(h);
	}

	public Collection<House> getHouses() {
		return Collections.unmodifiableCollection(mHouses);
	}
}
