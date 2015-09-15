package net.osmand.plus.stressreduction.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.content.Context;

import net.osmand.PlatformUtil;

import org.apache.commons.logging.Log;

/**
 * This class creates an unique identifier (UUID) for this device and stores the
 * UUID in the file system
 * (http://android-developers.blogspot.de/2011/03/identifying
 * -app-installations.html)
 *
 * @author Tobias
 */
public class UUIDCreator {

	private static final String INSTALLATION = "unique_identifier";
	private static final Log log = PlatformUtil.getLog(UUIDCreator.class);
	private static String UNIQUE_ID = null;

	public synchronized static String id(Context context) {
		if (UNIQUE_ID == null) {
			UNIQUE_ID = "UUID_ERROR";
			File installation = new File(context.getFilesDir(), INSTALLATION);
			log.debug("UUID File in: " + context.getFilesDir().getAbsolutePath());
			try {
				if (!installation.exists()) {
					writeInstallationFile(installation);
				}
				UNIQUE_ID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return UNIQUE_ID;
	}

	private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}
}
