package net.osmand.plus.stressreduction;

/**
 * This class contains constants used by the stress reduction plugin
 *
 * @author Tobias
 */
public class Constants {

	/** id string of the plugin */
	public static final String PLUGIN_ID = "net.osmand.plus.stressreduction.plugin";

	/** version code of the info dialog */
	private static final String INFO_VERSION_CODE = "1.0";

	/** version string of the info dialog */
	public static final String INFO_VERSION = "info_" + INFO_VERSION_CODE;

	/** version code of the what's new dialog */
	private static final String WHATS_NEW_VERSION_CODE = "1.3";

	/** version string of the what's new dialog */
	public static final String WHATS_NEW_VERSION = "whats_new_" + WHATS_NEW_VERSION_CODE;

	/** identifier for the location simulation dialog fragment */
	public static final String FRAGMENT_LOCATION_SIMULATION = "fragment_location_simulation";

	/** identifier for the stress reduction dialog fragment */
	public static final String FRAGMENT_SR_DIALOG = "fragment_sr_dialog";

	/** identifier for the info dialog fragment */
	public static final String FRAGMENT_INFO_DIALOG = "fragment_info_dialog";

	/** identifier for the what's new dialog fragment */
	public static final String FRAGMENT_WHATS_NEW_DIALOG = "fragment_whats_new_dialog";

	/** identifier for the new version dialog fragment */
	public static final String FRAGMENT_NEW_VERSION_DIALOG = "fragment_new_version_dialog";

	/** identifier if data was correct uploaded */
	public static final String DATA_UPLOADED_CORRECTLY = "data_uploaded_correctly";

	/** identifier for the last wifi receive */
	public static final String LAST_WIFI_RECEIVE = "last_wifi_receive";

	/** value of the wifi receiver timeout */
	public static final long WIFI_RECEIVER_TIMEOUT = 20000;

	/** identifier if upload is done from wifi receiver */
	public static final String WIFI_RECEIVER_UPLOAD = "wifi_receiver_upload";

	/** identifier for the upload mode regular */
	public static final String UPLOAD_MODE_WIFI = "upload_mode_wifi";

	/** identifier for the upload mode mobile */
	public static final String UPLOAD_MODE_MOBILE = "upload_mode_mobile";

	/** identifier for the upload mode after app was incorrect closed */
	public static final String UPLOAD_MODE_INCORRECT_CLOSE = "upload_mode_incorrect_close";

	/** value of the lowest stress value */
	public static final int STRESS_VALUE_LOW = 2;

	/** value of the medium stress value */
	public static final int STRESS_VALUE_MEDIUM = 1;

	/** value of the highest stress value */
	public static final int STRESS_VALUE_HIGH = 0;

	/** uri of the database upload script */
	public static final String URI_DATABASE_UPLOAD =
			"http://maps.hcilab.org/stressreduction/db_upload.php";

	/** uri of the database upload script (DEBUG EMULATOR) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_EMULATOR =
			"http://10.0.2.2:8888/db_upload_debug.php";

	/** uri of the database upload script (DEBUG DEVICE) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_DEVICE =
			"http://192.168.2.117:8888/db_upload_debug.php";

	/** uri of the database download script */
	public static final String URI_DATABASE_DOWNLOAD =
			"http://maps.hcilab.org/stressreduction/db_download.php";

	/** uri of the database download script (DEBUG) */
	public static final String URI_DATABASE_DOWNLOAD_DEBUG =
			"http://10.0.2.2:8888/db_download_debug.php";

	/** convert meter per second to kilometer per hour */
	public static final float MS_TO_KMH = 3.6f;

	/** maximum speed in km/h for showing the popup */
	public static final int DIALOG_SPEED_LIMIT = 5;

	/** minimum speed in km/h for considering driving */
	public static final int MINIMUM_DRIVING_SPEED = 20;

	/** minimum time in ms between two stress reduction dialogs */
	public static final int DIALOG_TIMEOUT = 30000;

	/** minimum distance in m between two stress reduction dialogs */
	public static final int DIALOG_DISTANCE_TIMEOUT = 200;

//	// Strings for the database
//	public static final String TABLE_SEGMENT_INFOS = "segment_infos";
//	//	public static final String COLUMN_SEGMENT_COUNT = "segment_count";
//	public static final String COLUMN_SEGMENT_ID = "segment_id";
//	public static final String COLUMN_SEGMENT_NAME = "segment_name";
//	public static final String COLUMN_SEGMENT_HIGHWAY = "segment_highway";
//	public static final String COLUMN_SEGMENT_LANES = "segment_lanes";
//	public static final String COLUMN_SEGMENT_MAXSPEED = "segment_maxspeed";
//	public static final String COLUMN_SEGMENT_CURRENTSPEED = "segment_currentspeed";
//	public static final String COLUMN_SEGMENT_AVERAGESPEED = "segment_averagespeed";
//	public static final String COLUMN_SEGMENT_ONEWAY = "segment_oneway";
//	public static final String COLUMN_SEGMENT_REFERENCES = "segment_references";
//	public static final String COLUMN_SEGMENT_ROUTE = "segment_route";
//	//	public static final String COLUMN_SEGMENT_RESTRICTIONS = "segment_restrictions";
//	public static final String COLUMN_SEGMENT_TIMESTAMP = "segment_timestamp";
//	public static final String COLUMN_SEGMENT_STRESSFUL = "segment_stress_value";
//
//	public static final String TABLE_LOCATION_INFOS = "location_infos";
//	//	public static final String COLUMN_LOCATION_COUNT = "location_count";
//	public static final String COLUMN_LOCATION_LATITUDE = "location_latitude";
//	public static final String COLUMN_LOCATION_LONGITUDE = "location_longitude";
//	public static final String COLUMN_LOCATION_CURRENTSPEED = "location_speed";
//	public static final String COLUMN_LOCATION_ACCELERATION_X = "location_accelerationX";
//	public static final String COLUMN_LOCATION_ACCELERATION_Y = "location_accelerationY";
//	public static final String COLUMN_LOCATION_ACCELERATION_Z = "location_accelerationZ";
//	public static final String COLUMN_LOCATION_DIRECTION = "location_direction";
//	public static final String COLUMN_LOCATION_TIMESTAMP = "location_timestamp";
//
//	public static final String TABLE_TIMES = "times";
//	public static final String COLUMN_TIMES_START = "times_start";
//	public static final String COLUMN_TIMES_END_CALCULATED = "times_end_calculated";
//	public static final String COLUMN_TIMES_END_REAL = "times_end_real";
//
//	public static final String COLUMN_PHONE_ID = "phone_id";

	// ########## NEW DATABASE STRINGS ################
	public static final String DATABASE_NAME = "stressReduction.db";
	public static final String USERS = "Users";
	public static final String LOCATIONS = "Locations";
	public static final String SEGMENTS = "Segments";
	public static final String OSM_SEGMENTS = "OSMSegments";
	public static final String APP_LOGS = "AppLogs";
	public static final String ROUTING_LOGS = "RoutingLogs";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String HIGHWAY = "highway";
	public static final String LANES = "lanes";
	public static final String MAX_SPEED = "maxSpeed";
	public static final String ONEWAY = "oneway";
	public static final String REFERENCES = "mReferences";
	public static final String ROUTE = "route";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String SPEED = "speed";
	public static final String AVG_SPEED = "avgSpeed";
	public static final String ACC_X = "accelerationX";
	public static final String ACC_Y = "accelerationY";
	public static final String ACC_Z = "accelerationZ";
	public static final String DIRECTION = "direction";
	public static final String TIMESTAMP = "mTimestamp";
	public static final String USER_ID = "userId";
	public static final String OSM_SEGMENT_ID = "osmSegmentId";
	public static final String TIME_APP_OPEN = "timeAppOpen";
	public static final String START_LAT = "startLat";
	public static final String START_LON = "startLon";
	public static final String END_LAT = "endLat";
	public static final String END_LON = "endLon";
	public static final String TIME_ARRIVAL_PRE_CALC = "timeArrivalPreCalc";
	public static final String TIME_ARRIVAL_REAL = "timeArrivalReal";
	public static final String TIME_ROUTING_START = "timeRoutingStart";
	public static final String TIME_ROUTING_END = "timeRoutingEnd";
	public static final String STRESS_VALUE = "stressValue";

	public static final String CREATE_TABLE_USERS = "CREATE TABLE " + USERS + " (" +
			ID + " varchar(255) NOT NULL, " +
			"CONSTRAINT " + ID + " PRIMARY KEY (" + ID + "));";

	public static final String CREATE_TABLE_SEGMENTS = "CREATE TABLE " + SEGMENTS + " (" +
			OSM_SEGMENT_ID + " integer(10) NOT NULL, " +
//			NAME + " varchar(255), " +
//			HIGHWAY + " varchar(255), " +
//			LANES + " smallint(1), " +
//			MAX_SPEED + " smallint(3), " +
//			ONEWAY + " smallint(1), " +
//			REFERENCES + " varchar(255) NOT NULL, " +
//			ROUTE + " varchar(255) NOT NULL, " +
			AVG_SPEED + " smallint(3) NOT NULL, " +
			STRESS_VALUE + " smallint(1) NOT NULL, " +
			TIMESTAMP + " timestamp NOT NULL, " +
			USER_ID + " varchar(255) NOT NULL, " +
			"PRIMARY KEY (" + OSM_SEGMENT_ID + ", " + TIMESTAMP + ", " + USER_ID + "), " +
			"FOREIGN KEY(" + OSM_SEGMENT_ID + ") REFERENCES \"" + OSM_SEGMENTS + "\"(" + ID + "), " +
			"FOREIGN KEY(" + USER_ID + ") REFERENCES \"" + USERS + "\"(" + ID + "));";

	public static final String CREATE_TABLE_OSM_SEGMENTS = "CREATE TABLE " + OSM_SEGMENTS + " (" +
			ID + " integer NOT NULL PRIMARY KEY, " +
			NAME + " varchar(255), " +
			HIGHWAY + " varchar(255), " +
			LANES + " smallint(1), " +
			MAX_SPEED + " smallint(3), " +
			ONEWAY + " smallint(1));";

	public static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + LOCATIONS + " (" +
			LATITUDE + " double(10) NOT NULL, " +
			LONGITUDE + " double(10) NOT NULL, " +
			SPEED + " smallint(3) NOT NULL, " +
			ACC_X + " float(10) NOT NULL, " +
			ACC_Y + " float(10) NOT NULL, " +
			ACC_Z + " float(10) NOT NULL, " +
			DIRECTION + " integer(3) NOT NULL, " +
			TIMESTAMP + " timestamp NOT NULL, " +
			USER_ID + " varchar(255) NOT NULL, " +
			"PRIMARY KEY (" + TIMESTAMP + ", " + USER_ID + "), " +
			"FOREIGN KEY(" + USER_ID + ") REFERENCES \"" + USERS + "\"(" + ID + "));";

	public static final String CREATE_TABLE_APP_LOGS = "CREATE TABLE " + APP_LOGS + " (" +
			TIME_APP_OPEN + " timestamp NOT NULL, " +
			USER_ID + " varchar(255) NOT NULL, " +
			"PRIMARY KEY (" + TIME_APP_OPEN + ", " + USER_ID + "), " +
			"FOREIGN KEY(" + USER_ID + ") REFERENCES \"" + USERS + "\"(" + ID + "));";

	public static final String CREATE_TABLE_ROUTING_LOGS = "CREATE TABLE " + ROUTING_LOGS + " (" +
			START_LAT + " double(10) NOT NULL, " +
			START_LON + " double(10) NOT NULL, " +
			END_LAT + " double(10) NOT NULL, " +
			END_LON + " double(10) NOT NULL, " +
			TIME_ARRIVAL_PRE_CALC + " timestamp NOT NULL, " +
			TIME_ARRIVAL_REAL + " timestamp NOT NULL, " +
			TIME_ROUTING_START + " timestamp NOT NULL, " +
			TIME_ROUTING_END + " timestamp NOT NULL, " +
			USER_ID + " varchar(255) NOT NULL, " +
			"PRIMARY KEY (" + TIME_ROUTING_START + ", " + USER_ID + "), " +
			"FOREIGN KEY(" + USER_ID + ") REFERENCES \"" + USERS + "\"(" + ID + "));";
}
