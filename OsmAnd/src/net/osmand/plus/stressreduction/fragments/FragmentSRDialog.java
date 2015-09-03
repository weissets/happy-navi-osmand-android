package net.osmand.plus.stressreduction.fragments;

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

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * This class represents the stress reduction dialog fragment
 *
 * @author Tobias
 */
public class FragmentSRDialog extends DialogFragment implements View.OnClickListener {

	private static SRDialogButtonClickListener srDialogButtonClickListener;
	private static String PLAY_SOUND = "play_sound";

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

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
				.format(new java.util.Date());
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

		ImageButton buttonHappy = (ImageButton) view.findViewById(R.id.imageButtonFaceHappy);
		buttonHappy.setOnClickListener(this);

		ImageButton buttonNeutral = (ImageButton) view.findViewById(R.id.imageButtonFaceNeutral);
		buttonNeutral.setOnClickListener(this);

		ImageButton buttonSad = (ImageButton) view.findViewById(R.id.imageButtonFaceSad);
		buttonSad.setOnClickListener(this);

		this.setCancelable(false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		Bundle arguments = this.getArguments();
		if (arguments.getBoolean(PLAY_SOUND, false)) {
			playNotificationSound();
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
}