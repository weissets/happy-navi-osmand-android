package net.osmand.plus.stressreduction.sensors;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;

/**
 * This class handles the sensors
 *
 * @author Tobias
 */
public class SensorHandler {

	private final SRLocation location;
	private final SRAccelerometer accelerometer;

	public SensorHandler(OsmandApplication osmandApplication, DataHandler dataHandler,
	                     FragmentHandler fragmentHandler) {
		location = new SRLocation(osmandApplication, dataHandler, fragmentHandler);
		accelerometer = new SRAccelerometer(osmandApplication, dataHandler);
	}

	public void startSensors() {
		location.startLocationListener();
		accelerometer.startAccelerometerSensor();
	}

	public void stopSensors() {
		location.stopLocationListener();
		accelerometer.stopAccelerometerSensor();
	}

}
