package net.osmand.plus.stressreduction.connectivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;

import org.apache.commons.logging.Log;

/**
 * This class is a receiver for wifi connections
 *
 * @author Tobias
 */
public class WifiReceiver extends BroadcastReceiver {

	private static final Log log = PlatformUtil.getLog(WifiReceiver.class);

	/**
	 * Enable the wifi receiver
	 *
	 * @param context the context
	 */
	public static void enableReceiver(Context context) {
		log.debug("enableReceiver(): enabling receiver...");
		ComponentName componentName = new ComponentName(context, WifiReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

	/**
	 * Disable the wifi receiver
	 *
	 * @param context the context
	 */
	public static void disableReceiver(Context context) {
		log.debug("disableReceiver(): disabling receiver...");
		ComponentName componentName = new ComponentName(context, WifiReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
	}

	/**
	 * Receiver which checks for connectivity changes
	 *
	 * @param context the context
	 * @param intent  the intent which started this receiver
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if ((!SRSharedPreferences.getReceiverTimeout(context)) &&
				intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			log.debug("onReceive(): WifiListener received connectivity change...");

			ConnectivityManager connectivityManager =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

			log.debug("onReceive(): Connectivity is " + ((networkInfo != null &&
					networkInfo.getType() == ConnectivityManager.TYPE_WIFI) ? "Wifi" : "not Wifi"));

			// check if intent contains a connectivity extra
			if (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				// check if there is a wifi connection
				if (networkInfo != null && networkInfo.getType() == ConnectivityManager
						.TYPE_WIFI) {
					log.debug("onReceive(): Wifi is available, starting upload...");
					disableReceiver(context);
					Intent uploadIntent = new Intent(context, UploadService.class);
					uploadIntent.putExtra(Constants.WIFI_RECEIVER_UPLOAD, true);
					WakefulIntentService.sendWakefulWork(context, uploadIntent);
				}
			}
		}
	}
}