package net.osmand.plus.stressreduction;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains constants used by the stress reduction plugin
 *
 * @author Tobias
 */
public class Constants {

	/** id string of the plugin */
	public static final String PLUGIN_ID = "net.osmand.plus.stressreduction.plugin";

	/** version code of the info dialog */
	private static final String INFO_VERSION_CODE = "2.2.0-216";

	/** version string of the info dialog */
	public static final String INFO_VERSION = "info_" + INFO_VERSION_CODE;

	/** version code of the what's new dialog */
	private static final String WHATS_NEW_VERSION_CODE = "2.2.0-216";

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

	/** identifier if data was correct uploaded */
	public static final String RECEIVER_UPLOAD = "receiver_upload";

	/** identifier for the last wifi receive */
	public static final String LAST_WIFI_RECEIVE = "last_wifi_receive";

	/** value of the wifi receiver timeout */
	public static final long WIFI_RECEIVER_TIMEOUT = 20000;

	/** identifier for the upload mode regular */
	public static final String UPLOAD_MODE_WIFI = "upload_mode_wifi";

	/** value of the lowest stress value */
	public static final int STRESS_VALUE_LOW = 2;

	/** value of the medium stress value */
	public static final int STRESS_VALUE_MEDIUM = 1;

	/** value of the highest stress value */
	public static final int STRESS_VALUE_HIGH = 0;

	public static final String SPEECH_INPUT = "speech_input";
	public static final String SPEECH_VALIDATION = "speech_validation";

	public static final List<String> SPEECH_INPUT_GOOD = Arrays.asList("good", "gut");
	public static final List<String> SPEECH_INPUT_NORMAL = Arrays.asList("normal");
	public static final List<String> SPEECH_INPUT_BAD = Arrays.asList("bad", "schlecht");
	public static final List<String> SPEECH_VALIDATION_CONFIRM =
			Arrays.asList("confirm", "bestaetigen");
	public static final List<String> SPEECH_VALIDATION_RETRY =
			Arrays.asList("retry", "wiederholen");
	public static final List<String> SPEECH_INPUT_ALL =
			Arrays.asList("good", "gut", "normal", "bad", "schlecht");
	public static final List<String> SPEECH_VALIDATION_ALL =
			Arrays.asList("confirm", "bestaetigen", "retry", "wiederholen");

	public static final Float[] SPEECH_VALUES =
			{1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f, 2.1f, 2.2f, 2.3f,
					2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f, 3.0f, 3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f,
					3.7f, 3.8f, 3.9f, 4.0f, 4.1f, 4.2f, 4.3f, 4.4f, 4.5f, 4.6f, 4.7f, 4.8f, 4.9f,
					5.0f};
	public static final String[] SPEECH_VALUES_E =
			{"1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0", "2.1",
					"2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", "3.0", "3.1", "3.2",
					"3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9", "4.0", "4.1", "4.2", "4.3",
					"4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "5.0"};

	/** uri of the database upload script */
	public static final String URI_DATABASE_UPLOAD =
			"https://maps.hci.simtech.uni-stuttgart.de/stressreduction/db_upload.php";

	/** uri of the database upload script (DEBUG EMULATOR) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_EMULATOR =
			"http://10.0.2.2:8888/upload/db_upload_v1.2_debug.php";

	/** uri of the database upload script (DEBUG DEVICE) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_DEVICE =
			"http://141.58.49.216:8888/upload/db_upload_v1.3_debug.php";

	/** uri of the database download script */
	public static final String URI_DATABASE_DOWNLOAD =
			"https://maps.hcilab.org/stressreduction/db_download.php";

	/** uri of the database download script (DEBUG) */
	public static final String URI_DATABASE_DOWNLOAD_DEBUG =
			"http://10.0.2.2:8888/download/db_download_debug.php";

	/** uri of the homepage */
	public static final String URI_HOMEPAGE = "https://maps.hci.simtech.uni-stuttgart.de/info";

	/** uri of the version code */
	public static final String URI_VERSION_CODE =
			"https://maps.hci.simtech.uni-stuttgart.de/happynavi/version";

	/** uri of the blank dash fragment content */
	public static final String URI_JSON_BLANK =
			"https://maps.hci.simtech.uni-stuttgart.de/happynavi/blank";

	/** convert meter per second to kilometer per hour */
	public static final float MS_TO_KMH = 3.6f;

	/** maximum speed in km/h for showing the popup */
	public static final int DIALOG_SPEED_LIMIT = 5;

	/** minimum speed in km/h for considering driving */
	public static final int MINIMUM_DRIVING_SPEED = 20;

	/** time between the first and last check of minimum driving speed in ms */
	public static final int SPEED_WATCHER_TIMEOUT = 2000;

	/** time between the first and last check of driving speed in ms */
	public static final int DIALOG_WATCHER_TIMEOUT = 5000;

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
	public static final String USER_GENDER = "userGender";
	public static final String USER_AGE = "userAge";
	public static final String USER_CAR = "userCar";
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
			USER_GENDER + " varchar(6), " +
			USER_AGE + " smallint(3), " +
			USER_CAR + " varchar(255), " +
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

	public static final String CREATE_TABLE_OSM_SEGMENTS =
			"CREATE TABLE IF NOT EXISTS " + OSM_SEGMENTS + " (" +
					ID + " integer NOT NULL PRIMARY KEY, " +
					NAME + " varchar(255), " +
					HIGHWAY + " varchar(255), " +
					LANES + " smallint(1), " +
					MAX_SPEED + " smallint(3), " +
					ONEWAY + " smallint(1));";

	public static final String CREATE_TABLE_LOCATIONS =
			"CREATE TABLE IF NOT EXISTS " + LOCATIONS + " (" +
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
					"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY +
					"));";

	public static final String CREATE_TABLE_APP_LOGS =
			"CREATE TABLE IF NOT EXISTS " + APP_LOGS + " (" +
					TIME_APP_OPEN + " timestamp NOT NULL, " +
					USER_PK + " integer NOT NULL, " +
					PRIMARY_KEY + " integer NOT NULL PRIMARY KEY, " +
					"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY +
					"));";

	public static final String CREATE_TABLE_ROUTING_LOGS =
			"CREATE TABLE IF NOT EXISTS " + ROUTING_LOGS + " (" +
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
					"FOREIGN KEY(" + USER_PK + ") REFERENCES \"" + USERS + "\"(" + PRIMARY_KEY +
					"));";
}
