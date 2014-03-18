package dusan.stefanovic.trainingapp.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

public class PhotoFileFactory {
	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	public static File createPhotoFile() {
		File imageFile = null;
		try {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
			String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
			File albumFile = getAlbumDir();
			imageFile = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageFile;
	}

	private static File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getAlbumName());
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
	
	private static String getAlbumName() {
		return "WATCHiTProcedureTrainer";
	}

}
