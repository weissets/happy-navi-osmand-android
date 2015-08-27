package net.osmand.plus.stressreduction.database;

import java.text.SimpleDateFormat;
import java.util.Locale;

import net.osmand.binary.RouteDataObject;

/**
 * This class represents the information about a single route segment
 *
 * @author Tobias
 */
public class SegmentInfo extends RouteDataObject {

	/** Object which contains all relevant information about the current route segment */
	private final RouteDataObject routeDataObject;

	/** The average speed for this segment */
	private final int averageSpeed;

	/** The timestamp for this segment */
	private final String timestamp;

	/** The stress value for this segment; 2 = no stress, 1 = neutral, 0 = stress */
	private int stressValue;

	/**
	 * Constructor of the segment info
	 *
	 * @param routeDataObject The route data object
	 * @param averageSpeed    The average speed for this route segment
	 */
	public SegmentInfo(RouteDataObject routeDataObject, int averageSpeed) {
		super(routeDataObject);
		this.routeDataObject = routeDataObject;
		this.averageSpeed = averageSpeed;
		this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
				.format(new java.util.Date());
		stressValue = -99;
	}

	/**
	 * Getter of the segment route data object
	 *
	 * @return The RouteDataObject of this segment
	 */
	public RouteDataObject getRouteDataObject() {
		return routeDataObject;
	}

	/**
	 * Getter of the segment average speed
	 *
	 * @return The average speed of this segment
	 */
	public int getAverageSpeed() {
		return averageSpeed;
	}

	/**
	 * Getter of the segment timestamp
	 *
	 * @return The timestamp of this segment
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Setter of the segment stress value
	 *
	 * @param stressValue The stress value of this segment
	 */
	public void setStressValue(int stressValue) {
		this.stressValue = stressValue;
	}

	/**
	 * Getter of the segment stress value
	 *
	 * @return The stress value of this segment
	 */
	public int getStressValue() {
		return stressValue;
	}

}
