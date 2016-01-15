package net.osmand.plus.stressreduction.database;

import android.content.Context;
import android.view.View;

import net.osmand.PlatformUtil;
import net.osmand.plus.R;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.fragments.FragmentSRDialog;
import net.osmand.plus.stressreduction.tools.Calculation;

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
		setTimestampLastStressValue(Calculation.getCurrentDateTime());
	}

	public void initDatabase() {
		sqLiteLogger.onCreate(sqLiteLogger.getWritableDatabase());
	}

	public void writeUserToDatabase() {
		sqLiteLogger.insertUser();
	}

	public void writeLocationInfoToDatabase(LocationInfo locationInfo) {
		sqLiteLogger.insertLocationInfo(locationInfo);
	}

	public void writeSegmentInfoToDatabase(SegmentInfo segmentInfo) {
		sqLiteLogger.insertOSMSegment(segmentInfo.getRouteDataObject());
		sqLiteLogger.insertSegmentInfo(segmentInfo);
	}

	public void writeAppLogToDatabase(String timestamp) {
		sqLiteLogger.insertAppLog(timestamp);
	}

	public void writeRoutingLogToDatabase(RoutingLog routingLog) {
		log.info("writeRoutingLogToDatabase(): usedSrRoute="+routingLog.getUsedSrRoute());
		log.info("writeRoutingLogToDatabase(): usedSrLevel="+routingLog.getUsedSrLevel());
		log.info("writeRoutingLogToDatabase(): distSr="+routingLog.getDistanceSrRoute());
		log.info("writeRoutingLogToDatabase(): distNormal="+routingLog.getDistanceNormalRoute());
		log.info("writeRoutingLogToDatabase(): timeSr="+routingLog.getTimeSrRoute());
		log.info("writeRoutingLogToDatabase(): timeNormal="+routingLog.getTimeNormalRoute());
		sqLiteLogger.insertRoutingLog(routingLog);
	}

	private void updateStressValueInDatabase(int stressValue, String endTimestamp) {
		sqLiteLogger.updateStressValueInSegmentInfos(getTimestampLastStressValue(), stressValue);
		setTimestampLastStressValue(endTimestamp);
	}

	@Override
	public void onSRButtonClick(View view, String timestamp) {
		int id = view.getId();
		int stressValue = -99;
		switch (id) {
			case R.id.buttonHappy:
				log.debug("onSRButtonClick(): Face Happy clicked");
				stressValue = Constants.STRESS_VALUE_LOW;
				break;
			case R.id.buttonNeutral:
				log.debug("onSRButtonClick(): Face Neutral clicked");
				stressValue = Constants.STRESS_VALUE_MEDIUM;
				break;
			case R.id.buttonSad:
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
