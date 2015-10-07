package net.osmand.plus.stressreduction;

import android.app.Activity;
import android.os.Bundle;

import net.osmand.PlatformUtil;
import net.osmand.plus.BuildConfig;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.stressreduction.connectivity.ConnectionHandler;
import net.osmand.plus.stressreduction.connectivity.WifiReceiver;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.database.SQLiteLogger;
import net.osmand.plus.stressreduction.fragments.FragmentHandler;
import net.osmand.plus.stressreduction.sensors.SensorHandler;
import net.osmand.plus.stressreduction.tools.Calculation;
import net.osmand.plus.stressreduction.tools.UUIDCreator;

import org.apache.commons.logging.Log;

/**
 * This class is the stress reduction plugin for OsmAnd.
 *
 * @author Tobias
 */
public class StressReductionPlugin extends OsmandPlugin {

	private static final Log log = PlatformUtil.getLog(StressReductionPlugin.class);

	private static String UNIQUE_ID;

	private final OsmandApplication osmandApplication;
	private final FragmentHandler fragmentHandler;
	private final SensorHandler sensorHandler;
	private MapActivity mapActivity;
	private boolean firstRun;

	public StressReductionPlugin(OsmandApplication osmandApplication) {
		this.osmandApplication = osmandApplication;

		UNIQUE_ID = UUIDCreator.id(osmandApplication);

		DataHandler dataHandler = new DataHandler(osmandApplication);
		fragmentHandler = new FragmentHandler(osmandApplication);
		sensorHandler = new SensorHandler(osmandApplication, dataHandler, fragmentHandler);

		firstRun = true;

		// for debugging
		if (BuildConfig.DEBUG) {
			UNIQUE_ID = "Tobi_Test_ID";
			osmandApplication.registerActivityLifecycleCallbacks(new CurrentActivityCallbacks());
		}

		// enable the plugin by default
		StressReductionPlugin.enablePlugin(null, osmandApplication, this, true);

		// write uuid to database
		dataHandler.writeUserToDatabase();
		dataHandler.writeAppLogToDatabase(Calculation.getCurrentDateTime());

		// download stress reduction database
		ConnectionHandler.downloadSRData(osmandApplication);
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
		return Constants.PLUGIN_ID;
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
	public boolean destinationReached() {
		sensorHandler.onDestinationReached();
		return true;
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

		// disable wifi receiver
		WifiReceiver.disableReceiver(osmandApplication);

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
	 * This class watches the map activity and triggers the data upload if map activity was closed.
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