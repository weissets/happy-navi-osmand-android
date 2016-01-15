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
	private double abortLat;
	private double abortLon;
	private final String timeRoutingStart;
	private String timeRoutingEndCalc;
	private String timeRoutingEnd;
	private String timeRoutingAbort;
	private int distanceToEnd;
	private boolean usedSrRoute;
	private int usedSrLevel;
	private int distanceSrRoute;
	private int distanceNormalRoute;
	private int timeSrRoute;
	private int timeNormalRoute;

	public RoutingLog(LatLon start, LatLon end, String timeRoutingEndCalc,
	                  String timeRoutingStart) {
		startLat = start.getLatitude();
		startLon = start.getLongitude();
		endLat = end.getLatitude();
		endLon = end.getLongitude();
		this.timeRoutingStart = timeRoutingStart;
		this.timeRoutingEndCalc = timeRoutingEndCalc;
		usedSrRoute = false;
		distanceSrRoute = 0;
		timeSrRoute = 0;
	}

	public void setAbortLat(double abortLat) {
		this.abortLat = abortLat;
	}

	public void setAbortLon(double abortLon) {
		this.abortLon = abortLon;
	}

	public void setTimeRoutingEnd(String timeRoutingEnd) {
		this.timeRoutingEnd = timeRoutingEnd;
	}

	public void setTimeRoutingAbort(String timeRoutingAbort) {
		this.timeRoutingAbort = timeRoutingAbort;
	}

	public void setDistanceToEnd(int distanceToEnd) {
		this.distanceToEnd = distanceToEnd;
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

	public double getAbortLat() {
		return abortLat;
	}

	public double getAbortLon() {
		return abortLon;
	}

	public String getTimeRoutingStart() {
		return timeRoutingStart;
	}

	public String getTimeRoutingEndCalc() {
		return timeRoutingEndCalc;
	}

	public String getTimeRoutingEnd() {
		return timeRoutingEnd;
	}

	public String getTimeRoutingAbort() {
		return timeRoutingAbort;
	}

	public int getDistanceToEnd() {
		return distanceToEnd;
	}

	public void setUsedSrRoute(boolean usedSrRoute) {
		this.usedSrRoute = usedSrRoute;
	}

	public void setUsedSrLevel(int usedSrLevel) { this.usedSrLevel = usedSrLevel; }

	public void setDistanceNormalRoute(int distanceNormalRoute) {
		this.distanceNormalRoute = distanceNormalRoute;
	}

	public void setDistanceSrRoute(int distanceSrRoute) {
		this.distanceSrRoute = distanceSrRoute;
	}

	public void setTimeNormalRoute(int timeNormalRoute) {
		this.timeNormalRoute = timeNormalRoute;
	}

	public void setTimeSrRoute(int timeSrRoute) {
		this.timeSrRoute = timeSrRoute;
	}

	public boolean getUsedSrRoute() {
		return usedSrRoute;
	}

	public int getUsedSrLevel() { return usedSrLevel; }

	public int getDistanceNormalRoute() {
		return distanceNormalRoute;
	}

	public int getDistanceSrRoute() {
		return distanceSrRoute;
	}

	public int getTimeNormalRoute() {
		return timeNormalRoute;
	}

	public int getTimeSrRoute() {
		return timeSrRoute;
	}

}
