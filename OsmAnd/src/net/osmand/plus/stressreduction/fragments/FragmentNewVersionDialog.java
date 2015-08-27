package net.osmand.plus.stressreduction.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import net.osmand.plus.R;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;

/**
 * This class represents the what's new fragment
 *
 * @author Tobias
 */
public class FragmentNewVersionDialog extends DialogFragment implements View.OnClickListener {

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sr_new_version_cancel_button) {
			this.dismiss();
		}
		if (v.getId() == R.id.sr_new_version_download_button) {
			final String packageName = this.getActivity().getPackageName();
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=" + packageName)));
			} catch (android.content.ActivityNotFoundException e) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("https://play.google.com/store/apps/details?id=" +
								packageName)));
			}
		}
		if (v.getId() == R.id.sr_new_version_checkbox) {
			SRSharedPreferences.setDisplayNewVersionDialog(this.getActivity().
					getApplication(), !((CheckBox) v).isChecked());
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		Window window = getDialog().getWindow();
		WindowManager.LayoutParams windowParams = window.getAttributes();
		windowParams.dimAmount = 0.80f;
		windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(windowParams);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new Dialog(getActivity(), getTheme()) {
			@Override
			public void onBackPressed() {
				this.dismiss();
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sr_dialog_new_version, container);
		Button okButton = (Button) view.findViewById(R.id.sr_new_version_download_button);
		okButton.setOnClickListener(this);
		Button playStoreButton = (Button) view.findViewById(R.id.sr_new_version_cancel_button);
		playStoreButton.setOnClickListener(this);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.sr_new_version_checkbox);
		checkBox.setOnClickListener(this);

		this.setCancelable(false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		return view;
	}

}