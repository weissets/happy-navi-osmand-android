package net.osmand.plus.stressreduction.voice;

import net.osmand.PlatformUtil;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.fragments.FragmentSRDialog;

import org.apache.commons.logging.Log;

import android.os.AsyncTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * This class implements the speech recognition for the stress reduction dialog
 *
 * @author Tobias
 */
public class SRPocketSphinx implements RecognitionListener {

	private static final Log log = PlatformUtil.getLog(FragmentSRDialog.class);

	private OsmandApplication osmandApplication;
	private static SRPocketSphinx instance;
	private SpeechRecognizer speechRecognizer;
	private FragmentSRDialog dialog;
	private ImageView speechImage;
	private String mode;
	private String debug;

	private SRPocketSphinx() {

	}

	public void init(final OsmandApplication osmandApplication, final float threshold) {
		this.osmandApplication = osmandApplication;
		new AsyncTask<Void, Void, Exception>() {
			@Override
			protected Exception doInBackground(Void... params) {
				try {
					Assets assets = new Assets(osmandApplication);
					File assetDir = assets.syncAssets();
					setupRecognizer(assetDir, threshold);
				} catch (IOException e) {
					return e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Exception result) {
				if (result != null) {
					log.error("onPostExecute(): failed to init recognizer! " + result);
				}
			}
		}.execute();
	}

	public static SRPocketSphinx getInstance() {
		if (instance == null) {
			instance = new SRPocketSphinx();
		}
		return instance;
	}

	// TODO adapt acoustic model
	private void setupRecognizer(File assetsDir, float threshold) throws IOException {
		// The recognizer can be configured to perform multiple searches
		// of different kind and switch between them
		speechRecognizer =
				defaultSetup().setAcousticModel(new File(assetsDir, "en-us-ptm")).setDictionary(
						new File(assetsDir, "sr_dialog.dict"))

						// To disable logging of raw audio comment out this call (takes a lot of
						// space on the device)
						//						.setRawLogDir(assetsDir)
						// Threshold to tune for keyphrase to balance between false alarms
						// and misses
						.setKeywordThreshold(1e-45f)
						// Use context-independent phonetic search, context-dependent is
						// too slow for mobile
						.setBoolean("-allphone_ci", true)
						// This is the dsratio. In most cases -ds 2 gives the best performance,
						// though accuracy suffers a bit. (Frame GMM computation downsampling
						// ratio) Thus lower should be better and higher should be less accurate.
						.setInteger("-ds", 1)
						// The default value is 4, the fastest value is 2, but accuracy can
						// suffer a bit depending on your acoustic model.
						.setInteger("-topn", 4)
						// This can be set quite low and still give you reasonable performance -
						// try 5.
//						.setInteger("-maxwpf", 5)
						// Depending on the acoustic and language model this can be very helpful.
						// Try 3000.
//						.setInteger("-maxhmmpf", 3000)
						// pl_window specifies lookahead distance in frames. Typical values are
						// from 0 (don't use lookahead) to 10 (decode 10 frames ahead). Bigger
						// values give faster decoding but reduced accuracy.
						.setInteger("-pl_window", 0)
						// voice volume threshold
						.setFloat("-vad_threshold", threshold)
						// add keywords
//						.setString("-kws", new File(assetsDir, "keywords.list").getPath())
						.getRecognizer();
		speechRecognizer.addListener(this);

		/** In your application you might not need to add all those searches.
		 * They are added here for demonstration. You can leave just one.
		 */

		// Add key phrase searches
//		File file = new File(assetsDir, "keywords.list");
//		speechRecognizer.addKeywordSearch("input", file);
//		speechRecognizer.addKeyphraseSearch("good", KEY_GOOD);
//		speechRecognizer.addKeyphraseSearch("normal", KEY_NORMAL);
//		speechRecognizer.addKeyphraseSearch("bad", KEY_BAD);

//		 Create grammar-based search for input recognition
		File inputGrammar = new File(assetsDir, "input.gram");
		speechRecognizer.addGrammarSearch(Constants.SPEECH_INPUT, inputGrammar);

//		 Create grammar-based search for validation recognition
		File validationGrammar = new File(assetsDir, "validation.gram");
		speechRecognizer.addGrammarSearch(Constants.SPEECH_VALIDATION, validationGrammar);
	}

	public void startListening(final String mode, FragmentSRDialog dialog, ImageView speechImage) {
		log.debug("startListening(): mode = " + mode);
		this.dialog = dialog;
		this.mode = mode;
		this.speechImage = speechImage;
		Animation animation = AnimationUtils.loadAnimation(osmandApplication, R.anim.blink);
		speechImage.startAnimation(animation);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (speechRecognizer == null) {
					try {
						wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				log.debug("startListening(): speechRecognizer ready, listening for mode " +
						SRPocketSphinx.this.mode);
				// start listening with 1s delay
				speechRecognizer.startListening(SRPocketSphinx.this.mode);//, 1);
			}
		}).start();
	}

	public void stopListening() {
		log.debug("stopListening()");
		speechRecognizer.cancel();
	}

	@Override
	public void onBeginningOfSpeech() {
		log.debug("beginOfSpeech()");
		//		speechProgress.setIndeterminate(false);
		//		speechProgress.setMax(10);
	}

	@Override
	public void onEndOfSpeech() {
		log.debug("endOfSpeech()");
		//		speechProgress.setIndeterminate(true);
	}

	@Override
	public void onPartialResult(Hypothesis hypothesis) {
		if (hypothesis != null) {
			debug = "onPartialResult(): \npartial = " + hypothesis.getHypstr() + "\nprobability =" +
					" " + hypothesis.getProb() + "\nbestScore = " + hypothesis.getBestScore();
			log.debug(debug);

//			if (hypothesis.getHypstr().equals(KEY_GOOD) || hypothesis.getHypstr().equals
//					(KEY_NORMAL) || hypothesis.getHypstr().equals(KEY_BAD)) {
//				speechRecognizer.stop();
//			}
			if (mode.equals(Constants.SPEECH_INPUT)) {
				if (Constants.SPEECH_INPUT_ALL.toString()
						.matches(".*\\b" + hypothesis.getHypstr() + "\\b.*")) {
					speechRecognizer.stop();
				}
			} else {
				if (Constants.SPEECH_VALIDATION_ALL.toString()
						.matches(".*\\b" + hypothesis.getHypstr() + "\\b.*")) {
					speechRecognizer.stop();
				}
			}
		}
	}

	@Override
	public void onResult(Hypothesis hypothesis) {
		if (hypothesis != null) {
			String result = hypothesis.getHypstr();
			debug = "onResult(): \nresult = " + hypothesis.getHypstr() + "\nprobability =" +
					" " + hypothesis.getProb() + "\nbestScore = " + hypothesis.getBestScore();
			log.debug(debug);
			speechImage.clearAnimation();
			stopListening();
			dialog.setResult(mode, result);
		}
	}

	@Override
	public void onError(Exception e) {
		log.error("onError(): error = " + e.toString());
	}

	@Override
	public void onTimeout() {
		speechRecognizer.cancel();
	}

}
