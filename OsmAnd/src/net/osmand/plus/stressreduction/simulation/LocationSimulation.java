package net.osmand.plus.stressreduction.simulation;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

import net.osmand.Location;
import net.osmand.PlatformUtil;
import net.osmand.ValueHolder;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;
import net.osmand.plus.stressreduction.fragments.FragmentLocationSimulationDialog;

import org.apache.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is a test class for simulating locations
 *
 * @author Tobias
 */
public class LocationSimulation
		implements FragmentLocationSimulationDialog.StartSimulationListener {

	private static final Log log = PlatformUtil.getLog(LocationSimulation.class);

	private final OsmandApplication osmandApplication;
	private final RoutingHelper routingHelper;
	private SimulationThread simulationThread;
	private final FragmentHandler fragmentHandler;

	private final List<Location> locationList = new ArrayList<>();

	private float LOCATION_SPEED;

	public LocationSimulation(OsmandApplication osmandApplication,
	                          FragmentHandler fragmentHandler) {
		this.osmandApplication = osmandApplication;
		this.fragmentHandler = fragmentHandler;
		routingHelper = osmandApplication.getRoutingHelper();
	}

	public void newRouteIsCalculated(boolean newRoute) {
		log.debug("newRouteIsCalculated(): newRoute=" + newRoute);
		if (osmandApplication.getSettings().SR_LOCATION_SIMULATION.get()) {
			// if develop mode then show dialog if route should be simulated
			if (simulationThread == null) {
				locationList.clear();
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
		}
	}

	@Override
	public void startSimulation(int speed) {
		if (simulationThread == null) {
			LOCATION_SPEED = speed;
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
			Random random = new Random();
			long timer = System.currentTimeMillis();

			while (thisThread == simulationThread && locationList.size() > 1) {
				log.debug("run(): locationList.size():" + locationList.size());
				if (locationList.size() > 1) {
					interpolatedLocationList =
							interpolateLocations(locationList.get(0), locationList.get(1));
					log.debug("run(): interpolatedLocationList.size():" +
							interpolatedLocationList.size());
				}
				float currentBearing = locationList.get(0).bearingTo(locationList.get(1));
				boolean firstRun = true;
				float speedVar = (random.nextFloat() - 0.5f) * 10f;
				for (Location loc : interpolatedLocationList) {
					// simulate points
					android.location.Location loc2 = new android.location.Location("gps");
					loc2.setTime(System.currentTimeMillis());
					loc2.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
					loc2.setLongitude(loc.getLongitude());
					loc2.setLatitude(loc.getLatitude());
					loc2.setAccuracy(5.0f);
					loc2.setBearing(currentBearing);
					loc2.setSpeed((LOCATION_SPEED + speedVar) / 3.6f);

					if (firstRun && System.currentTimeMillis() - timer > 30000) {
						// simulate car stopped for 3 seconds
						loc2.setSpeed(0.0f);
						for (int i = 0; i < 30; i++) {
							locationManager.setTestProviderLocation("gps", loc2);
							try {
								Thread.sleep(100);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						firstRun = false;
						timer = System.currentTimeMillis();
					} else {
						locationManager.setTestProviderLocation("gps", loc2);
						try {
							Thread.sleep(Math.round(1000 / (LOCATION_SPEED / 3.6f)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				locationList.remove(0);
			}
			locationManager.setTestProviderEnabled("gps", false);
			locationManager.removeTestProvider("gps");
			simulationThread = null;
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
			return interpolatedLocations;
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
