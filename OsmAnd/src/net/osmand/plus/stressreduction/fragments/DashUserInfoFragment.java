package net.osmand.plus.stressreduction.fragments;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.dashboard.DashBaseFragment;
import net.osmand.plus.dashboard.DashboardOnMap;
import net.osmand.plus.stressreduction.database.SQLiteLogger;

import org.apache.commons.logging.Log;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class DashUserInfoFragment extends DashBaseFragment {

	private static final Log log = PlatformUtil.getLog(DashUserInfoFragment.class);

	public static final String TAG = "DASH_USER_INFO_FRAGMENT";

	// Imported in shouldShow method
	private static OsmandSettings settings;
	private FragmentState state = FragmentState.INITIAL;
	private UserInfoDismissListener userInfoDismissListener;

	@Override
	public void onOpenDash() {

	}

	@Override
	public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
	                     @Nullable Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.sr_dash_user_info_fragment, container, false);
		TextView header = (TextView) view.findViewById(R.id.header);
		TextView subheader = (TextView) view.findViewById(R.id.subheader);
		Button genderButton = (Button) view.findViewById(R.id.buttonGender);
		Button ageButton = (Button) view.findViewById(R.id.buttonAge);
		EditText carText = (EditText) view.findViewById(R.id.editTextCar);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.layoutUserInfo);
		Button positiveButton = (Button) view.findViewById(R.id.positive_button);
		Button negativeButton = (Button) view.findViewById(R.id.negative_button);
		positiveButton.setOnClickListener(
				new PositiveButtonListener(header, subheader, positiveButton, negativeButton,
						genderButton, ageButton, carText, layout));
		negativeButton.setOnClickListener(
				new NegativeButtonListener(header, subheader, positiveButton, negativeButton));
		userInfoDismissListener = new UserInfoDismissListener(dashboard, settings);
		return view;
	}

	public static boolean shouldShow(OsmandSettings settings) {
		if (!settings.SR_LAST_DISPLAY_TIME.isSet()) {
			settings.SR_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
		}
		DashUserInfoFragment.settings = settings;
		long lastDisplayTimeInMillis = settings.SR_LAST_DISPLAY_TIME.get();
		int numberOfApplicationRuns = settings.SR_NUMBER_OF_APPLICATION_STARTS.get();
		SrUserInfoState state = settings.SR_USER_INFO_STATE.get();

		log.debug("shouldShow(): state=" + state + ", numberOfApplicationStarts=" +
				numberOfApplicationRuns);

		Calendar modifiedTime = Calendar.getInstance();
		Calendar lastDisplayTime = Calendar.getInstance();
		lastDisplayTime.setTimeInMillis(lastDisplayTimeInMillis);

		int bannerFreeRuns;

		switch (state) {
			case SHARED_INFO:
				log.debug("shouldShow(): already shared info");
				return false;
			case INITIAL_STATE:
				log.debug("shouldShow(): initial state");
				modifiedTime.add(Calendar.HOUR, -72);
				bannerFreeRuns = 5;
				return modifiedTime.after(lastDisplayTime) &&
						numberOfApplicationRuns >= bannerFreeRuns;
			case USER_INFO_DELAYED:
				log.debug("shouldShow(): user info delayed");
				modifiedTime.add(Calendar.HOUR, -48);
				bannerFreeRuns = 3;
				return modifiedTime.after(lastDisplayTime) &&
						numberOfApplicationRuns >= bannerFreeRuns;
			case DO_NOT_SHOW_AGAIN:
				log.debug("shouldShow(): do not show again");
				return false;
			default:
				throw new IllegalStateException("Unexpected state:" + state);
		}
	}

	@Override
	public DismissListener getDismissCallback() {
		return userInfoDismissListener;
	}

	public class PositiveButtonListener implements View.OnClickListener {

		private TextView header;
		private TextView subheader;
		private Button gender;
		private Button age;
		private EditText car;
		private Button positiveButton;
		private Button negativeButton;
		private LinearLayout layout;

		public PositiveButtonListener(TextView header, TextView subheader, Button positiveButton,
		                              Button negativeButton, Button gender, Button age,
		                              EditText car, LinearLayout layout) {
			this.header = header;
			this.subheader = subheader;
			this.gender = gender;
			this.age = age;
			this.car = car;
			this.layout = layout;
			this.positiveButton = positiveButton;
			this.negativeButton = negativeButton;
		}

		@Override
		public void onClick(View v) {
			switch (state) {
				case INITIAL:
					state = FragmentState.USER_SHARES;
					layout.setVisibility(View.VISIBLE);
					gender.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showPicker("gender", gender);
						}
					});
					age.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							showPicker("age", age);
						}
					});
					header.setText(getResources().getString(R.string.sr_user_info_enter));
					subheader.setText(getResources().getString(R.string.sr_user_info_enter_info));
					positiveButton.setText(getResources().getString(R.string.shared_string_save));
					negativeButton.setText(getResources().getString(R.string
							.shared_string_cancel));
					return;
				case USER_SHARES:
					if (gender.getText().toString().equals("")) {
						Toast.makeText(settings.getContext(),
								getResources().getString(R.string.sr_user_info_provide_gender),
								Toast.LENGTH_LONG).show();
					} else if (age.getText().toString().equals("")) {
						Toast.makeText(settings.getContext(),
								getResources().getString(R.string.sr_user_info_provide_age),
								Toast.LENGTH_LONG).show();
					} else if (car.getText().toString().equals("")) {
						Toast.makeText(settings.getContext(),
								getResources().getString(R.string.sr_user_info_provide_car),
								Toast.LENGTH_LONG).show();
					} else {
						settings.SR_USER_INFO_STATE.set(SrUserInfoState.SHARED_INFO);
						SQLiteLogger.getSQLiteLogger(settings.getContext())
								.insertUserInfo(gender.getText().toString(),
										age.getText().toString(), car.getText().toString());
						dashboard.refreshDashboardFragments();
					}
					return;
				case USER_DECLINES:
					settings.SR_USER_INFO_STATE.set(SrUserInfoState.USER_INFO_DELAYED);
					settings.SR_NUMBER_OF_APPLICATION_STARTS.set(0);
					settings.SR_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
					dashboard.refreshDashboardFragments();
					break;
			}
		}
	}

	public class NegativeButtonListener implements View.OnClickListener {

		private TextView header;
		private TextView subheader;
		private Button positiveButton;
		private Button negativeButton;

		public NegativeButtonListener(TextView header, TextView subheader, Button positiveButton,
		                              Button negativeButton) {
			this.header = header;
			this.subheader = subheader;
			this.positiveButton = positiveButton;
			this.negativeButton = negativeButton;
		}

		@Override
		public void onClick(View v) {
			switch (state) {
				case INITIAL:
					state = FragmentState.USER_DECLINES;

					header.setText(getResources().getString(R.string.sr_user_info_declined));
					subheader
							.setText(getResources().getString(R.string
									.sr_user_info_declined_info));
					positiveButton.setText(getResources().getString(R.string.shared_string_ok));
					negativeButton.setText(
							getResources().getString(R.string.shared_string_do_not_show_again));
					return;
				case USER_SHARES:
					settings.SR_USER_INFO_STATE.set(SrUserInfoState.USER_INFO_DELAYED);
					break;
				case USER_DECLINES:
					settings.SR_USER_INFO_STATE.set(SrUserInfoState.DO_NOT_SHOW_AGAIN);
					break;
			}
			settings.SR_NUMBER_OF_APPLICATION_STARTS.set(0);
			settings.SR_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
			dashboard.refreshDashboardFragments();
		}
	}

	private enum FragmentState {
		INITIAL,
		USER_SHARES,
		USER_DECLINES
	}

	public enum SrUserInfoState {
		INITIAL_STATE,
		SHARED_INFO,
		USER_INFO_DELAYED,
		DO_NOT_SHOW_AGAIN
	}

	public static class UserInfoShouldShow extends DashboardOnMap.DefaultShouldShow {

		@Override
		public boolean shouldShow(OsmandSettings settings, MapActivity activity, String tag) {
			return DashUserInfoFragment.shouldShow(settings) &&
					super.shouldShow(settings, activity, tag);
		}
	}

	private static class UserInfoDismissListener implements DismissListener {

		private DashboardOnMap dashboardOnMap;
		private OsmandSettings settings;

		public UserInfoDismissListener(DashboardOnMap dashboardOnMap, OsmandSettings settings) {
			this.dashboardOnMap = dashboardOnMap;
			this.settings = settings;
		}

		@Override
		public void onDismiss() {
			settings.SR_USER_INFO_STATE.set(SrUserInfoState.USER_INFO_DELAYED);
			settings.SR_NUMBER_OF_APPLICATION_STARTS.set(0);
			settings.SR_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
			dashboardOnMap.refreshDashboardFragments();
		}
	}

	private void showPicker(String which, final Button caller) {
		switch (which) {
			case "gender":
				final String genders[] = {"-", getResources().getString(R.string
						.sr_user_info_male),
						getResources().getString(R.string.sr_user_info_female)};

				NumberPicker genderPicker = new NumberPicker(settings.getContext());
				genderPicker.setMinValue(0);
				genderPicker.setMaxValue(genders.length - 1);
				genderPicker.setDisplayedValues(genders);
				genderPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				genderPicker.setValue(1);
				caller.setText(genders[1]);

				NumberPicker.OnValueChangeListener gernderChangedListener =
						new NumberPicker.OnValueChangeListener() {
							@Override
							public void onValueChange(NumberPicker picker, int oldVal, int
									newVal) {
								caller.setText(genders[newVal]);
							}
						};

				genderPicker.setOnValueChangedListener(gernderChangedListener);

				AlertDialog.Builder builderGender =
						new AlertDialog.Builder(getActivity()).setView(genderPicker);
				builderGender.setPositiveButton(R.string.shared_string_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				builderGender.show();
				break;
			case "age":
				NumberPicker agePicker = new NumberPicker(settings.getContext());
				agePicker.setMaxValue(100);
				agePicker.setMinValue(16);
				agePicker.setValue(30);
				agePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
				caller.setText("30");

				NumberPicker.OnValueChangeListener ageChangedListener =
						new NumberPicker.OnValueChangeListener() {
							@Override
							public void onValueChange(NumberPicker picker, int oldVal, int
									newVal) {
								caller.setText(String.valueOf(newVal));
							}
						};

				agePicker.setOnValueChangedListener(ageChangedListener);

				AlertDialog.Builder builderAge =
						new AlertDialog.Builder(getActivity()).setView(agePicker);
				builderAge.setPositiveButton(R.string.shared_string_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
				builderAge.show();
				break;
		}
	}
}
