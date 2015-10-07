package net.osmand.plus.stressreduction.tools;

import android.content.Context;
import android.content.SharedPreferences;

import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;

/**
 * This class handles the shared preferences
 *
 * @author Tobias
 */
public class SRSharedPreferences {

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

	public static boolean getDataUploadedCorrectly(final Context context) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		boolean dataUploadedCorrectly =
				sharedPreferences.getBoolean(Constants.DATA_UPLOADED_CORRECTLY, true);
		sharedPreferences.edit().putBoolean(Constants.DATA_UPLOADED_CORRECTLY, false).apply();
		return dataUploadedCorrectly;
	}

	public static void setDataUploadedCorrectly(final Context context, boolean uploadedCorrectly) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(Constants.DATA_UPLOADED_CORRECTLY, uploadedCorrectly)
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

}
