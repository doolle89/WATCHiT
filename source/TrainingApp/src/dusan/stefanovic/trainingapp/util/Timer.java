package dusan.stefanovic.trainingapp.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

public class Timer {
	
	private final ScheduledExecutorService mScheduledExecutorService = Executors.newScheduledThreadPool(1);
	private final Handler mTimerHandler = new Handler();
	private ScheduledFuture<?> mScheduledFuture = null;
	private boolean mIsRunning;
	private boolean mIsStarted;
	private long mPeriod;
	private long mStartTime;
	private long mStopTime;
	
	private volatile long mElapsedTime;
	
	public Timer() {
		mIsRunning = false;
		mIsStarted = false;
		mPeriod = 1000;
		mElapsedTime = 0;
		mStartTime = 0;
        mStopTime = 0;
	}
	
	public void start() {
    	start(0, mPeriod);
    }
	
	public void start(long period) {
    	start(0, period);
    }
	
	public void start(long initialDelay, long period) {
    	if (!mIsStarted) {
	    	mStartTime = System.currentTimeMillis() + initialDelay;
	    	mElapsedTime = 0;
	    	startTimer(initialDelay, period);
	    	mPeriod = period;
	        mIsStarted = true;
	        if (initialDelay > 0) {
	        	onPrepare();
        	}
        	mTimerHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					onStart();
				}
        		
        	}, initialDelay);
	    } else {
	    	startTimer(0, mPeriod);
        	onResume();
	    }
    }
    
    public void stop(boolean pause) {
    	if (mIsStarted) {
    		stopTimer();
	    	mStopTime = System.currentTimeMillis();
	    	if (mStartTime > mStopTime) {
	    		mStopTime = mStartTime;
	    	}
	    	mIsStarted = pause;
	    	if (pause) {
        		onPause();
        	} else {
        		onStop();
        	}
    	}
    }
    
    public void reset() {
    	mElapsedTime = 0;
		mStartTime = 0;
        mStopTime = 0;
    }
    
    public void cancel() {
    	mScheduledExecutorService.shutdown();
    }
    
    private void startTimer(final long initialDelay, final long period) {
    	if (!mIsRunning) {
	    	mScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
	
	            @Override
	            public void run() {
	            	mElapsedTime += period;
	            	mTimerHandler.post(new Runnable() {

						@Override
						public void run() {
							if (mIsRunning) {
								onTick(mElapsedTime);
							}
						}
	            		
	            	});
	            }
	            
	        }, initialDelay, period, TimeUnit.MILLISECONDS);
	    	mIsRunning = true;
    	}
    }
    
    private void stopTimer() {
    	if (mIsRunning) {
	    	mScheduledFuture.cancel(true);
	    	mIsRunning = false;
    	}
    }

	public long getElapsedTime() {
		return mElapsedTime;
	}
	
	protected void onPrepare() {
		
	}

	protected void onStart() {
		
	}

	protected void onResume() {
		
	}

	protected void onTick(long elapsedTime) {
		
	}

	protected void onPause() {
		
	}

	protected void onStop() {
		
	}
}
