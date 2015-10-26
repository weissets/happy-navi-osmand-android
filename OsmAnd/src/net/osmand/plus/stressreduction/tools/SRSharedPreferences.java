package net.osmand.plus.stressreduction.tools;

import android.content.Context;
import android.content.SharedPreferences;

import net.osmand.PlatformUtil;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;

import org.apache.commons.logging.Log;

/**
 * This class handles the shared preferences
 *
 * @author Tobias
 */
public class SRSharedPreferences {

	private static final Log log = PlatformUtil.getLog(SRSharedPreferences.class);

	public static boolean getDisplayInfoDialog(final Context context) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		if (!sharedPreferences.getBoolean(Constants.INFO_VERSION, false)) {
			sharedPreferences.edit().putBoolean(Constants.INFO_VERSION, true).apply();
			return true;
		}
		return false;
	}

	public static boolean getDisplayWhatsNewDialog(final Context context) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		if (!sharedPreferences.getBoolean(Constants.WHATS_NEW_VERSION, false)) {
			sharedPreferences.edit().putBoolean(Constants.WHATS_NEW_VERSION, true).apply();
			return true;
		}
		return false;
	}

	public static boolean getDisplayNewVersionDialog(final Context context) {
		return false; // TODO check for new version (Play Store?)
		//		SharedPreferences sharedPreferences = context.getSharedPreferences
		// (StressReductionPlugin.ID, Context.MODE_PRIVATE);
		//        return sharedPreferences.getBoolean(Constants.FRAGMENT_NEW_VERSION_DIALOG, true);
	}

	public static void setDisplayNewVersionDialog(final Context context, boolean display) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(Constants.FRAGMENT_NEW_VERSION_DIALOG, display)
				.apply();
	}

	public static boolean getReceiverTimeout(final Context context) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		long lastWifiReceive = sharedPreferences.getLong(Constants.LAST_WIFI_RECEIVE, 0);
		boolean timeout =
				(System.currentTimeMillis() - lastWifiReceive) < Constants.WIFI_RECEIVER_TIMEOUT;
		if (timeout) {
			return true;
		}
		sharedPreferences.edit().putLong(Constants.LAST_WIFI_RECEIVE, System.currentTimeMillis())
				.apply();
		return false;
	}

	public static boolean getOnlyWifiUpload(final Context context) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(Constants.UPLOAD_MODE_WIFI, true);
	}

	public static void setOnlyWifiUpload(final Context context, boolean onlyWifi) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(Constants.UPLOAD_MODE_WIFI, onlyWifi).apply();
	}

}
