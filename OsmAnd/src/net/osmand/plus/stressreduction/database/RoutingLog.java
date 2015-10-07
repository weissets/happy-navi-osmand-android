package net.osmand.plus.stressreduction.database;

import net.osmand.data.LatLon;

/**
 * This class represents a log of a single routing event
 *
 * @author Tobias
 */
public class RoutingLog {

	private final double startLat;

	private final double startLon;

	private final double endLat;

	private final double endLon;

	private final String timeArrivalPreCalc;

	private String timeArrivalReal;

	private final String timeRoutingStart;

	private String timeRoutingEnd;

	public RoutingLog(LatLon start, LatLon end, String timeArrivalPreCalc, String timeRoutingStart) {
		startLat = start.getLatitude();
		startLon = start.getLongitude();
		endLat = end.getLatitude();
		endLon = end.getLongitude();
		this.timeArrivalPreCalc = timeArrivalPreCalc;
		this.timeRoutingStart = timeRoutingStart;
		timeArrivalReal = null;
		timeRoutingEnd = null;
	}

	public void setTimeArrivalReal(String timeArrivalReal) {
		this.timeArrivalReal = timeArrivalReal;
	}

	public void setTimeRoutingEnd(String timeRoutingEnd) {
		this.timeRoutingEnd = timeRoutingEnd;
	}

	public double getEndLat() {
		return endLat;
	}

	public double getEndLon() {
		return endLon;
	}

	public double getStartLat() {
		return startLat;
	}

	public double getStartLon() {
		return startLon;
	}

	public String getTimeArrivalPreCalc() {
		return timeArrivalPreCalc;
	}

	public String getTimeArrivalReal() {
		return timeArrivalReal;
	}

	public String getTimeRoutingEnd() {
		return timeRoutingEnd;
	}

	public String getTimeRoutingStart() {
		return timeRoutingStart;
	}

	public boolean isLogComplete() {
		return timeArrivalReal != null && timeRoutingEnd != null;
	}

}
