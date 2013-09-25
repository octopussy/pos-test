package org.octopussy;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.materials.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import java.util.AbstractCollection;
import java.util.ArrayList;

public class PositionTest implements ApplicationListener {
	private static final float SIDE = 100;
	private static final float H_SIDE = SIDE / 2;
	private PerspectiveCamera camera;

	private ModelBatch modelBatch;
	private CameraInputController camControl;
	private AbstractCollection<Model> models;
	private AbstractCollection<ModelInstance> sides;

	private final PositionProvider mPositionProvider;
	private boolean mUseManualControl;

	public PositionTest(PositionProvider positionProvider) {
		mPositionProvider = positionProvider;
	}

	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new PerspectiveCamera(100, w, h);
		camera.position.set(0, 0, 0);
		camera.lookAt(0, 0, -1);
		camera.near = 0.1f;
		camera.far = 500f;
		camera.update();

		models = new ArrayList<Model>();
		sides = new ArrayList<ModelInstance>();
		addSide(0, 0, -H_SIDE, 0, 0, 0, 0, Color.GREEN);
		addSide(0, 0, -H_SIDE, 0, -1, 0, 90, Color.RED);
		addSide(0, 0, -H_SIDE, 0, -1, 0, 180, Color.BLUE);
		addSide(0, 0, -H_SIDE, 0, 1, 0, 90, Color.PINK);

		modelBatch = new ModelBatch();

		camControl = new CameraInputController(camera);
	}

	private void addSide(float tx, float ty, float tz, float rx, float ry, float rz, float deg, Color color) {
		ModelBuilder mb = new ModelBuilder();
		Model model = mb.createRect(
			-H_SIDE, -H_SIDE, 0,
			H_SIDE, -H_SIDE, 0,
			H_SIDE, H_SIDE, 0,
			-H_SIDE, H_SIDE, 0, 0, 0, -1,
			new Material(ColorAttribute.createDiffuse(color)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


		ModelInstance side = new ModelInstance(model);
		side.transform.idt();
		side.transform.rotate(rx, ry, rz, deg);
		side.transform.translate(tx, ty, tz);
		sides.add(side);
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		for (Model m:models){
			m.dispose();
		}
	}

	@Override
	public void render() {
		if (mPositionProvider.useManualControl() && !mUseManualControl){
			Gdx.input.setInputProcessor(camControl);
		}else if (!mPositionProvider.useManualControl() && mUseManualControl){
			Gdx.input.setInputProcessor(null);
		}
		mUseManualControl = mPositionProvider.useManualControl();
		if (!mUseManualControl){

			camera.position.set(0, 0, 0);
			camera.direction.set(0, 0, -1);
			camera.up.set(0, 1, 0);

			//camera.lookAt(0, -1, 0);
			camera.transform(mPositionProvider.getRotationMatrix());
			//camera.up.set(0, 1, 0);
		}

		camera.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(camera);
		for (ModelInstance m:sides){
			modelBatch.render(m);
		}
		modelBatch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
