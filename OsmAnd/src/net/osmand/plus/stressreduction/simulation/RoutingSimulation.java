package net.osmand.plus.stressreduction.simulation;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;
import net.osmand.plus.stressreduction.fragments.FragmentLocationSimulationDialog;
import net.osmand.plus.stressreduction.sensors.SRLocation;
import net.osmand.plus.stressreduction.tools.Calculation;

import org.apache.commons.logging.Log;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * This class simulates driving with the car with routing
 *
 * @author Tobias
 */
public class RoutingSimulation
		implements FragmentLocationSimulationDialog.StartSimulationListener {

	private static final Log log = PlatformUtil.getLog(RoutingSimulation.class);

	private final OsmandApplication osmandApplication;
	private final RoutingHelper routingHelper;
	private SimulationThread simulationThread;
	private final FragmentHandler fragmentHandler;

	private final List<Location> locationList = new ArrayList<>();
	private int SIMULATION_SPEED;

	public RoutingSimulation(OsmandApplication osmandApplication, FragmentHandler
			fragmentHandler) {
		this.osmandApplication = osmandApplication;
		this.fragmentHandler = fragmentHandler;
		routingHelper = osmandApplication.getRoutingHelper();
	}

	public void newRouteIsCalculated() {
		if (osmandApplication.getSettings().SR_LOCATION_SIMULATION.get()) {
			// if develop mode then show dialog if route should be simulated
			if (simulationThread == null) {
				locationList.addAll(routingHelper.getCurrentCalculatedRoute());
				fragmentHandler.showLocationSimulationDialog(this);
			} else {
				log.error("newRouteIsCalculated(): simulationThread is NOT NULL");
			}
		}
	}

	public void routeWasCancelled() {
		if (simulationThread != null) {
			simulationThread.interrupt();
			simulationThread = null;
			locationList.clear();
		}
	}

	@Override
	public void startSimulation(int speed, boolean routing) {
		if (simulationThread == null) {
			SRLocation.SIMULATION_SPEED = speed;
			if (!routing) {
				new LocationSimulation(osmandApplication, locationList, speed);
				routingHelper.clearCurrentRoute(null, null);
				return;
			}
			SIMULATION_SPEED = speed;
			simulationThread = new SimulationThread();
			simulationThread.start();
		} else {
			osmandApplication.showToastMessage("Simulation already in use!");
		}
	}

	private class SimulationThread extends Thread {
		/**
		 * Calls the <code>run()</code> method of the Runnable object the receiver
		 * holds. If no Runnable is set, does nothing.
		 *
		 * @see Thread#start
		 */
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		@Override
		public void run() {
			Thread thisThread = Thread.currentThread();
			LocationManager locationManager =
					(LocationManager) osmandApplication.getSystemService(Context.LOCATION_SERVICE);
			locationManager
					.addTestProvider("gps", false, false, false, false, false, true, true, 1, 1);
			locationManager.setTestProviderEnabled("gps", true);

			List<Location> interpolatedLocationList = new ArrayList<>();
			long timer = System.currentTimeMillis();
			float lastSegmentSpeed = Calculation.convertKmhToMs(50f);

			while (thisThread == simulationThread && locationList.size() > 1) {
				log.debug("run(): locationList.size():" + locationList.size());
				if (locationList.size() > 1) {
					interpolatedLocationList =
							interpolateLocations(locationList.get(0), locationList.get(1));
					log.debug("run(): interpolatedLocationList.size():" +
							interpolatedLocationList.size());
				}
				float currentBearing = locationList.get(0).bearingTo(locationList.get(1));
				locationList.remove(0);
				float LOCATION_SPEED;
				if (routingHelper.getCurrentMaxSpeed() > 0) {
					// this is speed in m/s
					LOCATION_SPEED = routingHelper.getCurrentMaxSpeed();
					lastSegmentSpeed = LOCATION_SPEED;
				} else {
					LOCATION_SPEED = lastSegmentSpeed;
				}
				for (Location loc : interpolatedLocationList) {
					// simulate points
					android.location.Location loc2 = new android.location.Location("gps");
					loc2.setTime(System.currentTimeMillis());
					loc2.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
					loc2.setLongitude(loc.getLongitude());
					loc2.setLatitude(loc.getLatitude());
					loc2.setAccuracy(5.0f);
					loc2.setBearing(currentBearing);
					loc2.setSpeed(LOCATION_SPEED);

					locationManager.setTestProviderLocation("gps", loc2);
					try {
						Thread.sleep(Math.round(1000 / (LOCATION_SPEED * SIMULATION_SPEED)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (System.currentTimeMillis() - timer > Math.round(30000/SIMULATION_SPEED)) {
					android.location.Location locWait = new android.location.Location("gps");
					locWait.setLongitude(
							interpolatedLocationList.get(interpolatedLocationList.size() - 1)
									.getLongitude());
					locWait.setLatitude(
							interpolatedLocationList.get(interpolatedLocationList.size() - 1)
									.getLatitude());
					locWait.setAccuracy(5.0f);
					locWait.setSpeed(0f);
					for (int i = 0; i < 30; i++) {
						locWait.setTime(System.currentTimeMillis());
						locWait.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
						locationManager.setTestProviderLocation("gps", locWait);
						try {
							Thread.sleep(100);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					timer = System.currentTimeMillis();
				}
			}
			locationManager.setTestProviderEnabled("gps", false);
			locationManager.removeTestProvider("gps");
//			simulationThread = null;
			routeWasCancelled();
			SRLocation.SIMULATION_SPEED = 1;
		}

		List<Location> interpolateLocations(Location loc1, Location loc2) {
			double diffLat = loc2.getLatitude() - loc1.getLatitude();
			double diffLon = loc2.getLongitude() - loc1.getLongitude();
			boolean finishedIfLess;
			boolean useLat = Math.abs(diffLat) > Math.abs(diffLon);
			if (useLat) {
				finishedIfLess = (diffLat < 0);
			} else {
				finishedIfLess = (diffLon < 0);
			}
			float[] distance = new float[1];
			Location.distanceBetween(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(),
					loc2.getLongitude(), distance);
			int dist = Math.round(distance[0]);
			double stepLat = diffLat / dist;
			double stepLon = diffLon / dist;
			List<Location> interpolatedLocations = new ArrayList<>();
			Location firstLocation = new Location(loc1);
			interpolatedLocations.add(firstLocation);
			while (!isFinished(interpolatedLocations.get(interpolatedLocations.size() - 1), loc2,
					finishedIfLess, useLat)) {
				Location currentLocation =
						new Location(interpolatedLocations.get(interpolatedLocations.size() - 1));
				currentLocation.setLatitude(currentLocation.getLatitude() + stepLat);
				currentLocation.setLongitude(currentLocation.getLongitude() + stepLon);
				interpolatedLocations.add(currentLocation);
			}
			return interpolatedLocations.subList(0, interpolatedLocations.size()-2);
		}

		boolean isFinished(Location currentLoc, Location finalLoc, boolean finishedIfLess,
		                   boolean useLat) {
			if (useLat) {
				return finishedIfLess && currentLoc.getLatitude() < finalLoc.getLatitude() ||
						!finishedIfLess && currentLoc.getLatitude() > finalLoc.getLatitude();
			} else {
				return finishedIfLess && currentLoc.getLongitude() < finalLoc.getLongitude() ||
						!finishedIfLess && currentLoc.getLongitude() > finalLoc.getLongitude();
			}
		}
	}
}
