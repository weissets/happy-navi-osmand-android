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
import android.os.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO download the sr data from the server
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
		super("UploadService");
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

	private int downloadFile(String serverUri) {

		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;

		try {
			URL url = new URL(serverUri);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// check for http ok to not save error message if download went wrong
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.error("Server returned HTTP " + connection.getResponseCode() + ", " +
						connection.getResponseMessage());
				return connection.getResponseCode();
			}

			// download the file
			input = connection.getInputStream();
			output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() +
					"/osmand/database/sr.db");

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
			}
		} catch (Exception e) {
			log.error(e.toString());
		} finally {
			try {
				if (output != null) {
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
		return  HttpURLConnection.HTTP_OK;
	}

	private class DownloadTask extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... sUrl) {

			int status;

			if (!BuildConfig.DEBUG) {
				status = downloadFile(Constants.URI_DATABASE_DOWNLOAD);
			} else {
				status = downloadFile(Constants.URI_DATABASE_DOWNLOAD_DEBUG);
			}

			return status;
		}

		@Override
		protected void onPostExecute(Integer result) {

		}
	}
}


