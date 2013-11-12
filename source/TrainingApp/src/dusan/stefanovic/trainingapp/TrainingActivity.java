/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dusan.stefanovic.trainingapp;


import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.fragment.TrainingCurrentStepFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingProgressFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingStepsFragment;
import dusan.stefanovic.trainingapp.service.TrainingService;
import dusan.stefanovic.trainingapp.service.TrainingService.TrainingServiceListener;
import dusan.stefanovic.trainingapp.service.WATCHiTServiceInterface;
import dusan.stefanovic.treningapp.R;

public class TrainingActivity extends ActionBarActivity implements TabListener, TrainingServiceListener {
	
	ActionBar mActionBar;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    Button mStartButton;
    Button mResumeButton;
    Button mPauseButton;
    
    // Intent for starting service after it's already bound
    private Intent mDelayedStartServiceIntent;
    
    private boolean mIsBindCalled;
    private boolean mIsBound;
    private int mDeviceConnectionState;

    private TrainingService mBoundService;
    
    Procedure mProcedure;
    long mStartDelay = 5000;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((TrainingService.LocalBinder) service).getService();
            mBoundService.registerTrainingServiceListener(TrainingActivity.this);
            mProcedure = mBoundService.setProcedure(mProcedure);
            doSynchronization();
            
            // start service with the intent after it's already bound
            if (mDelayedStartServiceIntent != null) {
            	startService(mDelayedStartServiceIntent);
            	mDelayedStartServiceIntent = null;
            }
            
            setIsBound(true);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            setIsBound(false);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        mActionBar = getSupportActionBar();

        // Specify Home/Up button 
        //actionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        // Specify that we will be displaying tabs in the action bar.
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            
        	@Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                mActionBar.setSelectedNavigationItem(position);
            }
        	
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            mActionBar.addTab(mActionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
        mActionBar.setSelectedNavigationItem(SectionsPagerAdapter.FRAGMENT_PROGRESS);
        
        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mBoundService != null) {
					mBoundService.startTraining();
				} else {
					setIsBound(false);
				}
			}
		});
        
        mResumeButton = (Button) findViewById(R.id.resume_button);
        mResumeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mBoundService != null) {
					mBoundService.resumeTraining();
				} else {
					setIsBound(false);
				}
			}
		});
        
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mPauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mBoundService != null) {
					mBoundService.pauseTraining();
				} else {
					setIsBound(false);
				}
			}
		});
        
        mProcedure = getIntent().getParcelableExtra("procedure");
        Intent intent = new Intent(this, TrainingService.class);
        intent.putExtras(getIntent());
        doBindAndStartService(intent);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        doSynchronization();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.training, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	tryToQuitTrainingActivity();
	            return true;
	        case R.id.action_stop_training:
	        	if (mBoundService != null) {
					mBoundService.stopTraining();
				} else {
					setIsBound(false);
				}
	            return true;
	        case R.id.action_settings:
	        	Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SETTINGS);
	        	startActivity(intent);
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
    @Override
    public void onBackPressed() {
    	tryToQuitTrainingActivity();
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
    	
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
    	
    }
    
    @Override
	public void onTrainingStart() {
    	updateSteps();
    	updateTimer(0);
    	performCountDown();
    	mStartButton.setVisibility(View.GONE);
    	showPauseButton();
	}
    
    @Override
	public void onTrainingStarted() {
    	updateSteps();
	}

	@Override
	public void onTrainingResumed() {
		updateSteps();
		showPauseButton();
	}

	@Override
	public void onTrainingPaused() {
		updateSteps();
		showResumeButton();
	}

	@Override
	public void onTrainingStopped() {
		updateSteps();
		showSelfAssessmentDialog();
		showStartButton();
	}
	
	@Override
	public void onDeviceConnectionChanged(int connectionState) {
		setDeviceConnectionState(connectionState);
	}
    
    @Override
	public void onProgressUpdated() {
    	updateProgress();
    	updateSteps();
	}

	@Override
	public void onTimerTicked(long milliseconds) {
		updateTimer(milliseconds);
	}
	
	private void tryToQuitTrainingActivity() {
		if (mProcedure.isStarted()) {
			DialogFragment dialog = new QuitDialogFragment();
			dialog.show(getSupportFragmentManager(), "quit_dialog");
		} else {
	        stopService(new Intent(this, TrainingService.class));
			finish();
		}
	}
	
	public void showSelfAssessmentDialog() {
		DialogFragment dialog = new SelfAssessmentDialogFragment();
		dialog.show(getSupportFragmentManager(), "self_assessment_dialog");
	}
	
	public void performCountDown() {
		CountDownDialogFragment dialog = new CountDownDialogFragment();
		dialog.show(getSupportFragmentManager(), "count_down_dialog");
	}
	
	private void doBindService(Intent intent) {
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBindCalled = true;
    }
	
    private void doBindAndStartService(Intent intent) {
    	mDelayedStartServiceIntent = intent;
    	doBindService(intent);
    }
    
    private void doUnbindService() {
        if (mIsBindCalled) {
        	if (mBoundService != null) {
        		mBoundService.unregisterTrainingServiceListener(this);
        	}
            unbindService(mConnection);
            mIsBindCalled = false;
            setIsBound(false);
        }
    }
    
    private void doSynchronization() {
    	// nesto ne radi u slucaju kada se upali rucno konekcija i onda se vratimo u aplikaciju
    	if (mBoundService != null) {
    		mProcedure = mBoundService.getProcedure();
    		updateProgress();
    		updateSteps();
    		updateState();
	        updateTimer(mBoundService.getElapsedTime());
	        setDeviceConnectionState(mBoundService.getDeviceConnectionState());
    	} else {
    		setIsBound(false);
    	}
    }
    
    private void setIsBound(boolean isBound) {
		mIsBound = isBound;
		if (!mIsBound) {
			setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_DISCONNECTED);
		}
	}
    
    private void setDeviceConnectionState(int deviceConnectionState) {
    	mDeviceConnectionState = deviceConnectionState;
	    switch (deviceConnectionState) {
			case WATCHiTServiceInterface.DEVICE_DISCONNECTED:
		    	mActionBar.setIcon(R.drawable.circle_red);
		        break;
			case WATCHiTServiceInterface.DEVICE_CONNECTING:
				mActionBar.setIcon(R.drawable.circle_yellow);
		        break;
		    case WATCHiTServiceInterface.DEVICE_CONNECTED:
		    	mActionBar.setIcon(R.drawable.circle_green);
		        break;
		}
	    
	    if (deviceConnectionState != WATCHiTServiceInterface.DEVICE_CONNECTED) {
	    	if (mProcedure.isStarted()) {
	    		// notifikacija ovde
	    	}
			mStartButton.setEnabled(false);
			mResumeButton.setEnabled(false);
		} else {
			mStartButton.setEnabled(true);
			mResumeButton.setEnabled(true);
		}
    }
    
    private void updateProgress() {
    	mSectionsPagerAdapter.mTrainingProgressFragment.setProgress(mProcedure.getProgress());
    }
    
    private void updateSteps() {
    	mSectionsPagerAdapter.mTrainingStepsFragment.setStepsList(mProcedure.getSteps());
    }
    
    private void updateState() {
    	if (!mProcedure.isStarted()) {
    		showStartButton();
    	} else if (mProcedure.isPaused()) {
    		showResumeButton();
    	} else if (mProcedure.isRunning()) {
    		showPauseButton();
    	}
    }
    
    private void updateTimer(long milliseconds) {
    	mSectionsPagerAdapter.mTrainingProgressFragment.updateTimer(milliseconds);
    }
    
    private void showStartButton() {
    	mStartButton.setVisibility(View.VISIBLE);
		mResumeButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.GONE);
    }
    
    private void showResumeButton() {
    	mStartButton.setVisibility(View.GONE);
		mResumeButton.setVisibility(View.VISIBLE);
		mPauseButton.setVisibility(View.GONE);
    }
    
    private void showPauseButton() {
    	mStartButton.setVisibility(View.GONE);
		mResumeButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.VISIBLE);
    }
    
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
    	
    	static final int FRAGMENT_CURRENT_STEP = 0;
    	static final int FRAGMENT_PROGRESS = 1;
    	static final int FRAGMENT_STEPS = 2;
    	
    	TrainingCurrentStepFragment mTrainingCurrentStepFragment;
    	TrainingProgressFragment mTrainingProgressFragment;
    	TrainingStepsFragment mTrainingStepsFragment;

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments == null) {
	            mTrainingCurrentStepFragment = new TrainingCurrentStepFragment();
	            mTrainingProgressFragment = new TrainingProgressFragment();
	            mTrainingStepsFragment = new TrainingStepsFragment();
            } else {
            	for (Fragment fragment : fragments) {
            		if (fragment instanceof TrainingCurrentStepFragment) {
            			mTrainingCurrentStepFragment = (TrainingCurrentStepFragment) fragment;
            		} else if (fragment instanceof TrainingProgressFragment) {
            			mTrainingProgressFragment = (TrainingProgressFragment) fragment;
            		} else if (fragment instanceof TrainingStepsFragment) {
            			mTrainingStepsFragment = (TrainingStepsFragment) fragment;
            		}
            	}
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case FRAGMENT_CURRENT_STEP:
                    return mTrainingCurrentStepFragment;
                case FRAGMENT_PROGRESS:
                    return mTrainingProgressFragment;
                case FRAGMENT_STEPS:
                    return mTrainingStepsFragment;
            }
			return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	switch (position) {
	            case FRAGMENT_CURRENT_STEP:
	                return "CURRENT STEP";
	            case FRAGMENT_PROGRESS:
	                return "PROGRESS";
	            case FRAGMENT_STEPS:
	                return "STEPS";
	        }
			return null;
        }
    }
    
    public static class QuitDialogFragment extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle("Quit training?");
	    	builder.setMessage("Are you sure you want to quit training?");
	    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    	        getActivity().stopService(new Intent(getActivity(), TrainingService.class));
	    	        getActivity().finish();
	            }
	    		
	    	});
	        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        		
	            }
	        	
	        });
	        return builder.create();
		}
		 
	}
    
    public static class SelfAssessmentDialogFragment extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle("Start self-assessment?");
	    	builder.setMessage("Do you want to start self-assessment?");
	    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    	        Intent intent = new Intent(getActivity(), SelfAssessment.class);
	    	        intent.putExtra("procedure", ((TrainingActivity) getActivity()).mProcedure);
	    	        startActivity(intent);
	            }
	    		
	    	});
	        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        		
	            }
	        	
	        });
	        return builder.create();
		}
	}
    
    public static class CountDownDialogFragment extends DialogFragment {
    	
    	TextView mTextView;
    	
    	boolean mShouldStart;
    	
    	@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent);
            setRetainInstance(true);
        }
		
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dialog_countdown, container, false);
            mTextView = (TextView) rootView.findViewById(R.id.textView);
            
        	if (mShouldStart) {
        		CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
            		
            		Vibrator mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            		
            		@Override
            		public void onTick(long millisUntilFinished) {
            			long secondsUntilFinished = (millisUntilFinished / 1000);
            			mTextView.setText(String.valueOf(secondsUntilFinished));
            			mVibrator.vibrate(300);
            		}
            		
            		@Override
            		public void onFinish() {
            			mTextView.setText("GO");
            			mVibrator.vibrate(1500);
            			dismiss();
            		}
            		
            	};
            	countDownTimer.start();
        		mShouldStart = false;
        	}
            
            return rootView;
    	}
    	
    	@Override
    	 public void onDestroyView() {
    	     if (getDialog() != null && getRetainInstance())
    	         getDialog().setDismissMessage(null);
    	         super.onDestroyView();
    	 }
    	
    	public void show(FragmentManager manager, String tag) {
    		super.show(manager, tag);
    		mShouldStart = true;
    	}
    }
}
