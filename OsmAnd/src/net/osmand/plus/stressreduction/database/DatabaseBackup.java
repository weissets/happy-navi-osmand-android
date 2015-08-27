package net.osmand.plus.stressreduction.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

import net.osmand.PlatformUtil;

import org.apache.commons.logging.Log;

/**
 * This class is a test class for copying the database to the sd card
 *
 * @author Tobias
 */
public class DatabaseBackup {

	private static final Log log = PlatformUtil.getLog(DatabaseBackup.class);

	private String appName = "";
	private String packageName = "";

	public DatabaseBackup(final Context context) {
		this.appName = "osmand-database";
		packageName = context.getPackageName();
	}

	public void backup(String databaseName) {
		boolean rc = false;

		boolean writeable = isSDCardWriteable();
		if (writeable) {
			String pathFrom =
					Environment.getDataDirectory() + "/data/" + packageName + "/databases/";
			File file = new File(pathFrom + databaseName);

			String pathTo = Environment.getExternalStorageDirectory() + appName + "/backup/";

			File fileBackupDir = new File(pathTo);

			log.debug("backup(): copy database " + databaseName + " \n from: " + pathFrom +
					" \n to: " + pathTo);

//			if (!fileBackupDir.exists()) {
//				fileBackupDir.mkdirs();
//			}

			if (file.exists()) {
				File fileBackup = new File(fileBackupDir, databaseName);
				try {
//					fileBackup.createNewFile();
					copyFile(file, fileBackup);
					rc = true;
				} catch (Exception exception) {
					//
				}
			}
		}

		if (rc) {
			log.debug("backup(): Database successful copied to sd card");
		} else {
			log.error("backup(): Copy Database to sd card FAILED!!!");
		}

	}

	private static void copyFile(File file, File fileBackup) {
		try {
			//int bytesum = 0;
			int byteread;
			if (file.exists()) {
				InputStream inStream = new FileInputStream(file.getAbsolutePath());
				FileOutputStream fs = new FileOutputStream(fileBackup.getAbsolutePath());
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					//bytesum += byteread;
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isSDCardWriteable() {
		boolean writeable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			writeable = true;
		}

		return writeable;
	}
}
