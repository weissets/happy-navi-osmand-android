package net.osmand.plus.stressreduction.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.osmand.PlatformUtil;
import net.osmand.binary.RouteDataObject;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;
import net.osmand.plus.stressreduction.tools.Calculation;
import net.osmand.plus.stressreduction.tools.SRSharedPreferences;

import org.apache.commons.logging.Log;

/**
 * This class manages the sqlite database
 *
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 *
 * @author Tobias
 */
public class SQLiteLogger extends SQLiteOpenHelper {

	private static final Log log = PlatformUtil.getLog(SQLiteLogger.class);

	private static SQLiteLogger sqLiteLogger;
	private int userPk;
	private OsmandApplication osmandApplication;

	private SQLiteLogger(Context context) {
		super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
		osmandApplication = (OsmandApplication) context.getApplicationContext();
		userPk = 1;
	}

	public static synchronized SQLiteLogger getSQLiteLogger(Context context) {
		if (sqLiteLogger == null) {
			sqLiteLogger = new SQLiteLogger(context);
		}
		return sqLiteLogger;
	}

	public static boolean hasNewSegments() {
		Cursor cursor = sqLiteLogger.getReadableDatabase().rawQuery(
				"SELECT " + Constants.TIMESTAMP + " FROM " + Constants.SEGMENTS + " WHERE " +
						"\"" + Constants.TIMESTAMP + "\" > \"" +
						DataHandler.getTimestampLastStressValue() + "\"", null);
		int count = cursor.getCount();
		cursor.close();
		return count > 0;
	}

	/**
	 * Check if there are segment or location entries in the database
	 *
	 * @return true if there are entries in the database, otherwise false
	 */
	public static boolean checkForData() {
		Cursor cursor1 = sqLiteLogger.getReadableDatabase()
				.rawQuery("SELECT * FROM " + Constants.SEGMENTS, null);
		Cursor cursor2 = sqLiteLogger.getReadableDatabase()
				.rawQuery("SELECT * FROM " + Constants.LOCATIONS, null);
		boolean hasData = cursor1.getCount() > 0 || cursor2.getCount() > 0;
		log.debug("checkForData(): found " + cursor1.getCount() + " segments and " +
				cursor2.getCount() + " locations in database");
		cursor1.close();
		cursor2.close();
		return hasData;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Constants.CREATE_TABLE_USERS);
		db.execSQL(Constants.CREATE_TABLE_SEGMENTS);
		db.execSQL(Constants.CREATE_TABLE_OSM_SEGMENTS);
		db.execSQL(Constants.CREATE_TABLE_LOCATIONS);
		db.execSQL(Constants.CREATE_TABLE_APP_LOGS);
		db.execSQL(Constants.CREATE_TABLE_ROUTING_LOGS);
		log.debug("onCreate(): Path to database is: " + db.getPath());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		log.warn("onUpgrade(): Upgrading database from version " + oldVersion + " to " +
				newVersion + ", which will recreate the tables");
		db.execSQL("DROP TABLE IF EXISTS " + Constants.USERS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.OSM_SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.APP_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.ROUTING_LOGS);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		log.warn("onDowngrade(): Downgrading database from version " + oldVersion + " to " +
				newVersion + ", which will recreate the tables");
		db.execSQL("DROP TABLE IF EXISTS " + Constants.USERS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.OSM_SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.APP_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.ROUTING_LOGS);
		onCreate(db);
	}

	public void clearDatabase() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + Constants.USERS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.OSM_SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.APP_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.ROUTING_LOGS);
		onCreate(db);
	}

	public void insertUser() {
		String[] userInfo = SRSharedPreferences.getUserInfo(osmandApplication);
		log.debug("insertUser(): inserting " + StressReductionPlugin.getUUID() + ", " +
				userInfo[0] + ", " + userInfo[1] + ", " + userInfo[2]);
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ID, StressReductionPlugin.getUUID());
		contentValues.put(Constants.USER_GENDER, userInfo[0]);
		contentValues.put(Constants.USER_AGE, Integer.valueOf(userInfo[1]));
		contentValues.put(Constants.USER_CAR, userInfo[2]);
		getWritableDatabase().insertWithOnConflict(Constants.USERS, null, contentValues,
				SQLiteDatabase.CONFLICT_REPLACE);
	}

	public void insertSegmentInfo(SegmentInfo segmentInfo) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.OSM_SEGMENT_ID, segmentInfo.getId());
		contentValues.put(Constants.AVG_SPEED, segmentInfo.getAverageSpeed());
		contentValues.put(Constants.STRESS_VALUE, segmentInfo.getStressValue());
		contentValues.put(Constants.TIMESTAMP, segmentInfo.getTimestamp());
		contentValues.put(Constants.USER_PK, userPk);

		getWritableDatabase().insert(Constants.SEGMENTS, null, contentValues);
	}

	public void insertOSMSegment(RouteDataObject routeDataObject) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.ID, routeDataObject.getId());
		contentValues.put(Constants.NAME, routeDataObject.getName());
		contentValues.put(Constants.HIGHWAY, routeDataObject.getHighway());
		contentValues.put(Constants.LANES, routeDataObject.getLanes());
		// true should be forward direction, see waypoint helper
		contentValues.put(Constants.MAX_SPEED,
				Calculation.convertMsToKmh(routeDataObject.getMaximumSpeed(true)));
		contentValues.put(Constants.ONEWAY, routeDataObject.getOneway());

		getWritableDatabase().insertWithOnConflict(Constants.OSM_SEGMENTS, null, contentValues,
				SQLiteDatabase.CONFLICT_IGNORE);
	}

	public void updateStressValueInSegmentInfos(String startTimestamp, int stressValue) {
		ContentValues contentValues = new ContentValues(1);
		contentValues.put(Constants.STRESS_VALUE, stressValue);
		int updatedRows = getWritableDatabase().update(Constants.SEGMENTS, contentValues,
				"\"" + Constants.TIMESTAMP + "\" > \"" + startTimestamp + "\"", null);
		log.debug("updateStressValueInSegmentInfos(): updated " + updatedRows + " rows with " +
				"stress value = " + stressValue);
	}

	public void insertLocationInfo(LocationInfo locationInfo) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.LATITUDE, locationInfo.getLatitude());
		contentValues.put(Constants.LONGITUDE, locationInfo.getLongitude());
		contentValues.put(Constants.SPEED, locationInfo.getSpeed());
		contentValues.put(Constants.ACC_X, locationInfo.getAccelerationX());
		contentValues.put(Constants.ACC_Y, locationInfo.getAccelerationY());
		contentValues.put(Constants.ACC_Z, locationInfo.getAccelerationZ());
		contentValues.put(Constants.DIRECTION, locationInfo.getDirection());
		contentValues.put(Constants.TIMESTAMP, locationInfo.getTimestamp());
		contentValues.put(Constants.USER_PK, userPk);

		getWritableDatabase().insert(Constants.LOCATIONS, null, contentValues);
	}

	public void insertAppLog(String timestamp) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.TIME_APP_OPEN, timestamp);
		contentValues.put(Constants.USER_PK, userPk);

		getWritableDatabase().insert(Constants.APP_LOGS, null, contentValues);
	}

	public void insertRoutingLog(RoutingLog routingLog) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.START_LAT, routingLog.getStartLat());
		contentValues.put(Constants.START_LON, routingLog.getStartLon());
		contentValues.put(Constants.END_LAT, routingLog.getEndLat());
		contentValues.put(Constants.END_LON, routingLog.getEndLon());
		contentValues.put(Constants.ABORT_LAT, routingLog.getAbortLat());
		contentValues.put(Constants.ABORT_LON, routingLog.getAbortLon());
		contentValues.put(Constants.TIME_ROUTING_START, routingLog.getTimeRoutingStart());
		contentValues.put(Constants.TIME_ROUTING_END_CALC, routingLog.getTimeRoutingEndCalc());
		contentValues.put(Constants.TIME_ROUTING_END, routingLog.getTimeRoutingEnd());
		contentValues.put(Constants.TIME_ROUTING_ABORT, routingLog.getTimeRoutingAbort());
		contentValues.put(Constants.DISTANCE_TO_END, routingLog.getDistanceToEnd());
		contentValues.put(Constants.USED_SR_ROUTE, (routingLog.getUsedSrRoute() ? 1 : 0));
		contentValues.put(Constants.USED_SR_LEVEL, routingLog.getUsedSrLevel());
		contentValues.put(Constants.DISTANCE_SR_ROUTE, routingLog.getDistanceSrRoute());
		contentValues.put(Constants.DISTANCE_NORMAL_ROUTE, routingLog.getDistanceNormalRoute());
		contentValues.put(Constants.TIME_SR_ROUTE, routingLog.getTimeSrRoute());
		contentValues.put(Constants.TIME_NORMAL_ROUTE, routingLog.getTimeNormalRoute());
		contentValues.put(Constants.USER_PK, userPk);

		getWritableDatabase().insert(Constants.ROUTING_LOGS, null, contentValues);
	}

	public void insertUserInfo(String gender, String age, String car) {
		SRSharedPreferences.setUserInfo(osmandApplication, gender, age, car);
		insertUser();
	}
}
