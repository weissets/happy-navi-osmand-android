package net.osmand.plus.stressreduction.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import net.osmand.PlatformUtil;
import net.osmand.plus.R;
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
	private String timestampLastStressValue;

	public DataHandler(Context context) {
		sqLiteLogger = SQLiteLogger.getSQLiteLogger(context);
		if (sqLiteLogger == null) {
			log.error("DataHandler(): sqLiteLogger is NULL");
		}

		timestampLastStressValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
				.format(new java.util.Date());
	}

	public void writeLocationInfoToDatabase(LocationInfo locationInfo) {
		sqLiteLogger.insertLocationInfo(locationInfo);
	}

	public void writeSegmentInfoToDatabase(SegmentInfo segmentInfo) {
		sqLiteLogger.insertSegmentInfo(segmentInfo);
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
				stressValue = 2;
				break;
			case R.id.imageButtonFaceNeutral:
				log.debug("onSRButtonClick(): Face Neutral clicked");
				stressValue = 1;
				break;
			case R.id.imageButtonFaceSad:
				log.debug("onSRButtonClick(): Face Sad clicked");
				stressValue = 0;
				break;
		}
		updateStressValueInDatabase(stressValue, timestamp);
	}

	public String getTimestampLastStressValue() {
		return timestampLastStressValue;
	}

	private void setTimestampLastStressValue(String timestamp) {
		timestampLastStressValue = timestamp;
	}

}
