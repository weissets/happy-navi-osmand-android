package net.osmand.plus.stressreduction.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import net.osmand.PlatformUtil;
import net.osmand.plus.R;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.fragments.FragmentSRDialog;

import org.apache.commons.logging.Log;

/**
 * This class handles the data for the database
 *
 * @author Tobias
 */
public class DataHandler implements FragmentSRDialog.SRDialogButtonClickListener {

	private static final Log log = PlatformUtil.getLog(DataHandler.class);

	private final SQLiteLogger sqLiteLogger;
	private static String timestampLastStressValue;

	public DataHandler(Context context) {
		sqLiteLogger = SQLiteLogger.getSQLiteLogger(context);
		if (sqLiteLogger == null) {
			log.error("DataHandler(): sqLiteLogger is NULL");
		}

		timestampLastStressValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
				.format(new java.util.Date());
	}

	public void writeUserToDatabase() {
		sqLiteLogger.insertUser();
	}

	public void writeLocationInfoToDatabase(LocationInfo locationInfo) {
		sqLiteLogger.insertLocationInfo(locationInfo);
	}

	public void writeSegmentInfoToDatabase(SegmentInfo segmentInfo) {
		sqLiteLogger.insertSegmentInfo(segmentInfo);
	}

	public void writeAppLogToDatabase(String timestamp) {
		sqLiteLogger.insertAppLog(timestamp);
	}

	public void writeRoutingLogToDatabase(RoutingLog routingLog) {
		sqLiteLogger.insertRoutingLog(routingLog);
	}

	private void updateStressValueInDatabase(int stressValue, String endTimestamp) {
		sqLiteLogger.updateStressValueInSegmentInfos(getTimestampLastStressValue(), endTimestamp,
				stressValue);
		setTimestampLastStressValue(endTimestamp);
	}

	@Override
	public void onSRButtonClick(View view, String timestamp) {
		int id = view.getId();
		int stressValue = -99;
		switch (id) {
			case R.id.imageButtonFaceHappy:
				log.debug("onSRButtonClick(): Face Happy clicked");
				stressValue = Constants.STRESS_VALUE_LOW;
				break;
			case R.id.imageButtonFaceNeutral:
				log.debug("onSRButtonClick(): Face Neutral clicked");
				stressValue = Constants.STRESS_VALUE_MEDIUM;
				break;
			case R.id.imageButtonFaceSad:
				log.debug("onSRButtonClick(): Face Sad clicked");
				stressValue = Constants.STRESS_VALUE_HIGH;
				break;
		}
		updateStressValueInDatabase(stressValue, timestamp);
	}

	public static String getTimestampLastStressValue() {
		return timestampLastStressValue;
	}

	public static void setTimestampLastStressValue(String timestamp) {
		timestampLastStressValue = timestamp;
	}

}
