package net.osmand.plus.dashboard;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.stressreduction.Constants;

import org.apache.commons.logging.Log;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class DashBlankFragment extends DashBaseFragment {

	private static final Log log = PlatformUtil.getLog(DashBlankFragment.class);

	public static final String TAG = "DASH_BLANK_FRAGMENT";

	// Imported in shouldShow method
	private static OsmandSettings settings;
	private BlankDismissListener blankDismissListener;

	private static JSONObject jsonObject;
	private static String locale;

	@Override
	public void onOpenDash() {

	}

	@Override
	public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
	                     @Nullable Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.sr_dash_blank_fragment, container, false);
		loadText(view);
		blankDismissListener = new BlankDismissListener(dashboard, settings);
		return view;
	}

	private void loadText(View view) {
		TextView header = (TextView) view.findViewById(R.id.header);
		TextView subheader = (TextView) view.findViewById(R.id.subheader);
		Button positiveButton = (Button) view.findViewById(R.id.blank_positive_button);
		Button neutralButton = (Button) view.findViewById(R.id.blank_neutral_button);
		Button negativeButton = (Button) view.findViewById(R.id.blank_negative_button);
		positiveButton.setOnClickListener(new PositiveButtonListener());
		neutralButton.setOnClickListener(new NeutralButtonListener());
		negativeButton.setOnClickListener(new NegativeButtonListener());

		try {
			jsonObject = new JSONObject(settings.SR_CURRENT_BLANK.get());
			if (settings.PREFERRED_LOCALE.get().toLowerCase().contains("de")) {
				locale = "blank_de";
			} else {
				locale = "blank_en";
			}
			header.setText(jsonObject.getJSONObject(locale).getString("header"));
			subheader.setText(jsonObject.getJSONObject(locale).getString("subheader"));
			positiveButton.setText(jsonObject.getJSONObject(locale).getString("positiveButton"));
			neutralButton.setText(jsonObject.getJSONObject(locale).getString("neutralButton"));
			negativeButton.setText(jsonObject.getJSONObject(locale).getString("negativeButton"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static boolean shouldShow(final OsmandSettings settings) {
		DashBlankFragment.settings = settings;

		long lastServerCheckTimeInMillis = settings.SR_BLANK_LAST_SERVER_CHECK_TIME.get();
		Calendar lastServerCheckTime = Calendar.getInstance();
		lastServerCheckTime.setTimeInMillis(lastServerCheckTimeInMillis);
		Calendar modifiedCheckTime = Calendar.getInstance();
		modifiedCheckTime.add(Calendar.HOUR, -24);
		if (modifiedCheckTime.after(lastServerCheckTime)) {
			getJsonObject();
			settings.SR_BLANK_LAST_SERVER_CHECK_TIME.set(System.currentTimeMillis());
		}

		int currentVersion = settings.SR_CURRENT_BLANK_VERSION.get();
		int serverVersion;
		if (settings.SR_CURRENT_BLANK.get().equals("")) {
			return false;
		} else {
			try {
				jsonObject = new JSONObject(settings.SR_CURRENT_BLANK.get());
				serverVersion = jsonObject.getJSONObject("version").getInt("version");
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}

		if (serverVersion > currentVersion) {
			settings.SR_BLANK_STATE.set(SrBlankState.INITIAL);
			settings.SR_CURRENT_BLANK_VERSION.set(serverVersion);
			log.debug("shouldShow(): new version, back to inital");
		}

		SrBlankState state = settings.SR_BLANK_STATE.get();
		long laterTimeInMillis = settings.SR_BLANK_LATER_TIME.get();
		Calendar laterTime = Calendar.getInstance();
		laterTime.setTimeInMillis(laterTimeInMillis);
		Calendar modifiedTime = Calendar.getInstance();

		if (state == SrBlankState.DO_NOT_SHOW_AGAIN) {
			log.debug("shouldShow(): do not show again");
			return false;
		} else if (state == SrBlankState.INITIAL) {
			log.debug("shouldShow(): inital");
			return true;
		} else if (state == SrBlankState.LATER) {
			log.debug("shouldShow(): later");
			modifiedTime.add(Calendar.HOUR, -24);
		}

		if (state == SrBlankState.LATER && modifiedTime.after(laterTime)) {
			log.debug("shouldShow(): time for later is up, back to initial");
			settings.SR_BLANK_STATE.set(SrBlankState.INITIAL);
		}

		return false;
	}

	private static void getJsonObject() {
		new Thread() {
			@Override
			public void run() {
				try {
					log.debug("getJsonObject()");
					URL url = new URL(Constants.URI_JSON_BLANK);
					HttpsURLConnection httpsURLConnection =
							(HttpsURLConnection) url.openConnection();
					httpsURLConnection.setRequestMethod("GET");
					httpsURLConnection.setDoInput(true); // allow inputs
					httpsURLConnection.setDoOutput(true); // allow outputs
					httpsURLConnection.setUseCaches(false); // no cached copy
					httpsURLConnection.setRequestProperty("Connection", "Close");
					httpsURLConnection.setRequestProperty("ENCTYPE", "text/plain");
					httpsURLConnection.connect();

					BufferedReader in = new BufferedReader(
							new InputStreamReader(httpsURLConnection.getInputStream()));
					String line;
					StringBuilder stringBuilder = new StringBuilder();
					while ((line = in.readLine()) != null) {
						stringBuilder.append(line);
					}
					log.debug("getJsonObject(): json = " + stringBuilder.toString());
					settings.SR_CURRENT_BLANK.set(stringBuilder.toString());
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	@Override
	public DismissListener getDismissCallback() {
		return blankDismissListener;
	}

	public class PositiveButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getJSONObject
						(locale).getString("url")));
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			settings.SR_BLANK_STATE.set(SrBlankState.DO_NOT_SHOW_AGAIN);
			dashboard.refreshDashboardFragments();
		}
	}

	public class NeutralButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			settings.SR_BLANK_LATER_TIME.set(System.currentTimeMillis());
			settings.SR_BLANK_STATE.set(SrBlankState.LATER);
			dashboard.refreshDashboardFragments();
		}
	}

	public class NegativeButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			settings.SR_BLANK_STATE.set(SrBlankState.DO_NOT_SHOW_AGAIN);
			dashboard.refreshDashboardFragments();
		}
	}

	public enum SrBlankState {
		LATER,
		DO_NOT_SHOW_AGAIN,
		INITIAL
	}

	public static class BlankShouldShow extends DashboardOnMap.DefaultShouldShow {

		@Override
		public boolean shouldShow(OsmandSettings settings, MapActivity activity, String tag) {
			return DashBlankFragment.shouldShow(settings) &&
					super.shouldShow(settings, activity, tag);
		}
	}

	private static class BlankDismissListener implements DismissListener {

		private DashboardOnMap dashboardOnMap;
		private OsmandSettings settings;

		public BlankDismissListener(DashboardOnMap dashboardOnMap, OsmandSettings settings) {
			this.dashboardOnMap = dashboardOnMap;
			this.settings = settings;
		}

		@Override
		public void onDismiss() {
			settings.SR_BLANK_LATER_TIME.set(System.currentTimeMillis());
			settings.SR_BLANK_STATE.set(SrBlankState.LATER);
			dashboardOnMap.refreshDashboardFragments();
		}
	}

}
