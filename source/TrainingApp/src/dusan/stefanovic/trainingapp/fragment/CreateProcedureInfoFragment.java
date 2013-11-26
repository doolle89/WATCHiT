package dusan.stefanovic.trainingapp.fragment;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.util.PhotoFileFactory;
import dusan.stefanovic.treningapp.R;

public class CreateProcedureInfoFragment extends Fragment {
	
	private static final int ACTION_TAKE_PHOTO = 123;
	
	private File mPhotoFile;
	
	private TextView mTitleTextView;
	private TextView mDescriptionTextView;
	private ImageView mImageView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_procedure_info, container, false);
        //Bundle args = getArguments();
        
        mTitleTextView = (TextView) rootView.findViewById(R.id.procedure_title);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.procedure_description);
        mImageView = (ImageView) rootView.findViewById(R.id.procedure_image);
        
        mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dispatchTakePictureIntent();
			}
        	
        });
        
        return rootView;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ACTION_TAKE_PHOTO) {
	    	if (mPhotoFile != null) {
	    		showPhotoFile(mPhotoFile);
	    	}
	    }
	}
	
	private void dispatchTakePictureIntent() {
		final PackageManager packageManager = getActivity().getPackageManager();
	    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    if (list.size() > 0) {
	    	mPhotoFile = PhotoFileFactory.createPhotoFile();
	    	if (mPhotoFile != null) {
	    		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
		    	startActivityForResult(intent, ACTION_TAKE_PHOTO);
	    	}
	    }
	}
	
	private void showPhotoFile(File photoFile) {
		String photoFileUrl = photoFile.getAbsolutePath();
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetWidth = mImageView.getWidth();
		int targetHeight = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		//bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoFileUrl, bitmapOptions);
		int photoWidth = bitmapOptions.outWidth;
		int photoHeight = bitmapOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetWidth > 0) || (targetHeight > 0)) {
			scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);	
		}

		/* Set bitmap options to scale the image decode target */
		bitmapOptions.inJustDecodeBounds = false;
		//bitmapOptions.inSampleSize = scaleFactor;
		//bitmapOptions.inPurgeable = true;
		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(photoFileUrl, bitmapOptions);
		mImageView.setImageBitmap(bitmap);
	}
	
	public String getTitle() {
		return mTitleTextView.getText().toString();
	}
	
	public String getDescripton() {
		return mDescriptionTextView.getText().toString();
	}
	
	public String getPhotoUrl() {
		String photoUrl = null;
		if (mPhotoFile != null) {
			photoUrl = mPhotoFile.getAbsolutePath();
		}
		return photoUrl;
	}
}
