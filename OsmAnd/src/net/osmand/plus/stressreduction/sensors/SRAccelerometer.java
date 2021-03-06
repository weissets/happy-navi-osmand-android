package net.osmand.plus.stressreduction.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.binary.RouteDataObject;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.database.LocationInfo;
import net.osmand.plus.stressreduction.tools.Calculation;

import org.apache.commons.logging.Log;

/**
 * This class handles the accelerometer
 *
 * @author Tobias
 */
public class SRAccelerometer implements OsmAndLocationProvider.OsmAndLocationListener {

	private static final Log log = PlatformUtil.getLog(SRAccelerometer.class);

	private final DataHandler dataHandler;
	private final SensorManager sensorManager;
	private final AccelerometerListener accelerometerListener;
	private final OsmAndLocationProvider osmAndLocationProvider;
	private final Sensor accelerometerSensor;
	private Location currentLocation;
	private int accelerometerType;

	private static RouteDataObject routeDataObject;

//	private final int ACCELEROMETER_LINEAR = 1;
	private static final int ACCELEROMETER_NORMAL = 2;
	private static final int ACCELEROMETER_NONE = 3;

	// TODO check how much data is generated -> 500kb per hour -> check again with real device!!!
	// INFO 500kb if no acceleration, else should be around 5 times as much
	public SRAccelerometer(OsmandApplication osmandApplication, DataHandler dataHandler) {
		this.dataHandler = dataHandler;
		osmAndLocationProvider = osmandApplication.getLocationProvider();
		sensorManager = (SensorManager) osmandApplication.getSystemService(Context.SENSOR_SERVICE);
		accelerometerListener = new AccelerometerListener();
		accelerometerSensor = getBestAccelerometerSensor();
		// if there is no accelerometer write this to database
//		if (accelerometerType == ACCELEROMETER_NONE) {
//
//		}
	}

	private Sensor getBestAccelerometerSensor() {
		// values of linear acceleration are worse then normalized acceleration values
//		if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
//			List<Sensor> linearSensors =
//					sensorManager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
//			for (Sensor sensor : linearSensors) {
//				log.debug("getBestAccelerometerSensor(): vendor = " + sensor.getVendor() +
//						", version = " + sensor.getVersion());
//				if ((sensor.getVendor().contains("AOSP") ||
//						sensor.getVendor().contains("Google")) && sensor.getVersion() == 3) {
//					accelerometerType = ACCELEROMETER_LINEAR;
//					return sensor;
//				}
//			}
//		}
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			accelerometerType = ACCELEROMETER_NORMAL;
			return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		accelerometerType = ACCELEROMETER_NONE;
		return null;
	}

	/**
	 * Register the accelerometer and location listener
	 */
	public void startAccelerometerSensor() {
		if (accelerometerType != ACCELEROMETER_NONE) {
			// Sensor delays: FASTEST=0.01s, GAME=0.02s, UI=0.1s,
			// NORMAL=0.2s
			sensorManager.registerListener(accelerometerListener, accelerometerSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			osmAndLocationProvider.addLocationListener(this);
			log.debug("startAccelerometerSensor()");
		}
	}

	/**
	 * Unregister the accelerometer and location listener
	 */
	public void stopAccelerometerSensor() {
		if (accelerometerType != ACCELEROMETER_NONE) {
			sensorManager.unregisterListener(accelerometerListener, accelerometerSensor);
			osmAndLocationProvider.removeLocationListener(this);
			log.debug("stopAccelerometerSensor()");
		}
	}

	@Override
	public void updateLocation(Location location) {
		currentLocation = location;
	}

	/**
	 * Class for the accelerometer sensor data
	 *
	 * http://www.mit.edu/afs.new/sipb/project/android/docs/guide/topics/sensors/sensors_motion.html
	 */
	private class AccelerometerListener implements SensorEventListener {

		float[] accelerationXYZ = {0, 0, 0};
		float[] gravityXYZ = {0, 0, 0};
		final float ALPHA = 0.8f;
		final float THRESHOLD = 0.1f;
		long id;

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (currentLocation != null) {
//				if (accelerometerType == ACCELEROMETER_NORMAL) {

					// alpha is calculated as t / (t + dT) with t, the low-pass filter's
					// time-constant and dT, the event delivery rate

					gravityXYZ[0] = ALPHA * gravityXYZ[0] + (1 - ALPHA) * event.values[0];
					gravityXYZ[1] = ALPHA * gravityXYZ[1] + (1 - ALPHA) * event.values[1];
					gravityXYZ[2] = ALPHA * gravityXYZ[2] + (1 - ALPHA) * event.values[2];

					accelerationXYZ[0] = event.values[0] - gravityXYZ[0];
					accelerationXYZ[1] = event.values[1] - gravityXYZ[1];
					accelerationXYZ[2] = event.values[2] - gravityXYZ[2];

					accelerationXYZ = normalizeAcceleration(accelerationXYZ.clone());

				routeDataObject = osmAndLocationProvider.getLastKnownRouteSegment();
				if (routeDataObject != null) {
					id = routeDataObject.id;
				} else {
					id = -1;
				}

				dataHandler.writeLocationInfoToDatabase(
						new LocationInfo(currentLocation.getLatitude(),
								currentLocation.getLongitude(),
								Calculation.convertMsToKmh(currentLocation.getSpeed()),
								accelerationXYZ[0], accelerationXYZ[1], accelerationXYZ[2],
								currentLocation.getBearing(), id));
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		// TODO check how much normalization
		private float[] normalizeAcceleration(float[] accelerationXYZ) {
			for (int i=0; i<accelerationXYZ.length; i++) {
				if (accelerationXYZ[i] < THRESHOLD && accelerationXYZ[i] > -THRESHOLD) {
					accelerationXYZ[i] = 0;
				}
			}
			return accelerationXYZ;
		}
	}
}
