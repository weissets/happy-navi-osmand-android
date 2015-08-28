package net.osmand.plus.stressreduction;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.stressreduction.connectivity.ConnectionHandler;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;
import net.osmand.plus.stressreduction.sensors.SensorHandler;
import net.osmand.plus.stressreduction.simulation.LocationSimulation;

import org.apache.commons.logging.Log;

/**
 * This class is the stress reduction plugin for OsmAnd.
 *
 * @author Tobias
 */
public class StressReductionPlugin extends OsmandPlugin {

	private static final Log log = PlatformUtil.getLog(StressReductionPlugin.class);

	public static final String ID = "net.osmand.plus.stressreduction.plugin";

	private static String UNIQUE_ID;

	private final OsmandApplication osmandApplication;
	private final FragmentHandler fragmentHandler;
	private final SensorHandler sensorHandler;
	private MapActivity mapActivity;
	private boolean firstRun;

	public StressReductionPlugin(OsmandApplication osmandApplication) {
		this.osmandApplication = osmandApplication;

		//		UNIQUE_ID = UUIDCreator.id(osmandApplication);
		UNIQUE_ID = "Tobi_Test_ID";

		DataHandler dataHandler = new DataHandler(osmandApplication);
		fragmentHandler = new FragmentHandler(osmandApplication);
		sensorHandler = new SensorHandler(osmandApplication, dataHandler, fragmentHandler);

		firstRun = true;

		// TODO remove ActivityLifecycleCallbacks (only for test purposes)
		osmandApplication.registerActivityLifecycleCallbacks(new CurrentActivityCallbacks());

		// enable the plugin by default
		StressReductionPlugin.enablePlugin(null, osmandApplication, this, true);
	}

	/**
	 * Initialize plugin runs just after creation
	 *
	 * @param osmandApplication The OsmandApplication
	 * @param activity          the currently running activity
	 */
	@Override
	public boolean init(OsmandApplication osmandApplication, Activity activity) {
		return true;
	}

	/**
	 * Getter for the anonymous unique id of the database
	 *
	 * @return unique id string
	 */
	public static String getUUID() {
		return UNIQUE_ID;
	}

	/**
	 * This class is called each time a activity changes its state
	 */
	private static class CurrentActivityCallbacks
			implements OsmandApplication.ActivityLifecycleCallbacks {

		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

		}

		@Override
		public void onActivityStarted(Activity activity) {

		}

		@Override
		public void onActivityResumed(Activity activity) {
			log.debug("onActivityResumed(): Current Activity is: " +
					activity.getComponentName().getClassName());
		}

		@Override
		public void onActivityPaused(Activity activity) {

		}

		@Override
		public void onActivityStopped(Activity activity) {

		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			log.debug("onActivityDestroyed(): Destroyed Activity is: " +
					activity.getComponentName().getClassName());
		}
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return osmandApplication.getString(R.string.sr_plugin_description);
	}

	@Override
	public String getName() {
		return osmandApplication.getString(R.string.sr_plugin_name);
	}

	@Override
	public int getLogoResourceId() {
		return R.drawable.sr_plugin_icon;
	}

	@Override
	public int getAssetResourceName() {
		return 0;
	}


	@Override
	public void mapActivityResume(MapActivity activity) {
		super.mapActivityResume(activity);

		// set mapActivity
		mapActivity = activity;
		fragmentHandler.setMapActivity(activity);

		// if first time, show dialogs
		if (firstRun) {
			firstRun = false;
			fragmentHandler.showStartDialogs();
		}

		// start sensors
		sensorHandler.startSensors();
	}

	@Override
	public void mapActivityPause(MapActivity activity) {
		super.mapActivityPause(activity);

		// set mapActivity not available
		mapActivity = null;
		fragmentHandler.setMapActivity(null);

		// start mapActivity watcher
		new Thread(new MapActivityWatcher()).start();
	}

	@Override
	public Class<? extends Activity> getSettingsActivity() {
		return StressReductionSettings.class;
	}

	private void initUpload() {
		if (osmandApplication.getSettings().SR_USE_WIFI_ONLY.get()) {
			ConnectionHandler.uploadData(osmandApplication, Constants.UPLOAD_MODE_WIFI);
		} else {
			ConnectionHandler.uploadData(osmandApplication, Constants.UPLOAD_MODE_MOBILE);
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
	private class MapActivityWatcher implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (mapActivity == null) {
				log.debug("run(): mapActivity NULL, stop sensors, init upload...");
				sensorHandler.stopSensors();
				initUpload();
			} else {
				log.debug("run(): mapActivity NOT NULL, changed orientation?");
			}
		}
	}

}