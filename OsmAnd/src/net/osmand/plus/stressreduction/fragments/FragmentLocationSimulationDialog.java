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
import android.widget.TextView;

import net.osmand.plus.R;

/**
 * This class represents the location simulation dialog fragment
 *
 * @author Tobias
 */
public class FragmentLocationSimulationDialog extends DialogFragment
		implements View.OnClickListener {

	private static StartSimulationListener startSimulationListener;
	private TextView speedText;

	public static FragmentLocationSimulationDialog newInstance(StartSimulationListener listener) {
		FragmentLocationSimulationDialog dialog = new FragmentLocationSimulationDialog();
		try {
			startSimulationListener = listener;
		} catch (ClassCastException e) {
			throw new ClassCastException(listener.getClass().getSimpleName() +
					" class must implement interface StartSimulationListener");
		}
		return dialog;
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.buttonPlus) {
			int speed = Integer.parseInt(speedText.getText().toString());
			speed += 10;
			if (speed > 100) {
				speed = 100;
			}
			speedText.setText(String.valueOf(speed));
		}
		if (v.getId() == R.id.buttonMinus) {
			int speed = Integer.parseInt(speedText.getText().toString());
			speed -= 10;
			if (speed < 0) {
				speed = 0;
			}
			speedText.setText(String.valueOf(speed));
		}
		if (v.getId() == R.id.sr_dialog_simulation_no) {
			this.dismiss();
		}
		if (v.getId() == R.id.sr_dialog_simulation_yes) {
			startSimulationListener
					.startSimulation(Integer.parseInt(speedText.getText().toString()));
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
		View view = inflater.inflate(R.layout.sr_dialog_simulation, container);
		Button noButton = (Button) view.findViewById(R.id.sr_dialog_simulation_no);
		noButton.setOnClickListener(this);
		Button yesButton = (Button) view.findViewById(R.id.sr_dialog_simulation_yes);
		yesButton.setOnClickListener(this);
		Button plusButton = (Button) view.findViewById(R.id.buttonPlus);
		plusButton.setOnClickListener(this);
		Button minusButton = (Button) view.findViewById(R.id.buttonMinus);
		minusButton.setOnClickListener(this);

		speedText = (TextView) view.findViewById(R.id.speedText);
		speedText.setText("50");

		this.setCancelable(false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		return view;
	}

	public interface StartSimulationListener {
		void startSimulation(int speed);
	}
}