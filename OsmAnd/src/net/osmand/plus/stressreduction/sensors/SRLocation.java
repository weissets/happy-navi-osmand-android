package net.osmand.plus.stressreduction.sensors;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.ValueHolder;
import net.osmand.binary.OsmandOdb;
import net.osmand.binary.RouteDataObject;
import net.osmand.data.LatLon;
import net.osmand.plus.CurrentPositionHelper;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RouteCalculationResult;
import net.osmand.plus.routing.RouteProvider;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.database.RoutingLog;
import net.osmand.plus.stressreduction.database.SQLiteLogger;
import net.osmand.plus.stressreduction.database.SegmentInfo;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;
import net.osmand.plus.stressreduction.simulation.RoutingSimulation;
import net.osmand.plus.stressreduction.tools.Calculation;
import net.osmand.router.BinaryRoutePlanner;
import net.osmand.router.RouteSegmentResult;

import org.apache.commons.logging.Log;
import org.apache.http.conn.routing.RouteInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class handles the location.
 *
 * @author Tobias
 */
public class SRLocation implements OsmAndLocationProvider.OsmAndLocationListener,
		RoutingHelper.IRouteInformationListener {

	private static final Log log = PlatformUtil.getLog(SRLocation.class);

	private final OsmAndLocationProvider osmAndLocationProvider;
	private final RoutingHelper routingHelper;
	private final DataHandler dataHandler;
	private final FragmentHandler fragmentHandler;
	private final RoutingSimulation routingSimulation;
	private final CurrentPositionHelper currentPositionHelper;
	private Location currentLocation;
	private Location lastDialogLocation;
	private RoutingLog routingLog;
	private long lastLoggedSegmentID;
	private long lastDialogSegmentID;
	private long timerLocation;
	private long timerDialog;
	private boolean isDriving;
	private boolean logSegments;
	private final List<Float> segmentSpeedList = new ArrayList<>();
	public static int SIMULATION_SPEED = 1;

	/**
	 * Constructor
	 *
	 * @param osmandApplication The OsmandApplication
	 * @param dataHandler       The DataHandler
	 * @param fragmentHandler   The FragmentHandler
	 */
	public SRLocation(OsmandApplication osmandApplication, DataHandler dataHandler,
	                  FragmentHandler fragmentHandler) {
		this.dataHandler = dataHandler;
		this.fragmentHandler = fragmentHandler;
		osmAndLocationProvider = osmandApplication.getLocationProvider();
		routingHelper = osmandApplication.getRoutingHelper();
		routingSimulation = new RoutingSimulation(osmandApplication, fragmentHandler);
		currentPositionHelper = new CurrentPositionHelper(osmandApplication);
		isDriving = false;
		//		logSegments = false;
		logSegments = true;
		timerLocation = 0;
		timerDialog = 0;
	}

	/**
	 * Start listening for location and route updates
	 */
	public void startLocationListener() {
		osmAndLocationProvider.addLocationListener(this);
		routingHelper.addListener(this);
	}

	/**
	 * Stop listening for location and route updates
	 */
	public void stopLocationListener() {
		osmAndLocationProvider.removeLocationListener(this);
		routingHelper.removeListener(this);
	}

	/**
	 * Called every time a new location is received
	 *
	 * @param location The current location
	 */
	@Override
	public void updateLocation(Location location) {

		if (!logSegments) {
			return;
		}

		// check location because simulation sometimes throws null objects
		if (location == null) {
			log.error("updateLocation(): location is NULL");
			return;
		}

		currentLocation = location;
		int currentSpeed = Calculation.convertMsToKmh(location.getSpeed());

		if (!isDriving && !isSpeedBelowThreshold(location, Constants.MINIMUM_DRIVING_SPEED)) {
			isDriving = true;
		}

		segmentSpeedList.add(location.getSpeed());
		// Logging of current location every second
		if ((System.currentTimeMillis() - timerLocation > 1000) &&
				(location.getProvider().contains("gps"))) {
			timerLocation = System.currentTimeMillis();
			// try getting info's from route segment result for the current
			// route data object or the last known route segment
			RouteDataObject routeDataObject;
			RouteSegmentResult routeSegmentResult = routingHelper.getCurrentSegmentResult();
			if (routeSegmentResult != null) {
				log.debug("updateLocation(): looking for rdo from routingHelper...");
				routeDataObject = routeSegmentResult.getObject();
			} else {
				log.debug("updateLocation(): routingHelper did not return rdo, now looking for " +
						"rdo from currentPositionHelper...");
				routeDataObject = currentPositionHelper.getLastKnownRouteSegment(location);
			}

			if (routeDataObject != null) {
				log.debug("updateLocation(): found rdo!");
				// check if current segment is the same as last segment
				if (routeDataObject.getId() == lastLoggedSegmentID) {
					log.debug("updateLocation(): same id as last logged segment");
					return;
				}
				log.debug("updateLocation(): logging: UniqueID=" + StressReductionPlugin.getUUID
						() +
						", SegmentID=" + routeDataObject.getId() + ", Name=" +
						routeDataObject.getName() + ", Highway=" + routeDataObject.getHighway() +
						", Lanes=" + routeDataObject.getLanes() + ", maxSpeed=" + Math.round(
						routeDataObject.getMaximumSpeed(routeSegmentResult == null ||
								routeSegmentResult.isForwardDirection()) * Constants.MS_TO_KMH) +
						", Oneway=" + routeDataObject.getOneway() + ", Ref=" +
						routeDataObject.getRef() + ", Route=" + routeDataObject.getRoute() +
						", Restrictions=" + routeDataObject.getRestrictionLength() +
						", Current Speed=" + currentSpeed + ", LatLon=" + location.getLatitude() +
						"," + location.getLongitude());
				dataHandler.writeSegmentInfoToDatabase(new SegmentInfo(routeDataObject,
						Calculation.convertMsToKmh(Calculation.getAverageValue(segmentSpeedList))));
				segmentSpeedList.clear();
				lastLoggedSegmentID = routeDataObject.getId();
			}
		}

		// check if still driving and start speed watcher if not
		if (isSpeedBelowThreshold(location, Constants.DIALOG_SPEED_LIMIT) && isDriving &&
				!isDialogTimeout() && !isDialogDistanceTimeout() && !isDialogSegmentIDTimeout()) {
			log.debug("updateLocation(): speed below dialog speed limit, " +
					"starting SpeedWatcher thread...");
			isDriving = false;
			new Thread(new SpeedWatcher()).start();
		}
	}

	/**
	 * Check if a dialog has been displayed recently.
	 *
	 * @return Boolean whether there is a dialog timeout or not
	 */
	private boolean isDialogTimeout() {
		// dialog timeout set to 30s TODO check how big the timeout should be
		if (System.currentTimeMillis() - timerDialog <
				(Constants.DIALOG_TIMEOUT / SIMULATION_SPEED)) {
			log.debug("isDialogTimeout(): true");
			return true;
		}
		timerDialog = System.currentTimeMillis();
		return false;
	}

	/**
	 * Check if the distance covered since the last displayed dialog is more than 200 meters.
	 *
	 * @return Boolean whether there is a dialog distance timeout or not
	 */
	private boolean isDialogDistanceTimeout() {
		// dialog distance timeout set to 200m TODO check how big the distance timeout should be
		if ((currentLocation != null) && (lastDialogLocation != null)) {
			if (currentLocation.distanceTo(lastDialogLocation) <
					Constants.DIALOG_DISTANCE_TIMEOUT) {
				log.debug("isDialogDistanceTimeout(): true");
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the current segment id is different from the last segment id a dialog
	 * has been displayed.
	 *
	 * @return Boolean whether the last segment id equals the current segment id
	 */
	private boolean isDialogSegmentIDTimeout() {
		return (lastDialogSegmentID == lastLoggedSegmentID);
	}

	/**
	 * Check if the current speed is below a certain threshold.
	 *
	 * @return Boolean whether the current speed is below a certain threshold
	 */
	private boolean isSpeedBelowThreshold(Location location, int threshold) {
		if (location.hasSpeed()) {
			int speedLocation = Calculation.convertMsToKmh(location.getSpeed());
			return (speedLocation <= threshold);
		}
		log.debug("isSpeedBelowThreshold(): location has no speed information!");
		return true;
	}

	/**
	 * Called if a new route is calculated.
	 *
	 * @param newRoute  Boolean whether a new route is calculated or an old route is recalculated
	 * @param showToast Boolean whether a toast should be displayed
	 */
	@Override
	public void newRouteIsCalculated(boolean newRoute, ValueHolder<Boolean> showToast) {
		log.debug("newRouteIsCalculated(): new route=" + newRoute + ", (turning on logging)");
		//		logSegments = true;
		routingSimulation.newRouteIsCalculated();
		// write data to routing log
		if (newRoute) {
			int size = routingHelper.getRoute().getOriginalRoute().size();
			LatLon start = routingHelper.getRoute().getOriginalRoute().get(0).getStartPoint();
			LatLon end = routingHelper.getRoute().getOriginalRoute().get(size - 1).getEndPoint();
			String timeArrivalPreCalc = Calculation.getSpecificDateTime(
					(routingHelper.getLeftTime() * 1000) + System.currentTimeMillis());
			String timeRoutingStart = Calculation.getCurrentDateTime();
			routingLog = new RoutingLog(start, end, timeArrivalPreCalc, timeRoutingStart);
		}
	}

	// TODO routing log auch bei abbruch durch nutzer loggen, abbruch location, zeit, dist zum ende

	/**
	 * Called if the current active route is cancelled.
	 */
	@Override
	public void routeWasCancelled() {
		routingSimulation.routeWasCancelled();
		if (logSegments && SQLiteLogger
				.getDatabaseSizeSegmentsSinceLastStressValue(
						DataHandler.getTimestampLastStressValue()) >
				0) {
			log.debug("routeWasCancelled(): (turning off logging), left distance="+routingHelper
					.getLeftDistance()+", last latlon="+routingHelper.getLastProjection()
					.getLatitude()+","+routingHelper.getLastProjection().getLongitude());
			fragmentHandler.showSRDialog(dataHandler);
			//			logSegments = false;
		}
		if (routingLog != null) {
			routingLog.setTimeRoutingEnd(Calculation.getCurrentDateTime());
			if (routingLog.isLogComplete()) {
				dataHandler.writeRoutingLogToDatabase(routingLog);
				routingLog = null;
			}
		}
	}

	public void onDestinationReached() {
		if (routingLog != null) {
			routingLog.setTimeArrivalReal(Calculation.getCurrentDateTime());
			if (routingLog.isLogComplete()) {
				dataHandler.writeRoutingLogToDatabase(routingLog);
				routingLog = null;
			}
		}
	}

	/**
	 * This class is a speed watcher which gets activated if the speed is below a certain
	 * threshold.
	 * If the current speed is still below the threshold after 2 seconds the SRDialog is
	 * initialized.
	 *
	 * @author Tobias
	 */
	private class SpeedWatcher implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (SQLiteLogger.getDatabaseSizeSegmentsSinceLastStressValue(
					DataHandler.getTimestampLastStressValue()) > 0 &&
					isSpeedBelowThreshold(currentLocation, Constants.DIALOG_SPEED_LIMIT)) {
				log.debug("run(): speed still zero, show dialog");
				lastDialogLocation = currentLocation;
				lastDialogSegmentID = lastLoggedSegmentID;
				fragmentHandler.showSRDialog(dataHandler);
			} else {
				log.debug("run(): speed now higher than dialog speed limit, not showing dialog");
			}
		}

	}
}
