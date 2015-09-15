package net.osmand.plus.stressreduction.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.osmand.PlatformUtil;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.StressReductionPlugin;

import org.apache.commons.logging.Log;

/**
 * This class manages the sqlite database
 *
 * @author Tobias
 */
public class SQLiteLogger extends SQLiteOpenHelper {

	private static final Log log = PlatformUtil.getLog(SQLiteLogger.class);

	private static final String DATABASE_NAME = "stress_reduction.db";
	private static final String DATABASE_SEGMENT_CREATE =
			"create table " + Constants.TABLE_SEGMENT_INFOS + " ("
					//			+ Constants.COLUMN_SEGMENT_COUNT
					//			+ " integer primary key, "		#automatically assigned
					+ Constants.COLUMN_PHONE_ID + " text, " + Constants.COLUMN_SEGMENT_ID +
					" integer, " + Constants.COLUMN_SEGMENT_NAME + " text, " +
					Constants.COLUMN_SEGMENT_HIGHWAY + " text, " + Constants.COLUMN_SEGMENT_LANES +
					" integer, " + Constants.COLUMN_SEGMENT_MAXSPEED + " integer, " +
					Constants.COLUMN_SEGMENT_AVERAGESPEED + " integer, " +
					Constants.COLUMN_SEGMENT_CURRENTSPEED + " double, " +
					Constants.COLUMN_SEGMENT_ONEWAY + " integer, " +
					Constants.COLUMN_SEGMENT_REFERENCES + " text, " +
					Constants.COLUMN_SEGMENT_ROUTE + " text, "
					+ Constants.COLUMN_SEGMENT_TIMESTAMP + " text, " +
					Constants.COLUMN_SEGMENT_STRESSFUL + " integer);";
	private static final String DATABASE_LOCATION_CREATE =
			"create table " + Constants.TABLE_LOCATION_INFOS + " ("
					//			+ Constants.COLUMN_LOCATION_COUNT
					//			+ " integer primary key, "      #automatically assigned
					+ Constants.COLUMN_PHONE_ID + " text, " + Constants.COLUMN_LOCATION_LATITUDE +
					" double, " + Constants.COLUMN_LOCATION_LONGITUDE + " double, " +
					Constants.COLUMN_LOCATION_CURRENTSPEED + " double, " +
					Constants.COLUMN_LOCATION_ACCELERATION_X + " double, " +
					Constants.COLUMN_LOCATION_ACCELERATION_Y + " double, " +
					Constants.COLUMN_LOCATION_ACCELERATION_Z + " double, " +
					Constants.COLUMN_LOCATION_DIRECTION + " double, " +
					Constants.COLUMN_LOCATION_TIMESTAMP + " text);";
//	private static final String DATABASE_TIMES_CREATE = "create table " + Constants
//			.TABLE_TIMES + " (" +
	private static SQLiteLogger sqLiteLogger;

	private SQLiteLogger(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	public static synchronized SQLiteLogger getSQLiteLogger(Context context) {
		if (sqLiteLogger == null) {
			sqLiteLogger = new SQLiteLogger(context);
		}
		return sqLiteLogger;
	}

	public static int getDatabaseSizeSegments() {
		Cursor cursor = sqLiteLogger.getReadableDatabase()
				.rawQuery("SELECT * FROM " + Constants.TABLE_SEGMENT_INFOS, null);
		int size = cursor.getCount();
		cursor.close();
		return size;
	}

	public static int getDatabaseSizeSinceLastStressValue(String lastStressValueTimestamp) {
		Cursor cursor = sqLiteLogger.getReadableDatabase()
				.rawQuery("SELECT " + Constants.COLUMN_SEGMENT_TIMESTAMP + " FROM " +
						Constants.TABLE_SEGMENT_INFOS + " WHERE " + "\"" +
						Constants.COLUMN_SEGMENT_TIMESTAMP + "\" > \"" + lastStressValueTimestamp +
						"\"", null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_SEGMENT_CREATE);
		db.execSQL(DATABASE_LOCATION_CREATE);
		log.debug("onCreate(): Path to database is: " + db.getPath());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		log.warn("onUpgrade(): Upgrading database from version " + oldVersion + " to " +
				newVersion +
				", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SEGMENT_INFOS);
		db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_LOCATION_INFOS);
		onCreate(db);
	}

	public void clearDatabase() {
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		if (sqLiteDatabase != null) {
			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SEGMENT_INFOS);
			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_LOCATION_INFOS);
			onCreate(sqLiteDatabase);
		} else {
			log.error("clearDatabase(): ERROR could not drop tables, database is NULL");
		}
	}

	public void insertSegmentInfo(SegmentInfo segmentInfo) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.COLUMN_PHONE_ID, StressReductionPlugin.getUUID());
		contentValues.put(Constants.COLUMN_SEGMENT_ID, segmentInfo.getId());
		contentValues.put(Constants.COLUMN_SEGMENT_NAME, segmentInfo.getName());
		contentValues.put(Constants.COLUMN_SEGMENT_HIGHWAY, segmentInfo.getHighway());
		contentValues.put(Constants.COLUMN_SEGMENT_LANES, segmentInfo.getLanes());
		contentValues.put(Constants.COLUMN_SEGMENT_MAXSPEED,
				Math.round(segmentInfo.getMaximumSpeed() * Constants.MS_TO_KMH));
		contentValues.put(Constants.COLUMN_SEGMENT_AVERAGESPEED, segmentInfo.getAverageSpeed());
		contentValues.put(Constants.COLUMN_SEGMENT_ONEWAY, segmentInfo.getOneway());
		contentValues.put(Constants.COLUMN_SEGMENT_REFERENCES, segmentInfo.getRef());
		contentValues.put(Constants.COLUMN_SEGMENT_ROUTE, segmentInfo.getRoute());
		contentValues.put(Constants.COLUMN_SEGMENT_TIMESTAMP, segmentInfo.getTimestamp());
		contentValues.put(Constants.COLUMN_SEGMENT_STRESSFUL, segmentInfo.getStressValue());

		getWritableDatabase().insert(Constants.TABLE_SEGMENT_INFOS, null, contentValues);

//		log.debug("insertSegmentInfo(): Added Segment Info to Database");
	}

	public void updateStressValueInSegmentInfos(String startTimestamp, String endTimestamp,
	                                            int stressValue) {
		ContentValues contentValues = new ContentValues(1);
		contentValues.put(Constants.COLUMN_SEGMENT_STRESSFUL, stressValue);
		int updatedRows = getWritableDatabase().update(Constants.TABLE_SEGMENT_INFOS,
				contentValues,
				"\"" + Constants.COLUMN_SEGMENT_TIMESTAMP + "\" > \"" + startTimestamp +
						"\" AND \"" + Constants.COLUMN_SEGMENT_TIMESTAMP + "\" < \"" +
						endTimestamp + "\"", null);
		log.debug("updateStressValueInSegmentInfos(): updated " + updatedRows + " rows!");
	}

	public void insertLocationInfo(LocationInfo locationInfo) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(Constants.COLUMN_PHONE_ID, StressReductionPlugin.getUUID());
		contentValues.put(Constants.COLUMN_LOCATION_LATITUDE, locationInfo.getLatitude());
		contentValues.put(Constants.COLUMN_LOCATION_LONGITUDE, locationInfo.getLongitude());
		contentValues.put(Constants.COLUMN_LOCATION_CURRENTSPEED, locationInfo.getSpeed());
		contentValues
				.put(Constants.COLUMN_LOCATION_ACCELERATION_X, locationInfo.getAccelerationX());
		contentValues
				.put(Constants.COLUMN_LOCATION_ACCELERATION_Y, locationInfo.getAccelerationY());
		contentValues
				.put(Constants.COLUMN_LOCATION_ACCELERATION_Z, locationInfo.getAccelerationZ());
		contentValues.put(Constants.COLUMN_LOCATION_DIRECTION, locationInfo.getDirection());
		contentValues.put(Constants.COLUMN_LOCATION_TIMESTAMP, locationInfo.getTimestamp());

		getWritableDatabase().insert(Constants.TABLE_LOCATION_INFOS, null, contentValues);

		// log.debug("insertLocationInfo(): Added Location Info to Database");
	}
}
