package net.osmand.plus.stressreduction.fragments;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.tools.Calculation;
import net.osmand.plus.stressreduction.voice.SRPocketSphinx;

import org.apache.commons.logging.Log;

import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;

import java.io.IOException;

/**
 * This class represents the stress reduction dialog fragment
 *
 * @author Tobias
 */
public class FragmentSRDialog extends DialogFragment implements View.OnClickListener {

	private static final Log log = PlatformUtil.getLog(FragmentSRDialog.class);

	private static SRDialogButtonClickListener srDialogButtonClickListener;
	private static String PLAY_SOUND = "play_sound";
	private ImageView speechImage;
	private View selectedButton;

	private ImageButton buttonHappy;
	private ImageButton buttonNeutral;
	private ImageButton buttonSad;

	private TableRow speechRow;
	private TableRow speechRowValidate;

	public static FragmentSRDialog newInstance(SRDialogButtonClickListener listener,
	                                           boolean playSound) {
		FragmentSRDialog dialog = new FragmentSRDialog();
		Bundle arguments = new Bundle(1);
		arguments.putBoolean(PLAY_SOUND, playSound);
		dialog.setArguments(arguments);
		try {
			srDialogButtonClickListener = listener;
		} catch (ClassCastException e) {
			throw new ClassCastException(listener.getClass().getSimpleName() +
					" class must implement interface SRDialogButtonClickListener");
		}
		return dialog;
	}

	@Override
	public void dismiss() {
		SRPocketSphinx.getInstance().stopListening();
		super.dismiss();
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		String timestamp = Calculation.getCurrentDateTimeMs();
		srDialogButtonClickListener.onSRButtonClick(v, timestamp);
		this.dismiss();
	}

	@Override
	public void onStart() {
		super.onStart();

		Window window = getDialog().getWindow();
		WindowManager.LayoutParams windowParams = window.getAttributes();
		windowParams.dimAmount = 0.50f;
		windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(windowParams);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new Dialog(getActivity(), getTheme()) {
			@Override
			public void onBackPressed() {
				((OsmandApplication) this.getOwnerActivity().getApplication())
						.showShortToastMessage(R.string.sr_request_rating);
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sr_dialog_rating, container);

		Bundle arguments = this.getArguments();
		if (arguments.getBoolean(PLAY_SOUND, false)) {
			playNotificationSound();
		}

		buttonHappy = (ImageButton) view.findViewById(R.id.imageButtonFaceHappy);
		buttonHappy.setOnClickListener(this);

		buttonNeutral = (ImageButton) view.findViewById(R.id.imageButtonFaceNeutral);
		buttonNeutral.setOnClickListener(this);

		buttonSad = (ImageButton) view.findViewById(R.id.imageButtonFaceSad);
		buttonSad.setOnClickListener(this);

		this.setCancelable(false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		// voice and/or touch input
		OsmandApplication osmandApplication =
				(OsmandApplication) getActivity().getApplicationContext();
		if (osmandApplication.getSettings().SR_SPEECH_INPUT.get()) {

			speechRow = (TableRow) view.findViewById(R.id.speechRow);
			speechRow.setVisibility(View.VISIBLE);
			speechRowValidate = (TableRow) view.findViewById(R.id.speechRowValidate);

			speechImage = (ImageView) view.findViewById(R.id.speechImage);
			speechImage.setVisibility(View.VISIBLE);

			promptSpeechInput(Constants.SPEECH_INPUT, speechImage);
		}

		return view;
	}

	private void playNotificationSound() {
		try {
			AssetFileDescriptor afd = this.getActivity().getApplication().getAssets()
					.openFd("sounds/desk_bell" + ".mp3");
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer
					.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			mediaPlayer.prepare();
			mediaPlayer.setVolume(1.0f, 1.0f);
			mediaPlayer.start();
		} catch (IllegalArgumentException | IllegalStateException | IOException e) {
			e.printStackTrace();
		}

	}

	public interface SRDialogButtonClickListener {

		void onSRButtonClick(View view, String timestamp);
	}

	/**
	 * Showing speech input dialog
	 */
	public void promptSpeechInput(String mode, ImageView speechImage) {
		switch (mode) {
			case Constants.SPEECH_INPUT:
				speechRow.setVisibility(View.VISIBLE);
				speechRowValidate.setVisibility(View.GONE);
				break;
			case Constants.SPEECH_VALIDATION:
				speechRow.setVisibility(View.GONE);
				speechRowValidate.setVisibility(View.VISIBLE);
				break;
		}
		SRPocketSphinx.getInstance().startListening(mode, this, speechImage);
	}

	public void setResult(String mode, String result) {
		log.debug("setResult(): mode = " + mode + ", result = " + result);
		switch (mode) {
			case Constants.SPEECH_VALIDATION:
				if (Constants.SPEECH_VALIDATION_CONFIRM.toString()
						.matches(".*\\b" + result + "\\b.*")) {
					selectedButton.setSelected(false);
					selectedButton.setPressed(true);
					onClick(selectedButton);
					log.debug("setResult(): button confirmed and button pressed!");
				} else if (Constants.SPEECH_VALIDATION_RETRY.toString()
						.matches(".*\\b" + result + "\\b.*")) {
					selectedButton.setSelected(false);
					promptSpeechInput(Constants.SPEECH_INPUT, speechImage);
					log.debug("setResult(): canceled, retry...");
				} else {
					log.error("setResult(): should not happen!");
					return;
				}
				break;
			case Constants.SPEECH_INPUT:
				if (Constants.SPEECH_INPUT_GOOD.toString().matches(".*\\b" + result + "\\b.*")) {
					buttonHappy.setSelected(true);
					selectedButton = buttonHappy;
				} else if (Constants.SPEECH_INPUT_NORMAL.toString()
						.matches(".*\\b" + result + "\\b.*")) {
					buttonNeutral.setSelected(true);
					selectedButton = buttonNeutral;
				} else if (Constants.SPEECH_INPUT_BAD.toString()
						.matches(".*\\b" + result + "\\b.*")) {
					buttonSad.setSelected(true);
					selectedButton = buttonSad;
				} else {
					log.error("setResult(): should not happen!");
					return;
				}
				promptSpeechInput(Constants.SPEECH_VALIDATION, speechImage);
				break;
		}
	}

}