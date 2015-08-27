package net.osmand.plus.stressreduction.connectivity;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.database.DatabaseBackup;
import net.osmand.plus.stressreduction.database.SQLiteLogger;

import org.apache.commons.logging.Log;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is a service for uploading the database to the server
 *
 * @author Tobias
 */
public class UploadService extends WakefulIntentService {

	private static final Log log = PlatformUtil.getLog(UploadService.class);

	private long timeout;

	/**
	 *
	 */
	public UploadService() {
		super("UploadService");
		timeout = 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.commonsware.cwac.wakeful.WakefulIntentService#doWakefulWork(android
	 * .content.Intent)
	 */
	@Override
	protected void doWakefulWork(Intent intent) {
		SQLiteLogger sqLiteLogger = SQLiteLogger.getSQLiteLogger(this);
		log.debug("doWakefulWork(): checking if there is data to upload...");
		if (checkForData(sqLiteLogger.getReadableDatabase()) &&
				((System.currentTimeMillis() - timeout) > 10000)) {
			timeout = System.currentTimeMillis();
			//			Log.d(StressReductionPlugin.TAG, "#DEBUG# UploadService.java:
			// doWakefulWork(): copy database to sdcard...");
			//			copyDatabaseToSDCard();
			log.debug("doWakefulWork(): found data in database, start uploading...");
			new UploadTask().execute(sqLiteLogger);
		} else {
			log.debug("doWakefulWork(): there is no data to upload");
		}
	}

	/**
	 * Check if there are entries in the database
	 *
	 * @param sqLiteDatabase the database to check for entries
	 * @return true if there are entries in the database, otherwise false
	 */
	private boolean checkForData(SQLiteDatabase sqLiteDatabase) {
		if (sqLiteDatabase != null) {
			String[] columns = {Constants.COLUMN_PHONE_ID};
			Cursor cursor = sqLiteDatabase
					.query(Constants.TABLE_LOCATION_INFOS, columns, null, null, null, null, null);
			int count = cursor.getCount();
			cursor.close();
			log.debug("checkForData(): found " + count + " entries in database table location " +
					"infos");
			if (count > 0) {
				return true;
			}
		}
		return false;
	}

	private void copyDatabaseToSDCard() {
		// DEBUG: copy database to sdcard
		DatabaseBackup tools = new DatabaseBackup(getApplicationContext());
		String path = SQLiteLogger.getSQLiteLogger(this).getReadableDatabase().getPath();
		String name = path.substring(path.lastIndexOf("/") + 1);
		tools.backup(name);
	}

	/**
	 * Upload the given file to the server
	 *
	 * @param sourceFileUri Path to the file which should be uploaded
	 * @return The response code of the server
	 */
	private int uploadFile(String sourceFileUri, String serverUri) {

		int serverResponseCode = -1;

		HttpURLConnection conn;
		DataOutputStream dos;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {
			log.error("uploadFile(): ERROR! could not find database file");
		} else {
			try {

				// open a URL connection
				FileInputStream fileInputStream = new FileInputStream(sourceFile);

				// URL url = new URL(Constants.URI_DATABASE_UPLOAD);
				URL url = new URL(serverUri);

				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" +
						boundary);
				conn.setRequestProperty("database", sourceFileUri);

				dos = new DataOutputStream(conn.getOutputStream());

				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=\"database\";filename=\"" +
						sourceFileUri + "\"" + lineEnd);

				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necessary after file data
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();

				log.debug("uploadFile(): HTTP Response is : " + serverResponseMessage + ": " +
						serverResponseCode);

				for (int i = 0; i < conn.getHeaderFields().size(); i++) {
					log.debug("uploadFile(): Header" + i + "=" + conn.getHeaderField(i) + " " +
							conn.getHeaderFieldKey(i));
				}

				// close the streams
				fileInputStream.close();
				dos.flush();
				dos.close();

			} catch (Exception e) {
				//				log.error("uploadFile(): Error: " + e.getMessage(), e);
				log.error("uploadFile(): Error: " + e.getMessage());
			}
		}
		return serverResponseCode;
	}

	/**
	 * @author Tobias
	 */
	private class UploadTask extends AsyncTask<SQLiteLogger, String, String> {

		SQLiteLogger sqLiteLogger = null;
		SQLiteDatabase sqLiteDatabase = null;

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(SQLiteLogger... params) {

			sqLiteLogger = params[0];
			sqLiteDatabase = sqLiteLogger.getReadableDatabase();

			log.debug("UploadTask: doInBackground(): start uploading with async task...");
			final String databasePath = sqLiteDatabase.getPath();
			int status = uploadFile(databasePath, Constants.URI_DATABASE_UPLOAD_DEBUG_EMULATOR);

			if (status == 200) {
				return "OK";
			} else {
				int preStatus = status;
				status = uploadFile(databasePath, Constants.URI_DATABASE_UPLOAD_DEBUG_DEVICE);
				if (status == 200) {
					return "OK";
				} else {
					return "ERROR: Status Codes = " + preStatus + ", " + status;
				}
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			// delete uploaded entries in the database
			if (result.equalsIgnoreCase("OK")) {
				log.debug("UploadTask: onPostExecute(): Upload OK: clearing database...");
				sqLiteLogger.clearDatabase();
			}
			// if there was an error close application
			else {
				log.error("UploadTask: onPostExecute(): Error: data not uploaded, " +
						"ERROR=" + result);
			}
		}

	}

}