package net.osmand.plus.stressreduction.tools;

import net.osmand.plus.stressreduction.Constants;

import java.util.List;

/**
 * This class is a helper class for different calculations
 *
 * @author Tobias
 */
public class Calculation {

	/**
	 * Convert a given float value in m/s to a rounded value in km/h
	 *
	 * @param ms The float value in m/s
	 * @return The calculated rounded value in km/h
	 */
	public static int convertMsToKmh(float ms) {
		return Math.round(ms * Constants.MS_TO_KMH);
	}

	/**
	 * Calculate the average value of a given list of floats
	 *
	 * @param values A list of float values
	 * @return The calculated average value
	 */
	public static float getAverageValue(List<Float> values) {
		int size = values.size();
		float sum = 0;
		for (int i = 0; i < size; i++) {
			sum = sum + values.get(i);
		}
		return (sum / size);
	}

}
