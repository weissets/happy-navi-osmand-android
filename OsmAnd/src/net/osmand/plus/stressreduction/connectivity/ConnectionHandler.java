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

	public static void uploadData(final OsmandApplication osmandApplication, String mode) {
		switch (mode) {
			case Constants.UPLOAD_MODE_WIFI:
				log.debug("uploadData(): try to upload data via Wifi...");
				if (osmandApplication.getSettings().isWifiConnected()) {
					log.debug("uploadData(): Wifi is available, starting upload...");
					initUpload(osmandApplication);
				} else {
					log.debug("uploadData(): Wifi is not available, enabling wifi receiver...");
					WifiReceiver.enableReceiver(osmandApplication);
				}
				break;
			//			case Constants.UPLOAD_MODE_INCORRECT_CLOSE:
			//				Log.d(StressReductionPlugin.TAG, "StressReductionPlugin.java: Data was
			// not correct uploaded, looking 20s for wifi...");
			//				final Thread thread = new Thread(new Runnable() {
			//
			//					@Override
			//					public void run() {
			//						long timer = System.currentTimeMillis();
			//						while (System.currentTimeMillis() - timer < 20000) {
			//							if (osmandApplication.getSettings().isWifiConnected()) {
			//								initUpload(osmandApplication);
			//								break;
			//							} else {
			//								try {
			//									Thread.sleep(2000);
			//								} catch (InterruptedException e) {
			//									e.printStackTrace();
			//								}
			//							}
			//						}
			//					}
			//				});
			//				thread.start();
			//				break;
			case Constants.UPLOAD_MODE_MOBILE:
				log.debug("uploadData(): starting upload via mobile connection...");
				initUpload(osmandApplication);
				break;
		}
	}

	private static void initUpload(OsmandApplication osmandApplication) {
		Intent uploadIntent = new Intent(osmandApplication, UploadService.class);
		WakefulIntentService.sendWakefulWork(osmandApplication, uploadIntent);
	}

	public static void downloadSRData(final OsmandApplication osmandApplication) {
		Intent downloadIntent = new Intent(osmandApplication, DownloadService.class);
		WakefulIntentService.sendWakefulWork(osmandApplication, downloadIntent);
	}

}
