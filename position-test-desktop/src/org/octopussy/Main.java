package org.octopussy;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Matrix4;
import org.octopussy.postest.PositionTest;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "position-test";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;
		
		new LwjglApplication(new PositionTest(new PositionProvider() {
			@Override
			public Matrix4 getRotationMatrix() {
				return new Matrix4();
			}

			@Override
			public boolean useManualControl() {
				return false;
			}
		}), cfg);
	}
}
