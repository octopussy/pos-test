package org.octopussy.postest;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author octopussy
 */

public class SensorFilter {
	private final Deque<float[]> values;
	private int length;
	private final float lowPassAlpha;
	private int smoothingSteps;

	public SensorFilter(int length, float lowPassAlpha, int smoothingSteps) {
		this.length = length;
		this.lowPassAlpha = lowPassAlpha;
		this.smoothingSteps = smoothingSteps;
		this.values = new LinkedList<float[]>();

	}

	public void addValues(float[] values) {
		if (values.length == length) {
			float[] filtered = lowPass(values, getSmoothedValue(), lowPassAlpha);
			this.values.add(filtered.clone());
			if (this.values.size() > smoothingSteps){
				this.values.pollFirst();
			}
		}
	}

	private float[] getSmoothedValue() {
		float result[] = new float[length];
		for (float[] v:values){
			for (int i=0; i<length; ++i){
				result[i] += v[i];
			}
		}
		for (int i=0; i<length; ++i){
			result[i] /= (float)values.size();
		}

		if (values.size() > 0){
			return result;
		}else {
			return new float[length];
		}
	}

	public float[] getResult() {
		if (values.size() > 0){
			return values.getLast();
		}else {
			return new float[length];
		}
	}

	private static float[] lowPass( float[] input, float[] output, float alpha ) {
		if (output == null){
			return input;
		}
		for ( int i=0; i<input.length; i++ ) {
			output[i] = output[i] + alpha * (input[i] - output[i]);
		}
		return output;
	}
}
