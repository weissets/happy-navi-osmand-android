package net.osmand.plus.stressreduction.connectivity;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import net.osmand.PlatformUtil;
import net.osmand.plus.BuildConfig;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.stressreduction.Constants;

import org.apache.commons.logging.Log;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class is a service for downloading the database to the server
 *
 * @author Tobias
 */
public class DownloadService extends WakefulIntentService {

	private static final Log log = PlatformUtil.getLog(DownloadService.class);

	/**
	 * Constructor
	 */
	public DownloadService() {
		super("DownloadService");
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
		log.debug("doWakefulWork(): downloading database...");
		new DownloadTask().execute();
	}

	private void getVersionCodeSrDb() {
		new Thread() {
			@Override
			public void run() {
				try {
					log.debug("getVersionCodeSrDb()");
					URL url = new URL(Constants.URI_VERSION_CODE_SR_DB);
					HttpsURLConnection httpsURLConnection =
							(HttpsURLConnection) url.openConnection();
					httpsURLConnection.setRequestMethod("GET");
					httpsURLConnection.setDoInput(true); // allow inputs
					httpsURLConnection.setDoOutput(true); // allow outputs
					httpsURLConnection.setUseCaches(false); // no cached copy
					httpsURLConnection.setRequestProperty("Connection", "Close");
					httpsURLConnection.setRequestProperty("ENCTYPE", "text/plain");
					httpsURLConnection.connect();

					BufferedReader in = new BufferedReader(
							new InputStreamReader(httpsURLConnection.getInputStream()));
					String v;
					String ver = "";
					while ((v = in.readLine()) != null) {
						ver += v;
					}
					log.debug("getVersionCodeSrDb(): version = " + ver);
					((OsmandApplication) getApplicationContext())
							.getSettings().SR_DB_VERSION_CODE_SERVER.set(Integer.valueOf(ver));
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	public void copyFile(File from, File to) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(from);
		FileOutputStream fileOutputStream = new FileOutputStream(to);
		FileChannel fileChannelIn = fileInputStream.getChannel();
		FileChannel fileChannelOut = fileOutputStream.getChannel();
		fileChannelIn.transferTo(0, fileChannelIn.size(), fileChannelOut);
		fileInputStream.close();
		fileOutputStream.close();
	}

	private int downloadFile(String serverUri) {

		InputStream input = null;
		OutputStream output = null;
		HttpsURLConnection connection = null;

		String path = ((OsmandApplication)getApplication()).getSettings()
				.getExternalStorageDirectory().getAbsolutePath() + "/sr.db";
		String pathBackup = ((OsmandApplication)getApplication()).getSettings()
				.getExternalStorageDirectory().getAbsolutePath() + "/sr_backup.db";

		File file = new File(path);
		File fileBackup = new File(pathBackup);

		try {
			copyFile(file, fileBackup);
			URL url = new URL(serverUri);
			connection = (HttpsURLConnection) url.openConnection();
			connection.connect();

			// check for http ok to not save error message if download went wrong
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error("Server returned HTTP " + connection.getResponseCode() + ", " +
						connection.getResponseMessage());
				return connection.getResponseCode();
			}

			// download the file
			input = connection.getInputStream();
			output = new FileOutputStream(file);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				output.write(data, 0, count);
			}
			log.debug("downloadFile(): downloaded total: " + total);
		} catch (Exception e) {
			log.error(e.toString());
			try {
				copyFile(fileBackup, file);
				fileBackup.delete();
				log.debug("downloadFile(): restored backup sr db!");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return HttpURLConnection.HTTP_INTERNAL_ERROR;
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException ignored) {
			}

			if (connection != null) {
				connection.disconnect();
			}

		}
		// set db location in settings
		if (dbValidityCheck(path)) {
			((OsmandApplication) getApplicationContext()).getSettings().SR_DB_PATH.set(path);
			fileBackup.delete();
		} else {
			((OsmandApplication) getApplicationContext()).getSettings().SR_DB_PATH.set("");
		}
		return HttpURLConnection.HTTP_OK;
	}

	private boolean dbValidityCheck(String path) {
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
			db.close();
		} catch (SQLiteException e) {
			log.error("dbValidityCheck(): db not valid! " + e.getMessage());
		}
		return (db != null);
	}

	private class DownloadTask extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... sUrl) {

			OsmandSettings settings = ((OsmandApplication) getApplicationContext()).getSettings();
			int status;
//			getVersionCodeSrDb();
//			if (settings.SR_DB_VERSION_CODE_SERVER.get() > settings.SR_DB_VERSION_CODE_DEVICE.get
//					()) {
				log.debug("doInBackground(): loading new sr db from server...");
//				settings.SR_DB_VERSION_CODE_DEVICE.set(settings.SR_DB_VERSION_CODE_SERVER.get());

				if (!BuildConfig.DEBUG) {
					log.debug("UploadTask: doInBackground(): downloading sr db from " +
							Constants.URI_DATABASE_DOWNLOAD);
					status = downloadFile(Constants.URI_DATABASE_DOWNLOAD);
				} else {
					log.debug("UploadTask: doInBackground(): downloading sr db from " +
							Constants.URI_DATABASE_DOWNLOAD_DEBUG);
					status = downloadFile(Constants.URI_DATABASE_DOWNLOAD_DEBUG);
				}
//			} else {
//				log.debug("doInBackground(): no new sr db version");
//			}

			return status;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == HttpURLConnection.HTTP_OK) {
				log.debug("onPostExecute(): sr db at " +
						((OsmandApplication) getApplicationContext()).getSettings().SR_DB_PATH
								.get());
			} else {
				log.error("onPostExecute(): sr db not downloaded!");
			}
		}
	}
}


