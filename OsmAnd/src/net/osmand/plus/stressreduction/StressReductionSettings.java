package net.osmand.plus.stressreduction;

import net.osmand.plus.BuildConfig;
import net.osmand.plus.R;
import net.osmand.plus.activities.SettingsBaseActivity;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;
import net.osmand.plus.stressreduction.voice.SRPocketSphinx;

import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
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

		CheckBoxPreference routingPreference = createCheckBoxPreference(settings.SR_ROUTING);
		routingPreference.setTitle(R.string.sr_settings_routing_title);
		routingPreference.setSummary(R.string.sr_settings_routing_description);
		preferenceScreen.addPreference(routingPreference);

		ListPreference srLevelPreference = createListPreference(settings.SR_LEVEL,
				getResources().getStringArray(R.array.sr_level_names), Constants.SR_LEVEL_VALUES);
		srLevelPreference.setTitle(R.string.sr_settings_sr_level_title);
		srLevelPreference.setSummary(R.string.sr_settings_sr_level_description);
		srLevelPreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						int value = Integer.valueOf(newValue.toString());
						settings.SR_LEVEL.set(value);
						return true;
					}
				});
		preferenceScreen.addPreference(srLevelPreference);

		CheckBoxPreference notificationSoundPreference =
				createCheckBoxPreference(settings.SR_NOTIFICATION_SOUND);
		notificationSoundPreference.setTitle(R.string.sr_settings_notification_sound_title);
		notificationSoundPreference.setSummary(R.string
				.sr_settings_notification_sound_description);
		preferenceScreen.addPreference(notificationSoundPreference);

		CheckBoxPreference speechInputPreference =
				createCheckBoxPreference(settings.SR_SPEECH_INPUT);
		speechInputPreference.setTitle(R.string.sr_settings_speech_input_title);
		speechInputPreference.setSummary(R.string.sr_settings_speech_input_description);
		preferenceScreen.addPreference(speechInputPreference);

		ListPreference speechValuePreference =
				createListPreference(settings.SR_SPEECH_VALUE, Constants.SPEECH_VALUES_E,
						Constants.SPEECH_VALUES);
		speechValuePreference.setTitle(R.string.sr_settings_speech_values_title);
		speechValuePreference.setSummary(R.string.sr_settings_speech_values_description);
		speechValuePreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						float value = Float.valueOf(newValue.toString());
						SRPocketSphinx.getInstance().init(settings.getContext(), value);
						return true;
					}
				});
		preferenceScreen.addPreference(speechValuePreference);

		CheckBoxPreference useWifiOnlyPreference =
				createCheckBoxPreference(settings.SR_USE_WIFI_ONLY);
		useWifiOnlyPreference.setTitle(R.string.sr_settings_use_wifi_only_title);
		useWifiOnlyPreference.setSummary(R.string.sr_settings_use_wifi_only_description);
		useWifiOnlyPreference
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						boolean state = (boolean) newValue;
						CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
						SRSharedPreferences.setOnlyWifiUpload(getMyApplication(), state);
						checkBoxPreference.setChecked(state);
						return state;
					}
				});
		preferenceScreen.addPreference(useWifiOnlyPreference);

		if (BuildConfig.DEBUG) {
			CheckBoxPreference locationSimulationPreference =
					createCheckBoxPreference(settings.SR_LOCATION_SIMULATION);
			locationSimulationPreference.setTitle(R.string.sr_settings_location_simulation_title);
			locationSimulationPreference
					.setSummary(R.string.sr_settings_location_simulation_description);
			preferenceScreen.addPreference(locationSimulationPreference);
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				locationSimulationPreference.setEnabled(false);
				locationSimulationPreference.setChecked(false);
			}
		}
	}

}
