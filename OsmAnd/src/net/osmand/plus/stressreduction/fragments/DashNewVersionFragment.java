package net.osmand.plus.stressreduction.fragments;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;
import net.osmand.plus.Version;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.dashboard.DashBaseFragment;
import net.osmand.plus.dashboard.DashboardOnMap;
import net.osmand.plus.dashboard.tools.DashFragmentData;
import net.osmand.plus.stressreduction.Constants;

import org.apache.commons.logging.Log;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class DashNewVersionFragment extends DashBaseFragment {

	private static final Log log = PlatformUtil.getLog(DashNewVersionFragment.class);

	public static final String TAG = "DASH_NEW_VERSION_FRAGMENT";

	// Imported in shouldShow method
	private static OsmandSettings settings;
	private NewVersionDismissListener newVersionDismissListener;

	@Override
	public void onOpenDash() {

	}

	@Override
	public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
	                     @Nullable Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater()
				.inflate(R.layout.sr_dash_new_version_fragment, container, false);
		Button downloadButton = (Button) view.findViewById(R.id.download_button);
		Button laterButton = (Button) view.findViewById(R.id.later_button);
		//		Button dismissButton = (Button) view.findViewById(R.id.dismiss_button);

		downloadButton.setOnClickListener(new DownloadButtonListener());
		laterButton.setOnClickListener(new LaterButtonListener());
		//		dismissButton.setOnClickListener(new DismissButtonListener());
		newVersionDismissListener = new NewVersionDismissListener(dashboard, settings);
		return view;
	}

	public static boolean shouldShow(final OsmandSettings settings) {
		if (!settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.isSet()) {
			settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
		}

		DashNewVersionFragment.settings = settings;
		long lastDisplayTimeInMillis = settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.get();
		long lastCheckTimeInMillis = settings.SR_NEW_VERSION_LAST_CHECK_TIME.get();
		SrNewVersionState state = settings.SR_NEW_VERSION_STATE.get();

		if (settings.SR_VERSION_CODE_SERVER.get() == 0 && state != SrNewVersionState.CHECK_AGAIN) {
			log.debug("shouldShow(): version code = 0, getting version code");
			// check version
			getVersionCode();
			settings.SR_NEW_VERSION_LAST_CHECK_TIME.set(System.currentTimeMillis());
			settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.CHECK_AGAIN);
			return false;
		}

		Calendar modifiedTime = Calendar.getInstance();
		Calendar lastDisplayTime = Calendar.getInstance();
		Calendar lastCheckTime = Calendar.getInstance();
		lastDisplayTime.setTimeInMillis(lastDisplayTimeInMillis);
		lastCheckTime.setTimeInMillis(lastCheckTimeInMillis);

		int thisVersion = 0;
		try {
			thisVersion = settings.getContext().getPackageManager()
					.getPackageInfo(settings.getContext().getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		int version = settings.SR_VERSION_CODE_SERVER.get();

		log.debug("shouldShow(): state=" + state + ", lastDisplayed=" + lastDisplayTime.getTime());

		if (Version.isFreeVersion(settings.getContext())) {
			log.debug("shouldShow(): no, is free version. play store does that!");
			return false;
		} else if (state == SrNewVersionState.LATER) {
			log.debug("shouldShow(): later");
			modifiedTime.add(Calendar.HOUR, -24);
		} else if (state == SrNewVersionState.CHECK_AGAIN) {
			log.debug("shouldShow(): check again");
			modifiedTime.add(Calendar.MINUTE, -1);
		} else if (state == SrNewVersionState.INITIAL) {
			log.debug("shouldShow(): version is " + version + ", thisVersion is " + thisVersion);
			if (version == 0) {
				log.debug("shouldShow(): check version");
				settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.CHECK_AGAIN);
			} else if (version > thisVersion) {
				log.debug("shouldShow(): found new version!!!");
				settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.NEW_VERSION);
				return true;
			} else {
				log.debug("shouldShow(): version code equal, no new version, state " +
						"set to later");
				settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.LATER);
				settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
			}
		} else if (state == SrNewVersionState.NEW_VERSION) {
			log.debug("shouldShow(): new version, showing dash fragment");
			return true;
		}

		if (modifiedTime.after(lastDisplayTime) && state == SrNewVersionState.LATER) {
			log.debug("shouldShow(): time for later is up, back to initial");
			settings.SR_VERSION_CODE_SERVER.set(0);
			settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.INITIAL);
		}
		if (modifiedTime.after(lastCheckTime) && state == SrNewVersionState.CHECK_AGAIN) {
			log.debug("shouldShow(): time for check again is up, back to initial");
			settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.INITIAL);
		}
		return false;
	}

	private static void getVersionCode() {
		new Thread() {
			@Override
			public void run() {
				try {
					log.debug("getVersionCode()");
					URL url = new URL(Constants.URI_VERSION_CODE);
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
					String v;
					String ver = "";
					while ((v = in.readLine()) != null) {
						ver += v;
					}
					log.debug("getVersionCode(): ver = " + ver);
					settings.SR_VERSION_CODE_SERVER.set(Integer.valueOf(ver));
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	@Override
	public DismissListener getDismissCallback() {
		return newVersionDismissListener;
	}

	public class DownloadButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URI_HOMEPAGE));
			try {
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
			settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.LATER);
			dashboard.refreshDashboardFragments();
		}
	}

	public class LaterButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
			settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.LATER);
			dashboard.refreshDashboardFragments();
		}
	}

	//	public class DismissButtonListener implements View.OnClickListener {
	//
	//		@Override
	//		public void onClick(View v) {
	//
	//			dashboard.refreshDashboardFragments();
	//		}
	//	}

	public enum SrNewVersionState {
		NEW_VERSION,
		LATER,
		CHECK_AGAIN,
		INITIAL
	}

	public static final DashFragmentData.ShouldShowFunction SHOULD_SHOW_FUNCTION =
			new DashboardOnMap.DefaultShouldShow() {
				@Override
				public boolean shouldShow(OsmandSettings settings, MapActivity activity,
				                          String tag) {
					return DashNewVersionFragment.shouldShow(settings) &&
							super.shouldShow(settings, activity, tag);
				}
			};

	private static class NewVersionDismissListener implements DismissListener {

		private DashboardOnMap dashboardOnMap;
		private OsmandSettings settings;

		public NewVersionDismissListener(DashboardOnMap dashboardOnMap, OsmandSettings settings) {
			this.dashboardOnMap = dashboardOnMap;
			this.settings = settings;
		}

		@Override
		public void onDismiss() {
			settings.SR_NEW_VERSION_STATE.set(SrNewVersionState.LATER);
			settings.SR_NEW_VERSION_LAST_DISPLAY_TIME.set(System.currentTimeMillis());
			dashboardOnMap.refreshDashboardFragments();
		}
	}

}
