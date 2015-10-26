package net.osmand.plus.stressreduction.database;

import net.osmand.plus.stressreduction.tools.Calculation;

/**
 * This class represents the information about a single location
 *
 * @author Tobias
 */
public class LocationInfo {

	/** The latitude of this location */
	private final double latitude;

	/** The longitude of this location */
	private final double longitude;

	/** The speed of this location */
	private final double speed;

	/** The x-axis acceleration of this location */
	private final double accelerationX;

	/** The y-axis acceleration of this location */
	private final double accelerationY;

	/** The z-axis acceleration of this location */
	private final double accelerationZ;

	/**
	 * The direction in degrees east of true north of this location
	 * (Bearing: 0=North, 90=East, 180=South, 270=West)
	 */
	private final double direction;

	/** The timestamp of this location */
	private final String timestamp;

	/**
	 * Constructor of the location info
	 *
	 * @param latitude      The location latitude
	 * @param longitude     The location longitude
	 * @param speed         The location speed
	 * @param accelerationX The location x-axis acceleration
	 * @param accelerationY The location y-axis acceleration
	 * @param accelerationZ The location z-axis acceleration
	 * @param direction     The location direction in degrees east of true north
	 */
	public LocationInfo(double latitude, double longitude, double speed, double accelerationX,
	                    double accelerationY, double accelerationZ, double direction) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.speed = speed;
		this.accelerationX = accelerationX;
		this.accelerationY = accelerationY;
		this.accelerationZ = accelerationZ;
		this.direction = direction;
		this.timestamp = Calculation.getCurrentDateTimeMs();
	}

	/**
	 * Getter of the location latitude
	 *
	 * @return The latitude of this location
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Getter of the location longitude
	 *
	 * @return The longitude of this location
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Getter of the location speed
	 *
	 * @return The speed of this location
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Getter of the location x-axis acceleration
	 *
	 * @return The x-axis acceleration of this location
	 */
	public double getAccelerationX() {
		return accelerationX;
	}

	/**
	 * Getter of the location y-axis acceleration
	 *
	 * @return The y-axis acceleration of this location
	 */
	public double getAccelerationY() {
		return accelerationY;
	}

	/**
	 * Getter of the location z-axis acceleration
	 *
	 * @return The z-axis acceleration of this location
	 */
	public double getAccelerationZ() {
		return accelerationZ;
	}

	/**
	 * Getter of the location direction in degrees east of true north
	 *
	 * @return The direction in degrees east of true north of this location
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * Getter of the location timestamp
	 *
	 * @return The timestamp of this location
	 */
	public String getTimestamp() {
		return timestamp;
	}

}
