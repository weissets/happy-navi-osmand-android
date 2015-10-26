package net.osmand.plus.stressreduction.connectivity;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;

import org.apache.commons.logging.Log;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class is a receiver for wifi connections
 *
 * @author Tobias
 */
public class ConnectionReceiver extends BroadcastReceiver {

	private static final Log log = PlatformUtil.getLog(ConnectionReceiver.class);

	/**
	 * Check if the receiver is enabled
	 *
	 * @param context The context
	 * @return Boolean if the receiver is enabled or not
	 */
	public static boolean isReceiverEnabled(Context context) {
		ComponentName componentName = new ComponentName(context, ConnectionReceiver.class);
		return context.getPackageManager().getComponentEnabledSetting(componentName) ==
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
	}

	/**
	 * Enable the wifi receiver
	 *
	 * @param context The context
	 */
	public static void enableReceiver(Context context) {
		log.debug("enableReceiver(): enabling mobile receiver...");
		ComponentName componentName = new ComponentName(context, ConnectionReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

	/**
	 * Disable the wifi receiver
	 *
	 * @param context The context
	 */
	public static void disableReceiver(Context context) {
		log.debug("disableReceiver(): disabling mobile receiver...");
		ComponentName componentName = new ComponentName(context, ConnectionReceiver.class);
		context.getPackageManager().setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}

	/**
	 * Receiver which checks for connectivity changes
	 *
	 * @param context The context
	 * @param intent  The intent which started this receiver
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if ((!SRSharedPreferences.getReceiverTimeout(context)) &&
				intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			log.debug("onReceive(): MobileReceiver received connectivity change...");

			ConnectivityManager connectivityManager =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			int type = networkInfo.getType();
			log.debug("onReceive(): Connectivity is " +
					((type == ConnectivityManager.TYPE_WIFI) ? "Wifi" :
							(type == ConnectivityManager.TYPE_MOBILE ? "mobile" : "other")));

			// check if intent contains a connectivity extra
			if (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				Intent uploadIntent = new Intent(context, UploadService.class);
				uploadIntent.putExtra(Constants.RECEIVER_UPLOAD, true);
				// if only wifi should be used
				if (SRSharedPreferences.getOnlyWifiUpload(context)) {
					if (type == ConnectivityManager.TYPE_WIFI) {
						log.debug("onReceive(): wifi is available, starting upload...");
						disableReceiver(context);
						WakefulIntentService.sendWakefulWork(context, uploadIntent);
					}
				} else {
					log.debug("onReceive(): wifi or mobile connection is available, starting " +
							"upload...");
					disableReceiver(context);
					WakefulIntentService.sendWakefulWork(context, uploadIntent);
				}
			}
		}
	}
}