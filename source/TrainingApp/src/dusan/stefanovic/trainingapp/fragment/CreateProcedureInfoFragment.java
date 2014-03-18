package dusan.stefanovic.trainingapp.fragment;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageView;
import dusan.stefanovic.trainingapp.util.PhotoFileFactory;
import dusan.stefanovic.treningapp.R;

public class CreateProcedureInfoFragment extends Fragment {
	
	private static final int ACTION_TAKE_PHOTO = 123;
	
	private String mTempPhotoFileUrl;
	private String mPhotoFileUrl;
	
	private EditText mTitleEditText;
	private EditText mDescriptionEditText;
	private ImageView mImageView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_procedure_info, container, false);
        //Bundle args = getArguments();
        
        mTitleEditText = (EditText) rootView.findViewById(R.id.procedure_title);
        mDescriptionEditText = (EditText) rootView.findViewById(R.id.procedure_description);
        mImageView = (ImageView) rootView.findViewById(R.id.procedure_image);
        mImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dispatchTakePictureIntent();
			}
        	
        });
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	
        	@SuppressWarnings("deprecation")
			@Override
        	public void onGlobalLayout() {
        		ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        		viewTreeObserver.removeGlobalOnLayoutListener(this);
        		showPhotoFile(mPhotoFileUrl);
        	}
        	
        });
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        	mPhotoFileUrl = savedInstanceState.getString("photo_file_url");
        }
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photo_file_url", mPhotoFileUrl);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ACTION_TAKE_PHOTO) {
	    	if (resultCode == Activity.RESULT_OK) {
	    		mPhotoFileUrl = mTempPhotoFileUrl;
		    	showPhotoFile(mPhotoFileUrl);
	    	}
	    }
	}
	
	private void dispatchTakePictureIntent() {
		final PackageManager packageManager = getActivity().getPackageManager();
	    final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    if (list.size() > 0) {
	    	File photoFile = PhotoFileFactory.createPhotoFile();
	    	if (photoFile != null) {
	    		mTempPhotoFileUrl = photoFile.getAbsolutePath();
	    		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		    	startActivityForResult(intent, ACTION_TAKE_PHOTO);
	    	}
	    }
	}
	
	private void showPhotoFile(String photoFileUrl) {
		if (photoFileUrl != null) {
			/* There isn't enough memory to open up more than a couple camera photos */
			/* So pre-scale the target bitmap into which the file is decoded */
	
			/* Get the size of the ImageView */
			int targetWidth = mImageView.getWidth();
			int targetHeight = mImageView.getHeight();
	
			/* Get the size of the image */
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(photoFileUrl, bitmapOptions);
			int photoWidth = bitmapOptions.outWidth;
			int photoHeight = bitmapOptions.outHeight;
			
			boolean min = (photoWidth > photoHeight) == (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
			/* Figure out which way needs to be reduced less */
			int scaleFactor = 1;
			if (((targetWidth > 0) || (targetHeight > 0)) && min) {
				scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);	
			} else if (((targetWidth > 0) || (targetHeight > 0)) && !min) {
				scaleFactor = Math.max(photoWidth/targetWidth, photoHeight/targetHeight);
			}
			
			// scaleFactor mora da bude najmanje 2 inace ne radi iz nekog raloga
			if (scaleFactor == 1) {
				scaleFactor = 2;
			}
	
			/* Set bitmap options to scale the image decode target */
			bitmapOptions.inJustDecodeBounds = false;
			bitmapOptions.inSampleSize = scaleFactor;
			bitmapOptions.inPurgeable = true;
			/* Decode the JPEG file into a Bitmap */
			((BitmapDrawable) mImageView.getDrawable()).getBitmap().recycle();
			Bitmap bitmap = BitmapFactory.decodeFile(photoFileUrl, bitmapOptions);
			mImageView.setImageBitmap(bitmap);
		}
	}
	
	public String getTitle() {
		return mTitleEditText.getText().toString();
	}
	
	public String getDescripton() {
		return mDescriptionEditText.getText().toString();
	}
	
	public String getPhotoUrl() {
		return mPhotoFileUrl;
	}
}
