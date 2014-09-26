package dusan.stefanovic.trainingapp.fragment;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import dusan.stefanovic.trainingapp.util.ImageHelper;
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
        mImageView.setTag(false);
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
        		ImageHelper.loadImageFromFile(mImageView, mPhotoFileUrl);
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
	    		ImageHelper.loadImageFromFile(mImageView, mPhotoFileUrl);
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
