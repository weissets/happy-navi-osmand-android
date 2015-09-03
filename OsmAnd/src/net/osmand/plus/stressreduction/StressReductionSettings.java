package net.osmand.plus.stressreduction;

import net.osmand.plus.BuildConfig;
import net.osmand.plus.activities.SettingsBaseActivity;

import net.osmand.plus.R;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceScreen;

/**
 * This class enables the stress reduction plugin settings in the menu
 *
 * @author Tobias
 */
public class StressReductionSettings extends SettingsBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getToolbar().setTitle(R.string.sr_settings_title);
		PreferenceScreen preferenceScreen = getPreferenceScreen();

		CheckBoxPreference notificationSoundPreference =
				createCheckBoxPreference(settings.SR_NOTIFICATION_SOUND);
		notificationSoundPreference.setTitle(R.string.sr_settings_notification_sound_title);
		notificationSoundPreference.setSummary(R.string
				.sr_settings_notification_sound_description);
		preferenceScreen.addPreference(notificationSoundPreference);

		CheckBoxPreference useWifiOnlyPreference =
				createCheckBoxPreference(settings.SR_USE_WIFI_ONLY);
		useWifiOnlyPreference.setTitle(R.string.sr_settings_use_wifi_only_title);
		useWifiOnlyPreference.setSummary(R.string.sr_settings_use_wifi_only_description);
		preferenceScreen.addPreference(useWifiOnlyPreference);

		if (BuildConfig.DEBUG) {
			CheckBoxPreference locationSimulationPreference = createCheckBoxPreference(settings.SR_LOCATION_SIMULATION);
			locationSimulationPreference.setTitle(R.string.sr_settings_location_simulation_title);
			locationSimulationPreference
					.setSummary(R.string.sr_settings_location_simulation_description);
			preferenceScreen.addPreference(locationSimulationPreference);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				locationSimulationPreference.setEnabled(false);
				locationSimulationPreference.setChecked(false);
			}
		}

		CheckBoxPreference routingPreference = createCheckBoxPreference(settings.SR_ROUTING);
		routingPreference.setTitle(R.string.sr_settings_routing_title);
		routingPreference.setSummary(R.string.sr_settings_routing_description);
		routingPreference.setEnabled(false);
		routingPreference.setChecked(false);
		preferenceScreen.addPreference(routingPreference);
	}

}
