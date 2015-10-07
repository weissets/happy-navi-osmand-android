package net.osmand.plus.stressreduction.fragments;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.database.SQLiteLogger;
import net.osmand.plus.stressreduction.simulation.RoutingSimulation;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;

import org.apache.commons.logging.Log;

/**
 * This class handles all interactions with fragments
 *
 * @author Tobias
 */
public class FragmentHandler {

	private static final Log log = PlatformUtil.getLog(FragmentHandler.class);

	private final OsmandApplication osmandApplication;
	private MapActivity mapActivity;

	public FragmentHandler(OsmandApplication osmandApplication) {
		this.osmandApplication = osmandApplication;
	}

	public void setMapActivity(MapActivity mapActivity) {
		this.mapActivity = mapActivity;
	}

	public void showStartDialogs() {
		if (SRSharedPreferences.getDisplayWhatsNewDialog(osmandApplication)) {
			showWhatsNewDialog();
		}
		if (SRSharedPreferences.getDisplayInfoDialog(osmandApplication)) {
			showInfoDialog();
		}
		if (SRSharedPreferences.getDisplayNewVersionDialog(osmandApplication)) {
			showNewVersionDialog();
		}
	}

	private void showInfoDialog() {
		if (mapActivity != null) {
			new FragmentInfoDialog()
					.show(mapActivity.getSupportFragmentManager(), Constants.FRAGMENT_INFO_DIALOG);
		} else {
			log.error("showInfoDialog(): mapActivity is NULL");
		}
	}

	private void showWhatsNewDialog() {
		if (mapActivity != null) {
			new FragmentWhatsNewDialog().show(mapActivity.getSupportFragmentManager(),
					Constants.FRAGMENT_WHATS_NEW_DIALOG);
		} else {
			log.error("showWhatsNewDialog(): mapActivity is NULL");
		}
	}

	private void showNewVersionDialog() {
		if (mapActivity != null) {
			new FragmentNewVersionDialog().show(mapActivity.getSupportFragmentManager(),
					Constants.FRAGMENT_NEW_VERSION_DIALOG);
		} else {
			log.error("showNewVersionDialog(): mapActivity is NULL");
		}
	}

	public void showSRDialog(DataHandler dataHandler) {
		if ((mapActivity != null) && (mapActivity.getSupportFragmentManager()
				.findFragmentByTag(Constants.FRAGMENT_SR_DIALOG) == null) && SQLiteLogger
				.getDatabaseSizeSegmentsSinceLastStressValue(
						DataHandler.getTimestampLastStressValue()) >
				0) {
			boolean playSound = osmandApplication.getSettings().SR_NOTIFICATION_SOUND.get();
			FragmentSRDialog fragmentSRDialog =
					FragmentSRDialog.newInstance(dataHandler, playSound);
			fragmentSRDialog
					.show(mapActivity.getSupportFragmentManager(), Constants.FRAGMENT_SR_DIALOG);
		} else {
			log.error("showSRDialog(): mapActivity is NULL or FragmentSRDialog is NOT NULL");
		}
	}

	public void showLocationSimulationDialog(RoutingSimulation routingSimulation) {
		if ((mapActivity != null) && (mapActivity.getSupportFragmentManager()
				.findFragmentByTag(Constants.FRAGMENT_LOCATION_SIMULATION) == null)) {
			FragmentLocationSimulationDialog fragmentLocationSimulationDialog =
					FragmentLocationSimulationDialog.newInstance(routingSimulation);
			fragmentLocationSimulationDialog.show(mapActivity.getSupportFragmentManager(),
					Constants.FRAGMENT_LOCATION_SIMULATION);
		} else {
			log.error("showLocationSimulationDialog(): mapActivity is NULL or " +
					"FragmentLocationSimulationDialog is NOT NULL");
		}
	}

}
