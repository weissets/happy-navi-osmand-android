package net.osmand.plus.stressreduction.connectivity;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.BuildConfig;
import net.osmand.plus.stressreduction.Constants;
import net.osmand.plus.stressreduction.database.DatabaseBackup;
import net.osmand.plus.stressreduction.database.SQLiteLogger;

import org.apache.commons.logging.Log;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class is a service for uploading the database to the server
 *
 * @author Tobias
 */
public class UploadService extends WakefulIntentService {

	private static final Log log = PlatformUtil.getLog(UploadService.class);

	private long timeout;
	//	private boolean receiverUpload;

	/**
	 * Constructor
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
		//		receiverUpload = intent.getBooleanExtra(Constants.RECEIVER_UPLOAD, false);
		log.debug("doWakefulWork(): checking if there is data to upload...");
		if (SQLiteLogger.checkForData() && ((System.currentTimeMillis() - timeout) > 10000)) {
			timeout = System.currentTimeMillis();
			//	Log.d(StressReductionPlugin.TAG, "#DEBUG# UploadService.java:
			//      doWakefulWork(): copy database to sdcard...");
			//	copyDatabaseToSDCard();
			log.debug("doWakefulWork(): found data in database, start uploading...");
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
	 * Upload the given file to the server using https
	 *
	 * @param dbPath    Path to the file which should be uploaded
	 * @param serverUrl Url of the server
	 * @return The response code of the server
	 */
	private int uploadFileHttps(String dbPath, String serverUrl) {

		HttpsURLConnection httpsURLConnection;
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
		File sourceFile = new File(dbPath);

		if (!sourceFile.isFile()) {
			log.error("uploadFileHttps(): ERROR! could not find database file");
		} else {
			try {
				// open a URL connection
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(serverUrl);

				// Open a HTTP connection to the URL
				log.debug("uploadFileHttps(): open url connection with https");
				httpsURLConnection = (HttpsURLConnection) url.openConnection();
				log.debug("uploadFileHttps(): url connection with https is open");
				httpsURLConnection.setDoInput(true); // allow inputs
				httpsURLConnection.setDoOutput(true); // allow outputs
				httpsURLConnection.setUseCaches(false); // no cached copy
				httpsURLConnection.setRequestMethod("POST");
				//				httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
				httpsURLConnection.setRequestProperty("Connection", "Close");
				httpsURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
				httpsURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				httpsURLConnection.setRequestProperty("database", dbPath);

				log.debug("uploadFileHttps(): get output stream");
				dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());

				log.debug("uploadFileHttps(): write output stream intro");
				dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
				dataOutputStream.writeBytes(
						"Content-Disposition: form-data; name=\"database\";filename=\"" +
								dbPath + "\"" + lineEnd);

				dataOutputStream.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				log.debug("uploadFileHttps(): read input stream");
				// read file and write it into form
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				log.debug("uploadFileHttps(): write output stream");
				while (bytesRead > 0) {

					dataOutputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necessary after file data
				dataOutputStream.writeBytes(lineEnd);
				dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				log.debug("uploadFileHttps(): close streams");
				// close the streams
				fileInputStream.close();
				dataOutputStream.flush();
				dataOutputStream.close();

				// Responses from the server (code and message)
				log.debug("uploadFileHttps(): get response code");
				serverResponseCode = httpsURLConnection.getResponseCode();
				String serverResponseMessage = httpsURLConnection.getResponseMessage();

				log.debug("uploadFileHttps(): HTTPS Response is : " + serverResponseMessage + ": " +
						serverResponseCode);

				for (int i = 0; i < httpsURLConnection.getHeaderFields().size(); i++) {
					log.debug("uploadFileHttps(): Header" + i + " = " +
							httpsURLConnection.getHeaderField(i) + " " +
							httpsURLConnection.getHeaderFieldKey(i));
				}

			} catch (Exception e) {
				log.error("uploadFileHttps(): Error: " + e.getMessage());
			}
		}
		return serverResponseCode;
	}

	/**
	 * Upload the given file to the server using http
	 *
	 * @param dbPath    Path to the file which should be uploaded
	 * @param serverUrl Url of the server
	 * @return The response code of the server
	 */
	private int uploadFileHttp(String dbPath, String serverUrl) {

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
		File sourceFile = new File(dbPath);

		// TODO upload via https takes too long

		if (!sourceFile.isFile()) {
			log.error("uploadFileHttp(): ERROR! could not find database file");
		} else {
			try {
				// open a URL connection
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				URL url = new URL(serverUrl);

				// Open a HTTP connection to the URL
				log.debug("uploadFileHttp(): open url connection with http");
				httpURLConnection = (HttpURLConnection) url.openConnection();
				log.debug("uploadFileHttp(): url connection with http is open");
				httpURLConnection.setDoInput(true); // allow inputs
				httpURLConnection.setDoOutput(true); // allow outputs
				httpURLConnection.setUseCaches(false); // no cached copy
				httpURLConnection.setRequestMethod("POST");
				//				httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
				httpURLConnection.setRequestProperty("Connection", "Close");
				httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
				httpURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				httpURLConnection.setRequestProperty("database", dbPath);

				log.debug("uploadFileHttp(): get output stream");
				dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

				log.debug("uploadFileHttp(): write output stream intro");
				dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
				dataOutputStream.writeBytes(
						"Content-Disposition: form-data; name=\"database\";filename=\"" +
								dbPath + "\"" + lineEnd);

				dataOutputStream.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				log.debug("uploadFileHttp(): read input stream");
				// read file and write it into form
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				log.debug("uploadFileHttp(): write output stream");
				while (bytesRead > 0) {

					dataOutputStream.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				}

				// send multipart form data necessary after file data
				dataOutputStream.writeBytes(lineEnd);
				dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				log.debug("uploadFileHttp(): close streams");
				// close the streams
				fileInputStream.close();
				dataOutputStream.flush();
				dataOutputStream.close();

				// Responses from the server (code and message)
				log.debug("uploadFileHttp(): get response code");
				serverResponseCode = httpURLConnection.getResponseCode();
				String serverResponseMessage = httpURLConnection.getResponseMessage();

				log.debug("uploadFileHttp(): HTTP Response is : " + serverResponseMessage + ": " +
						serverResponseCode);

				for (int i = 0; i < httpURLConnection.getHeaderFields().size(); i++) {
					log.debug("uploadFileHttp(): Header" + i + " = " +
							httpURLConnection.getHeaderField(i) + " " +
							httpURLConnection.getHeaderFieldKey(i));
				}

			} catch (Exception e) {
				log.error("uploadFileHttp(): Error: " + e.getMessage());
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
				log.debug("UploadTask: doInBackground(): uploading to " +
						Constants.URI_DATABASE_UPLOAD);
				status = uploadFileHttps(databasePath, Constants.URI_DATABASE_UPLOAD);
			} else {
				if (Build.FINGERPRINT.contains("generic")) {
					log.debug("UploadTask: doInBackground(): debug upload, trying emulator " +
							"upload");
					status = uploadFileHttp(databasePath,
							Constants.URI_DATABASE_UPLOAD_DEBUG_EMULATOR);
				} else {
					log.debug("UploadTask: doInBackground(): debug upload, trying device upload");
					status = uploadFileHttp(databasePath,
							Constants.URI_DATABASE_UPLOAD_DEBUG_DEVICE);
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
		protected void onPostExecute(Integer serverResponseCode) {
			// delete uploaded entries in the database
			if (serverResponseCode == HttpsURLConnection.HTTP_OK) {
				log.debug("UploadTask: onPostExecute(): Upload OK: clearing database...");
				sqLiteLogger.clearDatabase();
			} else {
				log.error("UploadTask: onPostExecute(): Error: data not uploaded, " +
						"ERROR Code = " + serverResponseCode);
			}
			// doesn't work as expected, background service still active even if app closed
			// completely
			//			if (receiverUpload) {
			//				log.debug("onPostExecute(): was receiverUpload, closing app...");
			//				Intent closeAppIntent = new Intent(getApplicationContext(),
			// ExitActivity.class);
			//				closeAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//				startActivity(closeAppIntent);
			//			}
		}

	}

}

