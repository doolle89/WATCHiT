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
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.trainingapp.dialog.StepLibraryDialogFragment;
import dusan.stefanovic.trainingapp.fragment.StepLibraryFragment.StepLibraryFragmentListener;
import dusan.stefanovic.trainingapp.util.PhotoFileFactory;
import dusan.stefanovic.trainingapp.view.TimePicker;
import dusan.stefanovic.treningapp.R;

public class CreateStepFragment extends Fragment {
	
	private static final int ACTION_TAKE_PHOTO = 563;
	
	public interface CreateStepFragmentListener {
		public void onStepSaved(Step step);
	}
	
	private CreateStepFragmentListener mCreateStepFragmentListener;
	
	private String mTempPhotoFileUrl;
	private String mPhotoFileUrl;
	
	private EditText mTitleEditText;
	private EditText mDescriptionEditText;
	private ImageView mImageView;
	private TimePicker mTimePicker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);	    
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_create_step, container, false);
        //Bundle args = getArguments();
        
        mTitleEditText = (EditText) rootView.findViewById(R.id.step_title);
        mDescriptionEditText = (EditText) rootView.findViewById(R.id.step_description);
        mImageView = (ImageView) rootView.findViewById(R.id.step_image);
        mTimePicker = (TimePicker) rootView.findViewById(R.id.timePicker);
        
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
		try {
			mCreateStepFragmentListener = (CreateStepFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement CreateStepFragmentListener");
        }
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.create_step, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_add_step:
	        	returnStep();
	            return true;
	        case R.id.action_save_step:
	        	saveStep();
	            return true;
	        case R.id.action_use_template_step:
	        	useTemplateFromLibrary();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
	
	private long getOptimalTime() {
		mTimePicker.clearFocus();
		
		int hours = mTimePicker.getCurrentHour() * 60 * 60;
		int minutes = mTimePicker.getCurrentMinute() * 60;
		int seconds = mTimePicker.getCurrentSecond();
		
		return (hours + minutes + seconds) * 1000l;
	}
	
	private void setOptimalTime(long milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60;
    	int minutes = (int) (milliseconds / 60000) % 60;
    	int hours = (int) (milliseconds / 3600000) % 24;
		
    	mTimePicker.setCurrentSecond(seconds);
    	mTimePicker.setCurrentMinute(minutes);
    	mTimePicker.setCurrentHour(hours);
	}
	
	private void resetView() {
		mTitleEditText.setText("");
		mDescriptionEditText.setText("");
		mTimePicker.setCurrentSecond(0);
    	mTimePicker.setCurrentMinute(0);
    	mTimePicker.setCurrentHour(0);
	}
	
	private Step createStep() {
		String title = mTitleEditText.getText().toString();
		if (title.contentEquals("")) {
			Toast.makeText(getActivity(), "Add title first", Toast.LENGTH_SHORT).show();
			return null;
		}
		String description = mDescriptionEditText.getText().toString();
		long optimalTime = getOptimalTime();
		return new Step(title, description, mPhotoFileUrl, optimalTime);
	}
	
	private void returnStep() {
		Step step = createStep();
		if (step != null) {
			Intent intent = new Intent();
			intent.putExtra(CreateProcedureStepsFragment.EXTRA_STEP_DATA, step);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}
	
	private void saveStep() {
		Step step = createStep();
		if (step != null) {
			AsyncTask<Step, Void, Step> asyncTask = new AsyncTask<Step, Void, Step>() {
	
				@Override
				protected Step doInBackground(Step... args) {
					if (getActivity() != null) {
						DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
						dbAdapter.open();
						dbAdapter.createStepTemplate(args[0]);
						dbAdapter.close();
					}
					return args[0];
				}
				
				@Override
				protected void onPostExecute(Step result) {
					if (getActivity() != null) {
						if (result.getTemplateId() != null) {
							Toast.makeText(getActivity(), "Step created seccessfully", Toast.LENGTH_SHORT).show();
							mCreateStepFragmentListener.onStepSaved(result);
							resetView();
						} else {
							Toast.makeText(getActivity(), "Unable to create step", Toast.LENGTH_SHORT).show();
						}
					}
				}
				
			};
			asyncTask.execute(step);
		}
	}
	
	private void useTemplateFromLibrary() {
		final StepLibraryDialogFragment stepLibraryDialogFragment = new StepLibraryDialogFragment();
		stepLibraryDialogFragment.setStepLibraryFragmentListener(new StepLibraryFragmentListener() {
			
			@Override
			public void onStepSelected(Step step) {
				mTitleEditText.setText(step.getTitle());
				mDescriptionEditText.setText(step.getDescription());
				setOptimalTime(step.getOptimalTime());
				stepLibraryDialogFragment.dismiss();
			}
		});
		stepLibraryDialogFragment.show(getChildFragmentManager(), "step_library_dialog");
	}
}
