package net.osmand.plus.stressreduction.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.stressreduction.tools.Calculation;

import org.apache.commons.logging.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents the stress reduction dialog fragment
 *
 * @author Tobias
 */
public class FragmentSRDialog extends DialogFragment implements View.OnClickListener,
		RecognitionListener {

	private static final Log log = PlatformUtil.getLog(FragmentSRDialog.class);

	private static SRDialogButtonClickListener srDialogButtonClickListener;
	private static String PLAY_SOUND = "play_sound";
	private SpeechRecognizer speechRecognizer = null;
	private Intent recognizerIntent;
	private ProgressBar speechProgress;
	private LinearLayout speechLayout;

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

		ImageButton buttonHappy = (ImageButton) view.findViewById(R.id.imageButtonFaceHappy);
		buttonHappy.setOnClickListener(this);

		ImageButton buttonNeutral = (ImageButton) view.findViewById(R.id.imageButtonFaceNeutral);
		buttonNeutral.setOnClickListener(this);

		ImageButton buttonSad = (ImageButton) view.findViewById(R.id.imageButtonFaceSad);
		buttonSad.setOnClickListener(this);

		this.setCancelable(false);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);


		// TODO check if voice input is enabled
		OsmandApplication osmandApplication = (OsmandApplication) getActivity().getApplication();
		if (osmandApplication.getSettings().SR_SPEECH_INPUT.get()) {
			speechLayout = (LinearLayout) view.findViewById(R.id.speechLayout);
			speechLayout.setVisibility(View.VISIBLE);
			speechProgress = (ProgressBar) view.findViewById(R.id.speechProgress);
			speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
			speechRecognizer.setRecognitionListener(this);
			recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
					osmandApplication.getLanguage()); //"en");
			//		recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
			//				this.getPackageName());
			recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
			speechRecognizer.startListening(recognizerIntent);
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

	/**
	 * Called when the endpointer is ready for the user to start speaking.
	 *
	 * @param params parameters set by the recognition service. Reserved for future use.
	 */
	@Override
	public void onReadyForSpeech(Bundle params) {

	}

	/**
	 * The user has started to speak.
	 */
	@Override
	public void onBeginningOfSpeech() {
		speechProgress.setIndeterminate(false);
		speechProgress.setMax(10);
	}

	/**
	 * The sound level in the audio stream has changed. There is no guarantee that this method will
	 * be called.
	 *
	 * @param rmsdB the new RMS dB value
	 */
	@Override
	public void onRmsChanged(float rmsdB) {
		speechProgress.setProgress((int) rmsdB);
	}

	/**
	 * More sound has been received. The purpose of this function is to allow giving feedback to
	 * the user regarding the captured audio. There is no guarantee that this method will be called.
	 *
	 * @param buffer a buffer containing a sequence of big-endian 16-bit integers representing a
	 *               single channel audio stream. The sample rate is implementation dependent.
	 */
	@Override
	public void onBufferReceived(byte[] buffer) {

	}

	/**
	 * Called after the user stops speaking.
	 */
	@Override
	public void onEndOfSpeech() {
		speechProgress.setIndeterminate(true);
		speechRecognizer.stopListening();
	}

	/**
	 * A network or recognition error occurred.
	 *
	 * @param error code is defined in {@link SpeechRecognizer}
	 */
	@Override
	public void onError(int error) {
		String message;
		switch (error) {
			case SpeechRecognizer.ERROR_AUDIO:
				message = "Audio recording error";
				break;
			case SpeechRecognizer.ERROR_CLIENT:
				message = "Client side error";
				break;
			case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
				message = "Insufficient permissions";
				break;
			case SpeechRecognizer.ERROR_NETWORK:
				message = "Network error";
				break;
			case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				message = "Network timeout";
				break;
			case SpeechRecognizer.ERROR_NO_MATCH:
				message = "No match";
				break;
			case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				message = "RecognitionService busy";
				break;
			case SpeechRecognizer.ERROR_SERVER:
				message = "error from server";
				break;
			case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				message = "No speech input";
				break;
			default:
				message = "Didn't understand, please try again.";
				break;
		}
		log.error("onError(): error = " + message);
		speechRecognizer.stopListening();
	}

	/**
	 * Called when recognition results are ready.
	 *
	 * @param results the recognition results. To retrieve the results in {@code
	 *                ArrayList&lt;String&gt;} format use {@link Bundle#getStringArrayList(String)}
	 *                with
	 *                {@link SpeechRecognizer#RESULTS_RECOGNITION} as a parameter. A float array of
	 *                confidence values might also be given in {@link
	 *                SpeechRecognizer#CONFIDENCE_SCORES}.
	 */
	@Override
	public void onResults(Bundle results) {
		ArrayList<String> matches = results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		if (matches != null) {
			for (String result : matches)
				log.debug("onResults(): result = " + result);
		}
	}

	/**
	 * Called when partial recognition results are available. The callback might be called at any
	 * time between {@link #onBeginningOfSpeech()} and {@link #onResults(Bundle)} when partial
	 * results are ready. This method may be called zero, one or multiple times for each call to
	 * {@link SpeechRecognizer#startListening(Intent)}, depending on the speech recognition
	 * service implementation.  To request partial results, use
	 * {@link RecognizerIntent#EXTRA_PARTIAL_RESULTS}
	 *
	 * @param partialResults the returned results. To retrieve the results in
	 *                       ArrayList&lt;String&gt; format use {@link Bundle#getStringArrayList
	 *                       (String)}
	 *                       with
	 *                       {@link SpeechRecognizer#RESULTS_RECOGNITION} as a parameter
	 */
	@Override
	public void onPartialResults(Bundle partialResults) {

	}

	/**
	 * Reserved for adding future events.
	 *
	 * @param eventType the type of the occurred event
	 * @param params    a Bundle containing the passed parameters
	 */
	@Override
	public void onEvent(int eventType, Bundle params) {

	}

	public interface SRDialogButtonClickListener {

		void onSRButtonClick(View view, String timestamp);
	}
}