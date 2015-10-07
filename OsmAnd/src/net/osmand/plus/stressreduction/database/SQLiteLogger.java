package net.osmand.plus.stressreduction.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.osmand.PlatformUtil;
import net.osmand.binary.RouteDataObject;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;

import org.apache.commons.logging.Log;

/**
 * This class manages the sqlite database
 *
 * @author Tobias
 */
public class SQLiteLogger extends SQLiteOpenHelper {

//	private static final Log log = PlatformUtil.getLog(SQLiteLogger.class);
//
//	private static final String DATABASE_NAME = "stress_reduction.db";
//	private static final String DATABASE_SEGMENT_CREATE =
//			"create table " + Constants.TABLE_SEGMENT_INFOS + " ("
//					//			+ Constants.COLUMN_SEGMENT_COUNT
//					//			+ " integer primary key, "		#automatically assigned
//					+ Constants.COLUMN_PHONE_ID + " text, " + Constants.COLUMN_SEGMENT_ID +
//					" integer, " + Constants.COLUMN_SEGMENT_NAME + " text, " +
//					Constants.COLUMN_SEGMENT_HIGHWAY + " text, " + Constants.COLUMN_SEGMENT_LANES +
//					" integer, " + Constants.COLUMN_SEGMENT_MAXSPEED + " integer, " +
//					Constants.COLUMN_SEGMENT_AVERAGESPEED + " integer, " +
//					Constants.COLUMN_SEGMENT_CURRENTSPEED + " double, " +
//					Constants.COLUMN_SEGMENT_ONEWAY + " integer, " +
//					Constants.COLUMN_SEGMENT_REFERENCES + " text, " +
//					Constants.COLUMN_SEGMENT_ROUTE + " text, "
//					+ Constants.COLUMN_SEGMENT_TIMESTAMP + " text, " +
//					Constants.COLUMN_SEGMENT_STRESSFUL + " integer);";
//	private static final String DATABASE_LOCATION_CREATE =
//			"create table " + Constants.TABLE_LOCATION_INFOS + " ("
//					//			+ Constants.COLUMN_LOCATION_COUNT
//					//			+ " integer primary key, "      #automatically assigned
//					+ Constants.COLUMN_PHONE_ID + " text, " + Constants.COLUMN_LOCATION_LATITUDE +
//					" double, " + Constants.COLUMN_LOCATION_LONGITUDE + " double, " +
//					Constants.COLUMN_LOCATION_CURRENTSPEED + " double, " +
//					Constants.COLUMN_LOCATION_ACCELERATION_X + " double, " +
//					Constants.COLUMN_LOCATION_ACCELERATION_Y + " double, " +
//					Constants.COLUMN_LOCATION_ACCELERATION_Z + " double, " +
//					Constants.COLUMN_LOCATION_DIRECTION + " double, " +
//					Constants.COLUMN_LOCATION_TIMESTAMP + " text);";
////	private static final String DATABASE_TIMES_CREATE = "create table " + Constants
////			.TABLE_TIMES + " (" +
//	private static SQLiteLogger sqLiteLogger;
//
//	private SQLiteLogger(Context context) {
//		super(context, DATABASE_NAME, null, 1);
//	}
//
//	public static synchronized SQLiteLogger getSQLiteLogger(Context context) {
//		if (sqLiteLogger == null) {
//			sqLiteLogger = new SQLiteLogger(context);
//		}
//		return sqLiteLogger;
//	}
//
//	public static int getDatabaseSizeSegments() {
//		Cursor cursor = sqLiteLogger.getReadableDatabase()
//				.rawQuery("SELECT * FROM " + Constants.TABLE_SEGMENT_INFOS, null);
//		int size = cursor.getCount();
//		cursor.close();
//		return size;
//	}
//
//	public static int getDatabaseSizeSegmentsSinceLastStressValue(String lastStressValueTimestamp) {
//		Cursor cursor = sqLiteLogger.getReadableDatabase()
//				.rawQuery("SELECT " + Constants.COLUMN_SEGMENT_TIMESTAMP + " FROM " +
//						Constants.TABLE_SEGMENT_INFOS + " WHERE " + "\"" +
//						Constants.COLUMN_SEGMENT_TIMESTAMP + "\" > \"" + lastStressValueTimestamp +
//						"\"", null);
//		int count = cursor.getCount();
//		cursor.close();
//		return count;
//	}
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(DATABASE_SEGMENT_CREATE);
//		db.execSQL(DATABASE_LOCATION_CREATE);
//		log.debug("onCreate(): Path to database is: " + db.getPath());
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		log.warn("onUpgrade(): Upgrading database from version " + oldVersion + " to " +
//				newVersion +
//				", which will destroy all old data");
//		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SEGMENT_INFOS);
//		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_LOCATION_INFOS);
//		onCreate(db);
//	}
//
//	public void clearDatabase() {
//		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//		if (sqLiteDatabase != null) {
//			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SEGMENT_INFOS);
//			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_LOCATION_INFOS);
//			onCreate(sqLiteDatabase);
//		} else {
//			log.error("clearDatabase(): ERROR could not drop tables, database is NULL");
//		}
//	}
//
//	public void insertSegmentInfo(SegmentInfo segmentInfo) {
//		ContentValues contentValues = new ContentValues();
//
//		contentValues.put(Constants.COLUMN_PHONE_ID, StressReductionPlugin.getUUID());
//		contentValues.put(Constants.COLUMN_SEGMENT_ID, segmentInfo.getId());
//		contentValues.put(Constants.COLUMN_SEGMENT_NAME, segmentInfo.getName());
//		contentValues.put(Constants.COLUMN_SEGMENT_HIGHWAY, segmentInfo.getHighway());
//		contentValues.put(Constants.COLUMN_SEGMENT_LANES, segmentInfo.getLanes());
//		contentValues.put(Constants.COLUMN_SEGMENT_MAXSPEED,
//				Math.round(segmentInfo.getMaximumSpeed(true) * // TODO test new rule for maxspeed!
//						Constants.MS_TO_KMH));
//		contentValues.put(Constants.COLUMN_SEGMENT_AVERAGESPEED, segmentInfo.getAverageSpeed());
//		contentValues.put(Constants.COLUMN_SEGMENT_ONEWAY, segmentInfo.getOneway());
//		contentValues.put(Constants.COLUMN_SEGMENT_REFERENCES, segmentInfo.getRef());
//		contentValues.put(Constants.COLUMN_SEGMENT_ROUTE, segmentInfo.getRoute());
//		contentValues.put(Constants.COLUMN_SEGMENT_TIMESTAMP, segmentInfo.getTimestamp());
//		contentValues.put(Constants.COLUMN_SEGMENT_STRESSFUL, segmentInfo.getStressValue());
//
//		getWritableDatabase().insert(Constants.TABLE_SEGMENT_INFOS, null, contentValues);
//
////		log.debug("insertSegmentInfo(): Added Segment Info to Database");
//	}
//
//	public void updateStressValueInSegmentInfos(String startTimestamp, String endTimestamp,
//	                                            int stressValue) {
//		ContentValues contentValues = new ContentValues(1);
//		contentValues.put(Constants.COLUMN_SEGMENT_STRESSFUL, stressValue);
//		int updatedRows = getWritableDatabase().update(Constants.TABLE_SEGMENT_INFOS,
//				contentValues,
//				"\"" + Constants.COLUMN_SEGMENT_TIMESTAMP + "\" > \"" + startTimestamp +
//						"\" AND \"" + Constants.COLUMN_SEGMENT_TIMESTAMP + "\" < \"" +
//						endTimestamp + "\"", null);
//		log.debug("updateStressValueInSegmentInfos(): updated " + updatedRows + " rows!");
//	}
//
//	public void insertLocationInfo(LocationInfo locationInfo) {
//		ContentValues contentValues = new ContentValues();
//
//		contentValues.put(Constants.COLUMN_PHONE_ID, StressReductionPlugin.getUUID());
//		contentValues.put(Constants.COLUMN_LOCATION_LATITUDE, locationInfo.getLatitude());
//		contentValues.put(Constants.COLUMN_LOCATION_LONGITUDE, locationInfo.getLongitude());
//		contentValues.put(Constants.COLUMN_LOCATION_CURRENTSPEED, locationInfo.getSpeed());
//		contentValues
//				.put(Constants.COLUMN_LOCATION_ACCELERATION_X, locationInfo.getAccelerationX());
//		contentValues
//				.put(Constants.COLUMN_LOCATION_ACCELERATION_Y, locationInfo.getAccelerationY());
//		contentValues
//				.put(Constants.COLUMN_LOCATION_ACCELERATION_Z, locationInfo.getAccelerationZ());
//		contentValues.put(Constants.COLUMN_LOCATION_DIRECTION, locationInfo.getDirection());
//		contentValues.put(Constants.COLUMN_LOCATION_TIMESTAMP, locationInfo.getTimestamp());
//
//		getWritableDatabase().insert(Constants.TABLE_LOCATION_INFOS, null, contentValues);
//
//		// log.debug("insertLocationInfo(): Added Location Info to Database");
//	}

	private static final Log log = PlatformUtil.getLog(SQLiteLogger.class);

	private static SQLiteLogger sqLiteLogger;

	private SQLiteLogger(Context context) {
		super(context, Constants.DATABASE_NAME, null, 1);
	}

	public static synchronized SQLiteLogger getSQLiteLogger(Context context) {
		if (sqLiteLogger == null) {
			sqLiteLogger = new SQLiteLogger(context);
		}
		return sqLiteLogger;
	}

	public static int getDatabaseSizeSegments() {
		Cursor cursor = sqLiteLogger.getReadableDatabase()
				.rawQuery("SELECT * FROM " + Constants.SEGMENTS, null);
		int size = cursor.getCount();
		cursor.close();
		return size;
	}

	public static int getDatabaseSizeSegmentsSinceLastStressValue(String lastStressValueTimestamp) {
		Cursor cursor = sqLiteLogger.getReadableDatabase()
				.rawQuery("SELECT " + Constants.TIMESTAMP + " FROM " +
						Constants.SEGMENTS + " WHERE " + "\"" +
						Constants.TIMESTAMP + "\" > \"" + lastStressValueTimestamp +
						"\"", null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * Check if there are segment or location entries in the database
	 *
	 * @return true if there are entries in the database, otherwise false
	 */
	public static boolean checkForData() {
		Cursor cursor1 = sqLiteLogger.getReadableDatabase().rawQuery("SELECT * FROM " + Constants
				.SEGMENTS, null);
		Cursor cursor2 = sqLiteLogger.getReadableDatabase().rawQuery("SELECT * FROM " + Constants
				.LOCATIONS, null);
		boolean hasData = cursor1.getCount() > 0 || cursor2.getCount() > 0;
		log.debug("checkForData(): found " + cursor1.getCount() + " segments and " + cursor2
						.getCount() + " locations in database");
		cursor1.close();
		cursor2.close();
		return hasData;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Constants.CREATE_TABLE_USERS);
		db.execSQL(Constants.CREATE_TABLE_SEGMENTS);
		db.execSQL(Constants.CREATE_TABLE_LOCATIONS);
		db.execSQL(Constants.CREATE_TABLE_APP_LOGS);
		db.execSQL(Constants.CREATE_TABLE_ROUTING_LOGS);
		log.debug("onCreate(): Path to database is: " + db.getPath());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		log.warn("onUpgrade(): Upgrading database from version " + oldVersion + " to " +
				newVersion +
				", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Constants.USERS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.APP_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.ROUTING_LOGS);
		onCreate(db);
	}

	public void clearDatabase() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + Constants.USERS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.SEGMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.APP_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.ROUTING_LOGS);
		onCreate(db);
	}

	public void insertUser() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constants.ID, StressReductionPlugin.getUUID());
		getWritableDatabase().insertWithOnConflict(Constants.USERS, null, contentValues,
				SQLiteDatabase.CONFLICT_IGNORE);
	}

	public void insertSegmentInfo(SegmentInfo segmentInfo) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.ID, segmentInfo.getId());
		contentValues.put(Constants.NAME, segmentInfo.getName());
		contentValues.put(Constants.HIGHWAY, segmentInfo.getHighway());
		contentValues.put(Constants.LANES, segmentInfo.getLanes());
		// true should be forward direction, see waypoint helper TODO testing
		contentValues.put(Constants.MAX_SPEED, segmentInfo.getMaximumSpeed(true));
		contentValues.put(Constants.ONEWAY, segmentInfo.getOneway());
		contentValues.put(Constants.REFERENCES, segmentInfo.getRef());
		contentValues.put(Constants.ROUTE, segmentInfo.getRoute());
		contentValues.put(Constants.AVG_SPEED, segmentInfo.getAverageSpeed());
		contentValues.put(Constants.STRESS_VALUE, segmentInfo.getStressValue());
		contentValues.put(Constants.TIMESTAMP, segmentInfo.getTimestamp());
		contentValues.put(Constants.USER_ID, StressReductionPlugin.getUUID());

		getWritableDatabase().insert(Constants.SEGMENTS, null, contentValues);
	}

	public void updateStressValueInSegmentInfos(String startTimestamp, String endTimestamp,
	                                            int stressValue) {
		ContentValues contentValues = new ContentValues(1);
		contentValues.put(Constants.STRESS_VALUE, stressValue);
		int updatedRows = getWritableDatabase().update(Constants.SEGMENTS,
				contentValues,
				"\"" + Constants.TIMESTAMP + "\" > \"" + startTimestamp +
						"\" AND \"" + Constants.TIMESTAMP + "\" < \"" +
						endTimestamp + "\"", null);
		log.debug("updateStressValueInSegmentInfos(): updated " + updatedRows + " rows!");
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
		contentValues.put(Constants.USER_ID, StressReductionPlugin.getUUID());

		getWritableDatabase().insert(Constants.LOCATIONS, null, contentValues);
	}

	public void insertAppLog(String timestamp) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.TIME_APP_OPEN, timestamp);
		contentValues.put(Constants.USER_ID, StressReductionPlugin.getUUID());

		getWritableDatabase().insert(Constants.APP_LOGS, null, contentValues);
	}

	public void insertRoutingLog(RoutingLog routingLog) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.START_LAT, routingLog.getStartLat());
		contentValues.put(Constants.START_LON, routingLog.getStartLon());
		contentValues.put(Constants.END_LAT, routingLog.getEndLat());
		contentValues.put(Constants.END_LON, routingLog.getEndLon());
		contentValues.put(Constants.TIME_ARRIVAL_PRE_CALC, routingLog.getTimeArrivalPreCalc());
		contentValues.put(Constants.TIME_ARRIVAL_REAL, routingLog.getTimeArrivalReal());
		contentValues.put(Constants.TIME_ROUTING_START, routingLog.getTimeRoutingStart());
		contentValues.put(Constants.TIME_ROUTING_END, routingLog.getTimeRoutingEnd());
		contentValues.put(Constants.USER_ID, StressReductionPlugin.getUUID());

		getWritableDatabase().insert(Constants.ROUTING_LOGS, null, contentValues);
	}
}
