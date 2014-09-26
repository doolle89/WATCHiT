package dusan.stefanovic.trainingapp.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class TextFileWriter {
	
	private static final String FOLDER = "WATCHiT";
	
	private static final String FILE_PREFIX = "WATCHiT_";
	private static final String FILE_SUFFIX = ".txt";
	
	public static File createTextFile(String fileName) {
		File textFile = null;
		try {
			if (fileName == null || fileName.contentEquals("")) {
				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
				fileName = FILE_PREFIX + timeStamp + "_";
			}
			File albumFile = getFileDir();
			textFile = File.createTempFile(fileName, FILE_SUFFIX, albumFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textFile;
	}

	private static File getFileDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FOLDER);
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()){
						return null;
					}
				}
			}
		} else {
			// External storage is not mounted READ/WRITE.
		}
		return storageDir;
	}
	
	public static boolean writeToFile(String content, String fileName) {
		boolean result = false;
		try {
			File file = createTextFile(fileName);
			FileWriter fileWriter = new FileWriter(file, false);
			fileWriter.write(content);
			fileWriter.close();
			result = true;
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

}
