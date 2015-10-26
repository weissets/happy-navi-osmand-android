package net.osmand.plus.stressreduction.connectivity;

import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.stressreduction.Constants;

import org.apache.commons.logging.Log;

/**
 * This class handles connections to the server
 *
 * @author Tobias
 */
public class ConnectionHandler {

	private static final Log log = PlatformUtil.getLog(ConnectionHandler.class);

	/**
	 * Upload data via wifi or mobile connection
	 *
	 * @param osmandApplication The OsmandApplication
	 */
	public static void uploadData(final OsmandApplication osmandApplication) {
		if (osmandApplication.getSettings().SR_USE_WIFI_ONLY.get()) {
			log.debug("uploadData(): try to upload data via Wifi...");
			if (osmandApplication.getSettings().isWifiConnected()) {
				log.debug("uploadData(): Wifi is available, starting upload...");
				initUpload(osmandApplication);
			} else {
				log.debug("uploadData(): Wifi is not available, enabling connection receiver...");
				ConnectionReceiver.enableReceiver(osmandApplication);
			}
		} else {
			log.debug("uploadData(): try to upload data via mobile connection...");
			if (osmandApplication.getSettings().isInternetConnectionAvailable(true)) {
				log.debug("uploadData(): mobile connection available, starting upload...");
				initUpload(osmandApplication);
			} else {
				log.debug("uploadData(): mobile connection is not available, enabling " +
						"connection receiver...");
				ConnectionReceiver.enableReceiver(osmandApplication);
			}
		}
	}

	/**
	 * Initialize the upload via the wakeful intent service
	 *
	 * @param osmandApplication The OsmandApplication
	 */
	private static void initUpload(final OsmandApplication osmandApplication) {
		Intent uploadIntent = new Intent(osmandApplication, UploadService.class);
		WakefulIntentService.sendWakefulWork(osmandApplication, uploadIntent);
	}

	/**
	 * Download data via the wakeful intent service
	 *
	 * @param osmandApplication The OsmandApplication
	 */
	public static void downloadSRData(final OsmandApplication osmandApplication) {
		Intent downloadIntent = new Intent(osmandApplication, DownloadService.class);
		WakefulIntentService.sendWakefulWork(osmandApplication, downloadIntent);
	}

}
