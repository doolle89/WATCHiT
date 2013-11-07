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
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.fragment.TrainingCurrentStepFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingProgressFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingStepsFragment;
import dusan.stefanovic.trainingapp.service.TrainingService;
import dusan.stefanovic.trainingapp.service.WATCHiTServiceInterface;
import dusan.stefanovic.trainingapp.service.TrainingService.TrainingServiceListener;
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
					mBoundService.startTraining(mProcedure);
				}
			}
		});
        
        mResumeButton = (Button) findViewById(R.id.resume_button);
        mResumeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mBoundService != null) {
					mBoundService.resumeTraining();
				}
			}
		});
        
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mPauseButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mBoundService != null) {
					mBoundService.pauseTraining();
				}
			}
		});
                
        
        
        mProcedure = getIntent().getParcelableExtra("Procedure");
        
        doBindAndStartService(new Intent(this, TrainingService.class));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	tryToQuitTrainingActivity();
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
	public void onTrainingPrepared() {
    	performCountDown();
	}
    
    @Override
	public void onTrainingStarted() {
		mStartButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onTrainingResumed() {
		mResumeButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onTrainingPaused() {
		mPauseButton.setVisibility(View.GONE);
		mResumeButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onTrainingStopped() {
		mStartButton.setVisibility(View.VISIBLE);
		mResumeButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.GONE);
	}
	
	@Override
	public void onDeviceConnectionChanged(int connectionState) {
		switch (connectionState) {
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
	}
    
    @Override
	public void onProgressChanged(int progress) {
    	mSectionsPagerAdapter.mTrainingProgressFragment.updateProgress(progress);
	}

	@Override
	public void onTimerTicked(long milliseconds) {
		mSectionsPagerAdapter.mTrainingProgressFragment.updateTimer(milliseconds);
	}
	
	private void tryToQuitTrainingActivity() {
		if (mBoundService != null && mBoundService.isTrainingStarted()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle("Quit training?");
	    	builder.setMessage("Are you sure you want to quit training?");
	    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    	        stopService(new Intent(TrainingActivity.this, TrainingService.class));
	    			finish();
	            }
	    		
	    	});
	        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        		
	            }
	        	
	        });
	        builder.show();
		} else {
	        stopService(new Intent(this, TrainingService.class));
			finish();
		}
	}
	
	public void performCountDown() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
    	final AlertDialog dialog = builder.show();
    	dialog.setContentView(R.layout.dialog_countdown);
    	final TextView textView = (TextView) dialog.findViewById(android.R.id.message);
    	new CountDownTimer(5000, 1000) {
    		
    		@Override
    		public void onTick(long millisUntilFinished) {
    			long secondsUntilFinished = (millisUntilFinished / 1000) - 1;
    			textView.setText(String.valueOf(secondsUntilFinished));
    		}
    		
    		@Override
    		public void onFinish() {
    			dialog.dismiss();
    		}
    		
    	}.start();
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
    
    private void setIsBound(boolean isBound) {
		mIsBound = isBound;
		if (!mIsBound) {
			//setIsStarted(false);
		}
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
}
