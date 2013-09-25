package org.octopussy;

import com.badlogic.gdx.math.Matrix4;

/**
 * @author octopussy
 */
public interface PositionProvider {
	Matrix4 getRotationMatrix();

	boolean useManualControl();
}
