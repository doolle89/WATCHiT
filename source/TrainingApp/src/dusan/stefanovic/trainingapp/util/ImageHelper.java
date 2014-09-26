package dusan.stefanovic.trainingapp.util;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class ImageHelper {
	
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
   
    public static void loadImageFromFile(ImageView imageView, String photoFilePath) {
		if (photoFilePath != null && !photoFilePath.contentEquals("")) {
	    	AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {
			    
				private WeakReference<ImageView> imageViewReference;

			    public AsyncTask<String, Void, Bitmap> initialise(ImageView imageView) {
			        // Use a WeakReference to ensure the ImageView can be garbage collected
			        imageViewReference = new WeakReference<ImageView>(imageView);
			        return this;
			    }
			    
			    @Override
			    protected Bitmap doInBackground(String... params) {
			    	final String photoFilePath = params[0];
			        
			        /* There isn't enough memory to open up more than a couple camera photos */
					/* So pre-scale the target bitmap into which the file is decoded */
			
					/* Get the size of the ImageView */
			    	int targetWidth = 0;
					int targetHeight = 0;
			    	if (imageViewReference != null) {
			            final ImageView imageView = imageViewReference.get();
			            if (imageView != null) {
			            	targetWidth = imageView.getWidth();
							targetHeight = imageView.getHeight();
			            }
			            if ((targetWidth > 0) && (targetHeight > 0)) {
							/* Get the size of the image */
							int rotation = getCameraPhotoOrientation(photoFilePath);
							BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
							bitmapOptions.inJustDecodeBounds = true;
							BitmapFactory.decodeFile(photoFilePath, bitmapOptions);
							int photoWidth = bitmapOptions.outWidth;
							int photoHeight = bitmapOptions.outHeight;
							if (rotation == 90 || rotation == 270) {
								photoWidth = bitmapOptions.outHeight;
								photoHeight = bitmapOptions.outWidth;
							}
							
							int scaleFactor = 1;
							scaleFactor = Math.max(photoWidth/targetWidth, photoHeight/targetHeight);
							
							/*
							if ((photoWidth < photoHeight) == (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
								scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);
							} else {
								scaleFactor = Math.max(photoWidth/targetWidth, photoHeight/targetHeight);
							}
							*/
							
							// scaleFactor mora da bude najmanje 2 inace ne radi iz nekog raloga
							if (scaleFactor == 1) {
								scaleFactor = 2;
							}
							
							/* Set bitmap options to scale the image decode target */
							bitmapOptions.inJustDecodeBounds = false;
							bitmapOptions.inSampleSize = scaleFactor;
							//bitmapOptions.inPurgeable = true;
							
							/* Decode the JPEG file into a Bitmap */
							Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath, bitmapOptions);
							if (bitmap != null) {
								Matrix matrix = new Matrix();
								matrix.postRotate(rotation);
								bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
							}
							return bitmap;
						} else {
							showDelayed(imageView, photoFilePath);
						}
			        }
					return null;
			    }
			    
			    @Override
			    protected void onPostExecute(Bitmap bitmap) {
			        if (imageViewReference != null && bitmap != null) {
			            final ImageView imageView = imageViewReference.get();
			            if (imageView != null) {
							if (imageView.getTag() instanceof Boolean && (Boolean) imageView.getTag() && imageView.getDrawable() instanceof BitmapDrawable) {
								Bitmap oldBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
								imageView.setImageBitmap(bitmap);
								oldBitmap.recycle();
				                imageView.setTag(true);
							} else {
								imageView.setImageBitmap(bitmap);
							}
			            }
			        }
			    }
			    
			}.initialise(imageView);
			asyncTask.execute(photoFilePath);
		}
	}
    
    private static void showDelayed(final ImageView imageView, final String photoFilePath) {
    	try {
			ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
			if (viewTreeObserver.isAlive()) {
				viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						loadImageFromFile(imageView, photoFilePath);
					}
				});
			}
		} catch (Exception e) {
			try {
				imageView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						showDelayed(imageView, photoFilePath);
					}
				}, 1000);
			} catch (Exception ee) {
			
			}
		}
    }
	
	public static int getCameraPhotoOrientation(String photoFilePath) {
		int photoOrientation = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(photoFilePath);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					photoOrientation = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					photoOrientation = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					photoOrientation = 270;
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return photoOrientation;
	}
}
