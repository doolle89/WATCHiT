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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
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
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.fragment.ProcedureListener;
import dusan.stefanovic.trainingapp.fragment.TrainingCurrentStepFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingOverviewFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingProgressFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingResultsFragment;
import dusan.stefanovic.trainingapp.fragment.TrainingStepsFragment;
import dusan.stefanovic.trainingapp.service.TrainingService;
import dusan.stefanovic.trainingapp.service.TrainingService.TrainingServiceListener;
import dusan.stefanovic.trainingapp.service.WATCHiTServiceInterface;
import dusan.stefanovic.treningapp.R;

public class TrainingActivity extends ActionBarActivity implements TabListener, ProcedureListener, TrainingServiceListener {
	
	ActionBar mActionBar;
    TabPagerAdapter mTabPagerAdapter;
    ViewPager mViewPager;
    Menu mMenu;
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

        mProcedure = getIntent().getParcelableExtra("procedure");
        if (mProcedure == null) {
        	finish();
        } else {
	        Intent intent = new Intent(this, TrainingService.class);
	        intent.putExtras(getIntent());
	        doBindAndStartService(intent);
        }
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        mActionBar = getSupportActionBar();

        // Specify Home/Up button 
        //actionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(mProcedure.getTitle());
        // Specify that we will be displaying tabs in the action bar.
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        //mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mTabPagerAdapter);
        mViewPager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            
        	@Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                mActionBar.setSelectedNavigationItem(position);
            }
        	
        });

        setUpTabsTabs();
        
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
    	mMenu = menu;
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
	        case R.id.action_reset_training:
	        	if (mBoundService != null) {
					mBoundService.resetTraining();
					changeTrainingView();
					updateProgress();
					updateTimer(0);
				} else {
					setIsBound(false);
				}
	        	showStartButton();
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
	public Procedure onProcedureRequested() {
		return mProcedure;
	}
    
    @Override
	public void onTrainingStart() {
    	performCountDown();
    	mStartButton.setVisibility(View.GONE);
    	showPauseButton();
	}
    
    @Override
	public void onTrainingStarted() {
    	updateSteps();
    	updateCurrentStep();
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
		changeTrainingView();
		updateSteps();
		showSelfAssessmentDialog();
		showRestartOption();
	}
	
	@Override
	public void onDeviceConnectionChanged(int connectionState) {
		setDeviceConnectionState(connectionState);
	}
    
    @Override
	public void onProgressUpdated() {
    	updateProgress();
    	updateSteps();
    	updateCurrentStep();
	}

	@Override
	public void onTimerTicked(long milliseconds) {
		updateTimer(milliseconds);
	}
	
	private void setUpTabsTabs() {
		mActionBar.removeAllTabs();
        for (int i = 0; i < mTabPagerAdapter.getCount(); i++) {
            mActionBar.addTab(mActionBar.newTab().setText(mTabPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
        mActionBar.setSelectedNavigationItem(TabPagerAdapter.TAB_2);
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
    	if (mBoundService != null) {
    		mProcedure = mBoundService.getProcedure();
    		updateProgress();
    		updateSteps();
    		updateState();
	        updateTimer(mBoundService.getElapsedTime());
	        setDeviceConnectionState(mBoundService.getDeviceConnectionState());
	        updateCurrentStep();
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
		    	mActionBar.setIcon(R.drawable.ic_disconnected);
		        break;
			case WATCHiTServiceInterface.DEVICE_CONNECTING:
				mActionBar.setIcon(R.drawable.ic_connecting);
		        break;
		    case WATCHiTServiceInterface.DEVICE_CONNECTED:
		    	mActionBar.setIcon(R.drawable.ic_connected);
		        break;
		}
	    
	    if (deviceConnectionState != WATCHiTServiceInterface.DEVICE_CONNECTED) {
	    	if (mProcedure.isStarted()) {
	    		// notifikacija ovde
	    	}
			// mStartButton.setEnabled(false);
			// mResumeButton.setEnabled(false);
		} else {
			mStartButton.setEnabled(true);
			mResumeButton.setEnabled(true);
		}
    }
    
    private void updateProgress() {
    	TrainingProgressFragment progressFragment = mTabPagerAdapter.getTrainingProgressFragment();
        if (progressFragment != null) {
        	progressFragment.update();
        }
    }

    private void updateTimer(long milliseconds) {
    	TrainingProgressFragment progressFragment = mTabPagerAdapter.getTrainingProgressFragment();
        if (progressFragment != null) {
        	progressFragment.updateTimer(milliseconds);
        }
    }
    
    private void updateSteps() {
    	TrainingStepsFragment stepsFragment = mTabPagerAdapter.getTrainingStepsFragment();
        if (stepsFragment != null) {
        	stepsFragment.update();
        }
    }
    
    private void updateCurrentStep() {
    	TrainingCurrentStepFragment currentStepFragment = mTabPagerAdapter.getTrainingCurrentStepFragment();
        if (currentStepFragment != null) {
        	currentStepFragment.update();
        }
    }
    
    private void updateState() {
    	if (isTrainingFinished()) {
    		showRestartOption();
    	} else if (!mProcedure.isStarted()) {
    		showStartButton();
    	} else if (mProcedure.isPaused()) {
    		showResumeButton();
    	} else if (mProcedure.isRunning()) {
    		showPauseButton();
    	}
    }
    
    private void showStartButton() {
    	mStartButton.setVisibility(View.VISIBLE);
    	setMeniOptionVisibility(R.id.action_stop_training, false);
        setMeniOptionVisibility(R.id.action_reset_training, false);
    }
    
    private void showResumeButton() {
    	mStartButton.setVisibility(View.GONE);
		mResumeButton.setVisibility(View.VISIBLE);
		mPauseButton.setVisibility(View.GONE);
		setMeniOptionVisibility(R.id.action_reset_training, false);
        setMeniOptionVisibility(R.id.action_stop_training, true);
    }
    
    private void showPauseButton() {
    	mStartButton.setVisibility(View.GONE);
		mResumeButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.VISIBLE);
		setMeniOptionVisibility(R.id.action_reset_training, false);
        setMeniOptionVisibility(R.id.action_stop_training, true);
    }
    
    private void showRestartOption() {
    	mResumeButton.setVisibility(View.GONE);
		mPauseButton.setVisibility(View.GONE);
    	setMeniOptionVisibility(R.id.action_stop_training, false);
    	setMeniOptionVisibility(R.id.action_reset_training, true);
    }
    
    private void setMeniOptionVisibility(int id, boolean isVisible) {
    	if (mMenu != null) {
    		MenuItem menuItem = mMenu.findItem(id);
    		menuItem.setVisible(isVisible);
    	}
    }
    
    private boolean isTrainingFinished() {
    	return !mProcedure.isStarted() && mProcedure.getStep(0).getStatus() != Step.STATUS_PENDING;
    }
    
    private void changeTrainingView() {
    	setUpTabsTabs();
    	mTabPagerAdapter.notifyDataSetChanged();
		
    }
    
    public class TabPagerAdapter extends FragmentStatePagerAdapter {
    	
    	static final int TAB_1 = 0;
    	static final int TAB_2 = 1;
    	static final int TAB_3 = 2;
    	
    	TrainingResultsFragment mTrainingResultsFragment;
    	TrainingOverviewFragment mTrainingOverviewFragment;

        public TabPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_1:
                	if (isTrainingFinished()) {
	                    return new TrainingResultsFragment();
                	} else {
	                    return new TrainingCurrentStepFragment();
                	}
                case TAB_2:
                	if (isTrainingFinished()) {
                		return new TrainingOverviewFragment();
                	} else {
	                	return new TrainingProgressFragment();
                	}
                case TAB_3:
                    return new TrainingStepsFragment();
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
	            case TAB_1:
	            	if (isTrainingFinished()) {
	            		return getText(R.string.training_activity_tab_results);
	            	} else {
	            		return getText(R.string.training_activity_tab_current_step);
	            	}
	            case TAB_2:
	            	if (isTrainingFinished()) { 
	            		return getText(R.string.training_activity_tab_overview);
	            	} else {
	            		return getText(R.string.training_activity_tab_progress);
	            	}
	            case TAB_3:
	                return getText(R.string.training_activity_tab_steps);
	        }
			return null;
        }
        
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        
        public TrainingCurrentStepFragment getTrainingCurrentStepFragment() {
        	List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
            	for (Fragment fragment : fragments) {
            		if (fragment instanceof TrainingCurrentStepFragment) {
            			return (TrainingCurrentStepFragment) fragment;
             		}
             	}
            }
            return null;
        }
        
        public TrainingProgressFragment getTrainingProgressFragment() {
	       	List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
            	for (Fragment fragment : fragments) {
            		if (fragment instanceof TrainingProgressFragment) {
            			return (TrainingProgressFragment) fragment;
            		}
            	}
            }
            return null;
	        }
        
        public TrainingStepsFragment getTrainingStepsFragment() {
        	List<Fragment> fragments = getSupportFragmentManager().getFragments();
       		if (fragments != null) {
       			for (Fragment fragment : fragments) {
       				if (fragment instanceof TrainingStepsFragment) {
       					return (TrainingStepsFragment) fragment;
       				}
       			}
       		}
       		return null;
        }
        
        public TrainingResultsFragment getTrainingResultsFragment() {
        	List<Fragment> fragments = getSupportFragmentManager().getFragments();
        	if (fragments != null) {
        		for (Fragment fragment : fragments) {
        			if (fragment instanceof TrainingResultsFragment) {
        				return (TrainingResultsFragment) fragment;
        			}
        		}
        	}
        	return null;
        }
        
        public TrainingOverviewFragment getTrainingOverviewFragment() {
           	List<Fragment> fragments = getSupportFragmentManager().getFragments();
           	if (fragments != null) {
           		for (Fragment fragment : fragments) {
           			if (fragment instanceof TrainingOverviewFragment) {
           				return (TrainingOverviewFragment) fragment;
           			}
           		}
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
	    	        Intent intent = new Intent(getActivity(), ReflectionActivity.class);
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
            View rootView = inflater.inflate(R.layout.dialog_fragment_countdown, container, false);
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
