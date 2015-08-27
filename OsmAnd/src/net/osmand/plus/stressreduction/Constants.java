package net.osmand.plus.stressreduction;

/**
 * This class contains constants used by the stress reduction plugin
 *
 * @author Tobias
 */
public class Constants {

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

	/** identifier for the upload mode regular */
	public static final String UPLOAD_MODE_WIFI = "upload_mode_wifi";

	/** identifier for the upload mode mobile */
	public static final String UPLOAD_MODE_MOBILE = "upload_mode_mobile";

	/** identifier for the upload mode after app was incorrect closed */
	public static final String UPLOAD_MODE_INCORRECT_CLOSE = "upload_mode_incorrect_close";

	/** uri of the database upload script */
	public static final String URI_DATABASE_UPLOAD =
			"http://maps.hcilab.org/stressreduction/db_upload_include.php";

	/** uri of the database upload script (DEBUG EMULATOR) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_EMULATOR =
			"http://10.0.2.2:8888/db_upload_debug.php";

	/** uri of the database upload script (DEBUG DEVICE) */
	public static final String URI_DATABASE_UPLOAD_DEBUG_DEVICE =
			"http://192.168.2.117:8888/db_upload_debug.php";

	/** convert meter per second to kilometer per hour */
	public static final float MS_TO_KMH = 3.6f;

	/** maximum speed in km/h for showing the popup */
	public static final int DIALOG_SPEED_LIMIT = 5;

	/** minimum speed in km/h for considering driving */
	public static final int MINIMUM_DRIVING_SPEED = 20;

	// Strings for the database
	public static final String TABLE_SEGMENT_INFOS = "segment_infos";
	//	public static final String COLUMN_SEGMENT_COUNT = "segment_count";
	public static final String COLUMN_SEGMENT_ID = "segment_id";
	public static final String COLUMN_SEGMENT_NAME = "segment_name";
	public static final String COLUMN_SEGMENT_HIGHWAY = "segment_highway";
	public static final String COLUMN_SEGMENT_LANES = "segment_lanes";
	public static final String COLUMN_SEGMENT_MAXSPEED = "segment_maxspeed";
	public static final String COLUMN_SEGMENT_CURRENTSPEED = "segment_currentspeed";
	public static final String COLUMN_SEGMENT_AVERAGESPEED = "segment_averagespeed";
	public static final String COLUMN_SEGMENT_ONEWAY = "segment_oneway";
	public static final String COLUMN_SEGMENT_REFERENCES = "segment_references";
	public static final String COLUMN_SEGMENT_ROUTE = "segment_route";
	//	public static final String COLUMN_SEGMENT_RESTRICTIONS = "segment_restrictions"; 
	public static final String COLUMN_SEGMENT_TIMESTAMP = "segment_timestamp";
	public static final String COLUMN_SEGMENT_STRESSFUL = "segment_stress_value";

	public static final String TABLE_LOCATION_INFOS = "location_infos";
	//	public static final String COLUMN_LOCATION_COUNT = "location_count"; 
	public static final String COLUMN_LOCATION_LATITUDE = "location_latitude";
	public static final String COLUMN_LOCATION_LONGITUDE = "location_longitude";
	public static final String COLUMN_LOCATION_CURRENTSPEED = "location_speed";
	public static final String COLUMN_LOCATION_ACCELERATION_X = "location_accelerationX";
	public static final String COLUMN_LOCATION_ACCELERATION_Y = "location_accelerationY";
	public static final String COLUMN_LOCATION_ACCELERATION_Z = "location_accelerationZ";
	public static final String COLUMN_LOCATION_DIRECTION = "location_direction";
	public static final String COLUMN_LOCATION_TIMESTAMP = "location_timestamp";

	public static final String COLUMN_PHONE_ID = "phone_id";

}
