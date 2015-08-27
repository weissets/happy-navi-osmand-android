package net.osmand.plus.stressreduction.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import net.osmand.Location;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.database.LocationInfo;
import net.osmand.plus.stressreduction.tools.Calculation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the accelerometer
 *
 * @author Tobias
 */
class SRAccelerometer implements OsmAndLocationProvider.OsmAndLocationListener {

	private final DataHandler dataHandler;
	private final SensorManager sensorManager;
	private final AccelerometerListener accelerometerListener;
	private final OsmAndLocationProvider osmAndLocationProvider;
	private final Sensor accelerometerSensor;
	private Location currentLocation;

	public SRAccelerometer(OsmandApplication osmandApplication, DataHandler dataHandler) {
		this.dataHandler = dataHandler;
		osmAndLocationProvider = osmandApplication.getLocationProvider();
		sensorManager = (SensorManager) osmandApplication.getSystemService(Context.SENSOR_SERVICE);
		accelerometerListener = new AccelerometerListener();
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	/**
	 * Register the accelerometer and location listener
	 */
	public void startAccelerometerSensor() {
		// Sensor delays: FASTEST=0.01s, GAME=0.02s, UI=0.1s,
		// NORMAL=0.2s
		sensorManager.registerListener(accelerometerListener, accelerometerSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		osmAndLocationProvider.addLocationListener(this);
	}

	/**
	 * Unregister the accelerometer and location listener
	 */
	public void stopAccelerometerSensor() {
		sensorManager.unregisterListener(accelerometerListener, accelerometerSensor);
		osmAndLocationProvider.removeLocationListener(this);
	}

	@Override
	public void updateLocation(Location location) {
		currentLocation = location;
	}

	/**
	 * Class for the accelerometer sensor data
	 */
	private class AccelerometerListener implements SensorEventListener {

		final List<Float> accelerationXList = new ArrayList<>();
		final List<Float> accelerationYList = new ArrayList<>();
		final List<Float> accelerationZList = new ArrayList<>();
		final List<Float> locationSpeedList = new ArrayList<>();
		final List<Float> directionList = new ArrayList<>();

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (currentLocation != null) {
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
					// get acceleration values for all three axes
					accelerationXList.add(event.values[0]);
					accelerationYList.add(event.values[1]);
					accelerationZList.add(event.values[2]);
					// get the direction of heading (0=North, 90=East,
					// 180=South, 270=West)
					directionList.add(currentLocation.getBearing());
					locationSpeedList.add(currentLocation.getSpeed());
				}
				// get average values to reduce data size
				if (accelerationXList.size() == 10) {
					dataHandler.writeLocationInfoToDatabase(
							new LocationInfo(currentLocation.getLatitude(),
									currentLocation.getLongitude(), Calculation
									.convertMsToKmh(Calculation.getAverageValue
											(locationSpeedList)),
									Calculation.getAverageValue(accelerationXList),
									Calculation.getAverageValue(accelerationYList),
									Calculation.getAverageValue(accelerationZList),
									Calculation.getAverageValue(directionList)));

					accelerationXList.clear();
					accelerationYList.clear();
					accelerationZList.clear();
					locationSpeedList.clear();
					directionList.clear();
				}
			}
		}

		//		private double getMaxMinValue(List<Double> values) {
		//			int size = values.size();
		//			double sum = 0;
		//			double max = 0;
		//			double min = 0;
		//			for (int i = 0; i < size; i++) {
		//				sum = sum + values.get(i);
		//				max = Math.max(max, values.get(i));
		//				min = Math.min(min, values.get(i));
		//			}
		//			if (sum >= 0) {
		//				return max;
		//			} else {
		//				return min;
		//			}
		//		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	}
}
