package net.osmand.plus.stressreduction.sensors;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.ValueHolder;
import net.osmand.binary.RouteDataObject;
import net.osmand.plus.ApplicationMode;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.database.SQLiteLogger;
import net.osmand.plus.stressreduction.database.SegmentInfo;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;
import net.osmand.plus.stressreduction.simulation.LocationSimulation;
import net.osmand.plus.stressreduction.tools.Calculation;
import net.osmand.router.RouteSegmentResult;
import net.osmand.router.VehicleRouter;

import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the location.
 *
 * @author Tobias
 */
class SRLocation implements OsmAndLocationProvider.OsmAndLocationListener,
		RoutingHelper.IRouteInformationListener {

	private static final Log log = PlatformUtil.getLog(SRLocation.class);

	private final OsmandApplication osmandApplication;
	private final OsmAndLocationProvider osmAndLocationProvider;
	private final RoutingHelper routingHelper;
	private final DataHandler dataHandler;
	private final FragmentHandler fragmentHandler;
	private final LocationSimulation locationSimulation;
	private Location currentLocation;
	private Location lastDialogLocation;
	private long lastLoggedSegmentID;
	private long lastDialogSegmentID;
	private long timerLocation;
	private long timerDialog;
	private boolean isDriving;
	private boolean logSegments;
	private final List<Float> segmentSpeedList = new ArrayList<>();

	/**
	 * Constructor
	 *
	 * @param osmandApplication The OsmandApplication
	 * @param dataHandler       The DataHandler
	 * @param fragmentHandler   The FragmentHandler
	 */
	public SRLocation(OsmandApplication osmandApplication, DataHandler dataHandler,
	                  FragmentHandler fragmentHandler) {
		this.osmandApplication = osmandApplication;
		this.dataHandler = dataHandler;
		this.fragmentHandler = fragmentHandler;
		osmAndLocationProvider = osmandApplication.getLocationProvider();
		routingHelper = osmandApplication.getRoutingHelper();
		locationSimulation = new LocationSimulation(osmandApplication, fragmentHandler);
		isDriving = false;
		logSegments = false;
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
				routeDataObject = routeSegmentResult.getObject();
			} else {
				routeDataObject = osmAndLocationProvider.getLastKnownRouteSegment();
			}

			if (routeDataObject != null) {
				// check if current segment is the same as last segment
				if (routeDataObject.getId() == lastLoggedSegmentID) {
					log.debug("updateLocation(): same id as last logged segment");
					return;
				}
				log.debug("updateLocation(): logging: UniqueID=" + StressReductionPlugin.getUUID
						() +
						", SegmentID=" + routeDataObject.getId() + ", Name=" +
						routeDataObject.getName() + ", Highway=" + routeDataObject.getHighway() +
						", Lanes=" + routeDataObject.getLanes() + ", maxSpeed=" +
						Math.round(routeDataObject.getMaximumSpeed() * Constants.MS_TO_KMH) +
						", Oneway=" + routeDataObject.getOneway() + ", Ref=" +
						routeDataObject.getRef() + ", Route=" + routeDataObject.getRoute() +
						", Restrictions=" + routeDataObject.getRestrictionLength() +
						", Current Speed=" + currentSpeed + ", LatLon=" + location.getLatitude() +
						"," + location.getLongitude());
				dataHandler.writeSegmentInfoToDatabase(new SegmentInfo(routeDataObject,
						Calculation.convertMsToKmh(Calculation.getAverageValue(segmentSpeedList)
						)));
				segmentSpeedList.clear();
				lastLoggedSegmentID = routeDataObject.getId();
			} else {
				log.debug("updateLocation(): RouteDataObject is NULL");
				// TODO obtain info for current segment (note: segment not available without
				// calculated route)
			}
		}

		// check if still driving and start speed watcher if not
		if (isSpeedBelowThreshold(location, Constants.DIALOG_SPEED_LIMIT) && isDriving &&
				!isDialogTimeout() && !isDialogDistanceTimeout() && !isDialogSegmentIDTimeout()) {
			log.debug("updateLocation(): speed below dialog speed limit, " +
					"starting SpeedWatcher thread...");
			isDriving = false;
			new Thread(new SpeedWatcher()).start();//new SpeedWatcher().run();
		}
	}

	/**
	 * Check if a dialog has been displayed recently.
	 *
	 * @return Boolean whether there is a dialog timeout or not
	 */
	private boolean isDialogTimeout() {
		// dialog timeout set to 30s TODO check how big the timeout should be
		if (System.currentTimeMillis() - timerDialog < 30000) {
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
			if (currentLocation.distanceTo(lastDialogLocation) < 200) {
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
		log.debug("newRouteIsCalculated(): new route=" + newRoute + ", turning on logging");
		logSegments = true;
		locationSimulation.newRouteIsCalculated(newRoute);

	}

	/**
	 * Called if the current active route is cancelled.
	 */
	@Override
	public void routeWasCancelled() {
		locationSimulation.routeWasCancelled();
		if (logSegments && SQLiteLogger
				.getDatabaseSizeSinceLastStressValue(dataHandler.getTimestampLastStressValue()) >
				0) {
			log.debug("routeWasCancelled(): turning off logging");
			fragmentHandler.showSRDialog(dataHandler);
			logSegments = false;
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
			if (SQLiteLogger.getDatabaseSizeSinceLastStressValue(
					dataHandler.getTimestampLastStressValue()) > 0 &&
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
