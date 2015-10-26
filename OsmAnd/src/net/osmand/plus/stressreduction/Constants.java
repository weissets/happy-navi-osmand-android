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
	private static final String INFO_VERSION_CODE = "2.2.0-213";

	/** version string of the info dialog */
	public static final String INFO_VERSION = "info_" + INFO_VERSION_CODE;

	/** version code of the what's new dialog */
	private static final String WHATS_NEW_VERSION_CODE = "2.2.0-213";

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
	public static final String RECEIVER_UPLOAD = "receiver_upload";

	/** identifier for the last wifi receive */
	public static final String LAST_WIFI_RECEIVE = "last_wifi_receive";

	/** value of the wifi receiver timeout */
	public static final long WIFI_RECEIVER_TIMEOUT = 20000;

	/** identifier for the upload mode regular */
	public static final String UPLOAD_MODE_WIFI = "upload_mode_wifi";

	/** identifier for the upload mode mobile */
	public static final String UPLOAD_MODE_MOBILE = "upload_mode_mobile";

	/** value of the lowest stress value */
	public static final int STRESS_VALUE_LOW = 2;

	/** value of the medium stress value */
	public static final int STRESS_VALUE_MEDIUM = 1;

	/** value of the highest stress value */
	public static final int STRESS_VALUE_HIGH = 0;

	// TODO switch to https
	/** uri of the database upload script */
	public static final String URI_DATABASE_UPLOAD =
			"http://maps.hci.simtech.uni-stuttgart.de/stressreduction/db_upload_v1.2.php";

	/** uri of the database upload script (DEBUG EMULATOR) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_EMULATOR =
			"http://10.0.2.2:8888/upload/db_upload_v1.2_debug.php";

	/** uri of the database upload script (DEBUG DEVICE) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_DEVICE =
			"http://192.168.2.100:8888/upload/db_upload_v1.2_debug.php";

	/** uri of the database download script */
	public static final String URI_DATABASE_DOWNLOAD =
			"https://maps.hcilab.org/stressreduction/db_download.php";

	/** uri of the database download script (DEBUG) */
	public static final String URI_DATABASE_DOWNLOAD_DEBUG =
			"http://10.0.2.2:8888/download/db_download_debug.php";

	/** convert meter per second to kilometer per hour */
	public static final float MS_TO_KMH = 3.6f;

	/** maximum speed in km/h for showing the popup */
	public static final int DIALOG_SPEED_LIMIT = 5;

	/** minimum speed in km/h for considering driving */
	public static final int MINIMUM_DRIVING_SPEED = 20;

	/** time between the first and last check of minimum driving speed in ms */
	public static final int SPEED_TIMEOUT = 2000;

	/** minimum time span in ms between two stress reduction dialogs */
	public static final int DIALOG_TIMEOUT = 30000;

	/** minimum time span in ms before the routing log is valid if route was cancelled */
	public static final long ROUTING_LOG_TIMEOUT = 30000;

	/** minimum distance in m between two stress reduction dialogs */
	public static final int DIALOG_DISTANCE_TIMEOUT = 500;

	/** minimum distance in m for considering a routing abortion */
	public static final int MIN_DIST_ABORT = 200;

	// ############## DATABASE STRINGS ################

	// database name
	public static final String DATABASE_NAME = "stressReduction.db";

	// table names
	public static final String USERS = "Users";
	public static final String LOCATIONS = "Locations";
	public static final String SEGMENTS = "Segments";
	public static final String OSM_SEGMENTS = "OSMSegments";
	public static final String APP_LOGS = "AppLogs";
	public static final String ROUTING_LOGS = "RoutingLogs";

	// column names
	public static final String PRIMARY_KEY = "pk";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String HIGHWAY = "highway";
	public static final String LANES = "lanes";
	public static final String MAX_SPEED = "maxSpeed";
	public static final String ONEWAY = "oneway";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String SPEED = "speed";
	public static final String AVG_SPEED = "avgSpeed";
	public static final String ACC_X = "accelerationX";
	public static final String ACC_Y = "accelerationY";
	public static final String ACC_Z = "accelerationZ";
	public static final String DIRECTION = "direction";
	public static final String TIMESTAMP = "mTimestamp";
	public static final String USER_PK = "userPk";
	public static final String OSM_SEGMENT_ID = "osmSegmentId";
	public static final String TIME_APP_OPEN = "timeAppOpen";
	public static final String START_LAT = "startLat";
	public static final String START_LON = "startLon";
	public static final String END_LAT = "endLat";
	public static final String END_LON = "endLon";
	public static final String ABORT_LAT = "abortLat";
	public static final String ABORT_LON = "abortLon";
	public static final String TIME_ROUTING_START = "timeRoutingStart";
	public static final String TIME_ROUTING_END = "timeRoutingEnd";
	public static final String TIME_ROUTING_END_CALC = "timeRoutingEndCalc";
	public static final String TIME_ROUTING_ABORT = "timeRoutingAbort";
	public static final String DISTANCE_TO_END = "distanceToEnd";
	public static final String STRESS_VALUE = "stressValue";

	// INFO data types get converted internally -> http://www.sqlite.org/datatype3.html

	public static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + USERS + " (" +
			ID + " varchar(255) NOT NULL UNIQUE, " +
			PRIMARY_KEY + " integer NOT NULL PRIMARY KEY);";

	public static final String CREATE_TABLE_SEGMENTS = "CREATE TABLE IF NOT EXISTS " + SEGMENTS +
			" (" +
			OSM_SEGMENT_ID + " integer NOT NULL, " +
			AVG_SPEED + " smallint(3) NOT NULL, " +
			STRESS_VALUE + " smallint(1) NOT NULL, " +
			TIMESTAMP + " timestamp NOT NULL, " +
			USER_PK + " integer NOT NULL, " +
			PRIMARY_KEY + " integer NOT NULL PRIMARY KEY, " +
			"FOREIGN KEY(" + OSM_SEGMENT_ID + ") REFERENCES \"" + OSM_SEGMENTS + "\"(" + ID +
			"), " +
			"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY + "));";

	public static final String CREATE_TABLE_OSM_SEGMENTS = "CREATE TABLE IF NOT EXISTS " + OSM_SEGMENTS + " (" +
			ID + " integer NOT NULL PRIMARY KEY, " +
			NAME + " varchar(255), " +
			HIGHWAY + " varchar(255), " +
			LANES + " smallint(1), " +
			MAX_SPEED + " smallint(3), " +
			ONEWAY + " smallint(1));";

	public static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE IF NOT EXISTS " + LOCATIONS + " (" +
			LATITUDE + " double NOT NULL, " +
			LONGITUDE + " double NOT NULL, " +
			SPEED + " smallint(3) NOT NULL, " +
			ACC_X + " float(10) NOT NULL, " +
			ACC_Y + " float(10) NOT NULL, " +
			ACC_Z + " float(10) NOT NULL, " +
			DIRECTION + " integer(3) NOT NULL, " +
			TIMESTAMP + " timestamp NOT NULL, " +
			USER_PK + " integer NOT NULL, " +
			PRIMARY_KEY + " integer NOT NULL PRIMARY KEY, " +
			"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY + "));";

	public static final String CREATE_TABLE_APP_LOGS = "CREATE TABLE IF NOT EXISTS " + APP_LOGS + " (" +
			TIME_APP_OPEN + " timestamp NOT NULL, " +
			USER_PK + " integer NOT NULL, " +
			PRIMARY_KEY + " integer NOT NULL PRIMARY KEY, " +
			"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY + "));";

	public static final String CREATE_TABLE_ROUTING_LOGS = "CREATE TABLE IF NOT EXISTS " + ROUTING_LOGS + " (" +
			START_LAT + " double NOT NULL, " +
			START_LON + " double NOT NULL, " +
			END_LAT + " double NOT NULL, " +
			END_LON + " double NOT NULL, " +
			ABORT_LAT + " double, " +
			ABORT_LON + " double, " +
			TIME_ROUTING_START + " timestamp NOT NULL, " +
			TIME_ROUTING_END_CALC + " timestamp NOT NULL, " +
			TIME_ROUTING_END + " timestamp, " +
			TIME_ROUTING_ABORT + " timestamp, " +
			DISTANCE_TO_END + " integer, " +
			USER_PK + " integer NOT NULL, " +
			PRIMARY_KEY + " integer NOT NULL PRIMARY KEY, " +
			"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY + "));";
}
