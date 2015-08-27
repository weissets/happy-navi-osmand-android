package net.osmand.plus.stressreduction.fragments;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.database.DataHandler;
import net.osmand.plus.stressreduction.simulation.LocationSimulation;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;

import org.apache.commons.logging.Log;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

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
				.findFragmentByTag(Constants.FRAGMENT_SR_DIALOG) == null)) {
			playNotificationSound();
			FragmentSRDialog fragmentSRDialog = FragmentSRDialog.newInstance(dataHandler);
			fragmentSRDialog
					.show(mapActivity.getSupportFragmentManager(), Constants.FRAGMENT_SR_DIALOG);
		} else {
			log.error("showSRDialog(): mapActivity is NULL or FragmentSRDialog is NOT NULL");
		}
	}

	public void showLocationSimulationDialog(LocationSimulation locationSimulation) {
		if ((mapActivity != null) && (mapActivity.getSupportFragmentManager()
				.findFragmentByTag(Constants.FRAGMENT_LOCATION_SIMULATION) == null)) {
			FragmentLocationSimulationDialog fragmentLocationSimulationDialog =
					FragmentLocationSimulationDialog.newInstance(locationSimulation);
			fragmentLocationSimulationDialog.show(mapActivity.getSupportFragmentManager(),
					Constants.FRAGMENT_LOCATION_SIMULATION);
		} else {
			log.error("showLocationSimulationDialog(): mapActivity is NULL or " +
					"FragmentLocationSimulationDialog is NOT NULL");
		}
	}

	private void playNotificationSound() {
		if (osmandApplication.getSettings().SR_NOTIFICATION_SOUND.get()) {
			try {
				AssetFileDescriptor afd =
						osmandApplication.getAssets().openFd("sounds/desk_bell.mp3");
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
						afd.getLength());
				mediaPlayer.prepare();
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.start();
			} catch (IllegalArgumentException | IllegalStateException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
