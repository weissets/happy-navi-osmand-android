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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * This class handles all interactions with fragments
 *
 * @author Tobias
 */
public class FragmentHandler {

	private static final Log log = PlatformUtil.getLog(FragmentHandler.class);

	private final OsmandApplication osmandApplication;
	private MapActivity mapActivity;
	private long simulationDialogTimeout;
	private long srDialogTimeout;

	public FragmentHandler(OsmandApplication osmandApplication) {
		this.osmandApplication = osmandApplication;
		simulationDialogTimeout = 0;
		srDialogTimeout = 0;
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

	public boolean isSRDialogVisible() {
		if (mapActivity != null) {
			FragmentSRDialog fragmentSRDialog =
					(FragmentSRDialog) mapActivity.getSupportFragmentManager()
							.findFragmentByTag(Constants.FRAGMENT_SR_DIALOG);
			return fragmentSRDialog != null && fragmentSRDialog.isVisible();
		}
		return false;
	}

	public void hideSRDialog() {
		if (mapActivity != null) {
			FragmentSRDialog fragmentSRDialog =
					(FragmentSRDialog) mapActivity.getSupportFragmentManager()
							.findFragmentByTag(Constants.FRAGMENT_SR_DIALOG);
			if (fragmentSRDialog != null) {
				fragmentSRDialog.dismiss();
			}
		}
	}

	public void showSRDialog(DataHandler dataHandler) {

		if (mapActivity != null && !isTimeout(srDialogTimeout) && SQLiteLogger.hasNewSegments()) {

			srDialogTimeout = System.currentTimeMillis();

			FragmentSRDialog fragmentSRDialog =
					(FragmentSRDialog) mapActivity.getSupportFragmentManager()
							.findFragmentByTag(Constants.FRAGMENT_SR_DIALOG);

			if (fragmentSRDialog == null) {
				boolean playSound = osmandApplication.getSettings().SR_NOTIFICATION_SOUND.get();
				fragmentSRDialog = FragmentSRDialog.newInstance(dataHandler, playSound);
				FragmentTransaction fragmentTransaction =
						mapActivity.getSupportFragmentManager().beginTransaction();
				fragmentTransaction.add(fragmentSRDialog, Constants.FRAGMENT_SR_DIALOG);
				fragmentTransaction.disallowAddToBackStack();
				fragmentTransaction.commit();
			} else {
				FragmentTransaction fragmentTransaction =
						mapActivity.getSupportFragmentManager().beginTransaction();
				fragmentTransaction.show(fragmentSRDialog);
				fragmentTransaction.disallowAddToBackStack();
				fragmentTransaction.commit();
			}
		}
	}

	public void showLocationSimulationDialog(RoutingSimulation routingSimulation) {
		if (mapActivity != null && !isTimeout(simulationDialogTimeout)) {

			simulationDialogTimeout = System.currentTimeMillis();

			FragmentLocationSimulationDialog fragmentLocationSimulationDialog =
					(FragmentLocationSimulationDialog) mapActivity.getSupportFragmentManager()
							.findFragmentByTag(Constants.FRAGMENT_LOCATION_SIMULATION);

			if (fragmentLocationSimulationDialog == null) {
				fragmentLocationSimulationDialog =
						FragmentLocationSimulationDialog.newInstance(routingSimulation);
				FragmentTransaction fragmentTransaction =
						mapActivity.getSupportFragmentManager().beginTransaction();
				fragmentTransaction.add(fragmentLocationSimulationDialog,
						Constants.FRAGMENT_LOCATION_SIMULATION);
				fragmentTransaction.disallowAddToBackStack();
				fragmentTransaction.commit();
			} else {
				FragmentTransaction fragmentTransaction =
						mapActivity.getSupportFragmentManager().beginTransaction();
				fragmentTransaction.show(fragmentLocationSimulationDialog);
				fragmentTransaction.disallowAddToBackStack();
				fragmentTransaction.commit();
			}
		}
	}

	private boolean isTimeout(long time) {
		return (System.currentTimeMillis() - time) < 2000;
	}

}
