package org.octopussy;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;

import java.util.Arrays;

public class MainActivity extends AndroidApplication implements SensorEventListener {
	private static final double EPSILON = 0.01;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagneticFieldSensor;
	private float[] mGravityValues = {0f, 0f, 0f};
	private float[] mMagneticFieldValues = {0f, 0f, 0f};
	private Matrix4 mRotationMatrix = new Matrix4();
	private Sensor mRotationSensor;
	private Quaternion mRotationVector;
	private float[] gravity = new float[3];
	private Sensor mGyroSensor;
	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ViewGroup rootLayout = (ViewGroup)findViewById(R.id.content);
		final CheckBox checkUseManualControl = (CheckBox)findViewById(R.id.checkBox);

		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;

		View glView = initializeForView(new PositionTest(new PositionProvider(){
			@Override
			public Matrix4 getRotationMatrix() {
				return mRotationMatrix;
			}

			@Override
			public boolean useManualControl() {
				return checkUseManualControl.isChecked();
			}
		}), cfg);

		rootLayout.addView(glView, 0);

		mHandler = new Handler();
		tick();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mMagneticFieldSensor);
		mSensorManager.unregisterListener(this, mGyroSensor);
	}

	private static final float NS2S = 1.0f / 1000000000.0f;
	private float[] deltaRotationVector = new float[4];
	private float timestamp;

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mAccelerometer){
			mGravityValues = event.values.clone();
		}else if (event.sensor == mMagneticFieldSensor){
			mMagneticFieldValues = event.values.clone();
		}else if (event.sensor == mRotationSensor){
			mRotationVector = new Quaternion(event.values[0], event.values[1], event.values[2], event.values[3]);
		}else if (event.sensor == mGyroSensor){
			// This timestep's delta rotation to be multiplied by the current rotation
			// after computing it from the gyro sample data.
			deltaRotationVector = new float[4];
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				// Axis of the rotation sample, not normalized yet.
				float axisX = event.values[0];
				float axisY = event.values[1];
				float axisZ = event.values[2];

				// Calculate the angular speed of the sample
				double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

				// Normalize the rotation vector if it's big enough to get the axis
				// (that is, EPSILON should represent your maximum allowable margin of error)
				if (omegaMagnitude > EPSILON) {
					axisX /= omegaMagnitude;
					axisY /= omegaMagnitude;
					axisZ /= omegaMagnitude;
				}

				// Integrate around this axis with the angular speed by the timestep
				// in order to get a delta rotation from this sample over the timestep
				// We will convert this axis-angle representation of the delta rotation
				// into a quaternion before turning it into the rotation matrix.
				double thetaOverTwo = omegaMagnitude * dT / 2.0f;
				double sinThetaOverTwo = Math.sin(thetaOverTwo);
				double cosThetaOverTwo = Math.cos(thetaOverTwo);
				deltaRotationVector[0] = (float)sinThetaOverTwo * axisX;
				deltaRotationVector[1] = (float)sinThetaOverTwo * axisY;
				deltaRotationVector[2] = (float)sinThetaOverTwo * axisZ;
				deltaRotationVector[3] = (float)cosThetaOverTwo;
			}
			timestamp = event.timestamp;
			float[] deltaRotationMatrix = new float[16];
			SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
			Log.d("fdf", Arrays.toString(deltaRotationVector));
			//mRotationMatrix = new Matrix4(deltaRotationMatrix).mul(mRotationMatrix);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	private void tick(){
		float[] r = new float[16];
		if (SensorManager.getRotationMatrix(r, null, mGravityValues, mMagneticFieldValues)){
			mRotationMatrix = new Matrix4(r).rotate(1, 0, 0, 90).tra();
		}

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				tick();
			}
		}, 10);
	}
}