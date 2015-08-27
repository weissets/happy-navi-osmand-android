package net.osmand.plus.stressreduction.fragments;

import android.app.Dialog;
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

import net.osmand.plus.R;

/**
 * This class represents the what's new dialog fragment
 *
 * @author Tobias
 */
public class FragmentWhatsNewDialog extends DialogFragment implements View.OnClickListener {

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sr_whats_new_ok_button) {
			this.dismiss();
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
		View view = inflater.inflate(R.layout.sr_dialog_whats_new, container);
		Button okButton = (Button) view.findViewById(R.id.sr_whats_new_ok_button);
		okButton.setOnClickListener(this);

		this.setCancelable(false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		return view;
	}

}