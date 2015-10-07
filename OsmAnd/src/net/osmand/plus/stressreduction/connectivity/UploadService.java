package net.osmand.plus.stressreduction.connectivity;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.BuildConfig;
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
	private boolean isWifiReceiverUpload;

	/**
	 * Constructor
	 */
	public UploadService() {
		super("UploadService");
		timeout = 0;
		isWifiReceiverUpload = false;
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
		if (SQLiteLogger.checkForData() &&
				((System.currentTimeMillis() - timeout) > 10000)) {
			timeout = System.currentTimeMillis();
			//	Log.d(StressReductionPlugin.TAG, "#DEBUG# UploadService.java:
			//      doWakefulWork(): copy database to sdcard...");
			//	copyDatabaseToSDCard();
			log.debug("doWakefulWork(): found data in database, start uploading...");
			isWifiReceiverUpload = intent.getBooleanExtra(Constants.WIFI_RECEIVER_UPLOAD, false);
			new UploadTask().execute(sqLiteLogger);
		} else {
			log.debug("doWakefulWork(): there is no data to upload");
		}
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
	 * @param serverUri Uri of the server
	 * @return The response code of the server
	 */
	private int uploadFile(String sourceFileUri, String serverUri) {

		HttpURLConnection httpURLConnection;
		DataOutputStream dataOutputStream;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int serverResponseCode = -1;
		int bytesRead;
		int bytesAvailable;
		int bufferSize;
		int maxBufferSize = 1024 * 1024;
		byte[] buffer;
		File sourceFile = new File(sourceFileUri);

		if (!sourceFile.isFile()) {
			log.error("uploadFile(): ERROR! could not find database file");
		} else {
			try {
				// open a URL connection
				FileInputStream fileInputStream = new FileInputStream(sourceFile);

				URL url = new URL(serverUri);

				// Open a HTTP connection to the URL
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setDoInput(true); // Allow Inputs
				httpURLConnection.setDoOutput(true); // Allow Outputs
				httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
				httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
				httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" +
						boundary);
				httpURLConnection.setRequestProperty("database", sourceFileUri);

				dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

				dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
				dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"database\";filename=\"" +
						sourceFileUri + "\"" + lineEnd);

				dataOutputStream.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dataOutputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necessary after file data
				dataOutputStream.writeBytes(lineEnd);
				dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				// Responses from the server (code and message)
				serverResponseCode = httpURLConnection.getResponseCode();
				String serverResponseMessage = httpURLConnection.getResponseMessage();

				log.debug("uploadFile(): HTTP Response is : " + serverResponseMessage + ": " +
						serverResponseCode);

				for (int i = 0; i < httpURLConnection.getHeaderFields().size(); i++) {
					log.debug("uploadFile(): Header" + i + "=" + httpURLConnection.getHeaderField(i) + " " +
							httpURLConnection.getHeaderFieldKey(i));
				}

				// close the streams
				fileInputStream.close();
				dataOutputStream.flush();
				dataOutputStream.close();

			} catch (Exception e) {
				log.error("uploadFile(): Error: " + e.getMessage());
			}
		}
		return serverResponseCode;
	}

	/**
	 * @author Tobias
	 */
	private class UploadTask extends AsyncTask<SQLiteLogger, String, Integer> {

		SQLiteLogger sqLiteLogger = null;

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground(SQLiteLogger... params) {

			sqLiteLogger = params[0];
			final String databasePath = sqLiteLogger.getReadableDatabase().getPath();
			int status;

			log.debug("UploadTask: doInBackground(): start uploading with async task...");
			if (!BuildConfig.DEBUG) {
				status = uploadFile(databasePath, Constants.URI_DATABASE_UPLOAD);
			} else {
				log.debug("UploadTask: doInBackground(): debug upload, trying emulator upload");
				status = uploadFile(databasePath, Constants.URI_DATABASE_UPLOAD_DEBUG_EMULATOR);
				if (status != HttpURLConnection.HTTP_OK) {
					log.debug("UploadTask: doInBackground(): debug upload, trying device upload");
					status = uploadFile(databasePath, Constants.URI_DATABASE_UPLOAD_DEBUG_DEVICE);
				}
			}
			return status;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Integer result) {
			// delete uploaded entries in the database
			if (result == HttpURLConnection.HTTP_OK) {
				log.debug("UploadTask: onPostExecute(): Upload OK: clearing database...");
				sqLiteLogger.clearDatabase();
			}
			else {
				log.error("UploadTask: onPostExecute(): Error: data not uploaded, " +
						"ERROR Code = " + result);
			}
			if (isWifiReceiverUpload) {
				System.runFinalization();
				System.exit(0);
			}
		}
	}

}

