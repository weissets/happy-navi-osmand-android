package net.osmand.plus.stressreduction.tools;

import android.content.Context;
import android.content.SharedPreferences;

import net.osmand.plus.stressreduction.Constants;

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

	public static String[] getUserInfo(final Context context) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		String[] ret = new String[3];
		ret[0] = sharedPreferences.getString(Constants.USER_GENDER, "none");
		ret[1] = sharedPreferences.getString(Constants.USER_AGE, "0");
		ret[2] = sharedPreferences.getString(Constants.USER_CAR, "none");
		return ret;
	}

	public static void setUserInfo(final Context context, String gender, String age, String car) {
		SharedPreferences sharedPreferences =
				context.getSharedPreferences(Constants.PLUGIN_ID, Context.MODE_PRIVATE);
		sharedPreferences.edit().putString(Constants.USER_GENDER, gender).apply();
		sharedPreferences.edit().putString(Constants.USER_AGE, age).apply();
		sharedPreferences.edit().putString(Constants.USER_CAR, car).apply();
	}

}
