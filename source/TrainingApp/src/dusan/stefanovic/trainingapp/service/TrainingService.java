package dusan.stefanovic.trainingapp.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import dusan.stefanovic.connectionapp.service.WATCHiTServiceInterface;
import dusan.stefanovic.trainingapp.TrainingActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.util.Timer;
import dusan.stefanovic.treningapp.R;

public class TrainingService extends Service {
    // Messenger for communicating with remote service
    Messenger mServiceMessenger = null;
    // Flag indicating whether we have called bind on the service
    boolean mIsBindCalled;
    // Flag indicating whether we are bound to the service
    boolean mIsBound;
    // Flag indicating whether service is started
    boolean mIsStarted;
    // Device connection state
    int mDeviceConnectionState;
    // Intent for starting service after it's already bound
    Intent mDelayedStartServiceIntent;
    
    Procedure mProcedure;
    

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	public TrainingService getService() {
            return TrainingService.this;
        }
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    
    // Handler of incoming messages from service
	static class IncomingHandler extends Handler {
		
		private final WeakReference<TrainingService> mWeakReference; 

		IncomingHandler(TrainingService service) {
			mWeakReference = new WeakReference<TrainingService>(service);
	    }
		
        @Override
        public void handleMessage(Message message) {
        	final TrainingService service = mWeakReference.get();
        	if (service != null) {
	            switch (message.what) {
		            case WATCHiTServiceInterface.SERVICE_STARTED:
		            	service.setIsStarted(true);
		                break;
		            case WATCHiTServiceInterface.SERVICE_STOPPED:
		            	service.setIsStarted(false);
			            break;
		            case WATCHiTServiceInterface.CLIENT_REGISTERED:
		            	service.setIsBound(true);
		            	service.requestUpdate();
		                break;
		            case WATCHiTServiceInterface.CLIENT_UNREGISTERED:
		            	service.setIsBound(false);
		                break;
		            case WATCHiTServiceInterface.DEVICE_DISCONNECTED:
		            	service.setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_DISCONNECTED);
				        break;
		            case WATCHiTServiceInterface.DEVICE_CONNECTING:
		            	service.setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_CONNECTING);
		                break;
		            case WATCHiTServiceInterface.DEVICE_CONNECTED:
		            	service.setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_CONNECTED);
			            break;
		            case WATCHiTServiceInterface.UPDATE:
		            	service.update(message.getData());
		            	break;
		            case WATCHiTServiceInterface.TAG_READ:
		            	byte[] data = message.getData().getByteArray(WATCHiTServiceInterface.TAG_DATA_CONTENT);
		            	int length = message.getData().getInt(WATCHiTServiceInterface.TAG_DATA_LENGTH);
		            	service.processData(data, length);
		            	break;
		            	
		            default:
		                super.handleMessage(message);
	            }
        	}
        }
    }
    
    // Target we publish for clients to send messages to IncomingHandler.
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    
    // Class for interacting with the main interface of the service
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
        	mServiceMessenger = new Messenger(service);

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message message = Message.obtain(null, WATCHiTServiceInterface.REGISTER_CLIENT);
                message.replyTo = mMessenger;
                mServiceMessenger.send(message);
                
                // start service with the intent after it's already bound
                if (mDelayedStartServiceIntent != null) {
                	doStartService(mDelayedStartServiceIntent);
                	mDelayedStartServiceIntent = null;
                }
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
        	mServiceMessenger = null;
        	setIsBound(false);
        }
    };
    
    final Timer mTimer = new Timer() {
    	
    	@Override
    	protected void onPrepare() {
			trainingStart();
    	}
    	
    	@Override
    	protected void onStart() {
    		mProcedure.start();
    		trainingStarted();
    	}
    	
    	@Override
    	protected void onResume() {
    		trainingResumed();
    	}
    	
    	@Override
    	protected void onTick(long elapsedTime) {
    		timerTicked(getElapsedTime());
    	}
    	
    	@Override
    	protected void onPause() {
    		trainingPaused();
    	}
    	
    	@Override
    	protected void onStop() {
    		trainingStopped();
    	}
    	
    };
    
    @Override
    public void onCreate() {
        Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SERVICE);
        if (isServiceCallable(intent)) {
        	doBindAndStartService(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        return START_NOT_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        doStopService();
        doUnbindService();
        mTimer.cancel();
    }

    // Show a notification while this service is running.
    private void doStartForeground() {
    	Intent intent = new Intent(this, TrainingActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
								        .setContentTitle(getText(R.string.training_service_label))
								        .setContentText(getText(R.string.training_service_context))
								        .setSmallIcon(R.drawable.stat_sample)
								        .setTicker(getText(R.string.training_service_started))
								        .setWhen(System.currentTimeMillis())
								        .setContentIntent(pendingIntent)
								        .build();
        startForeground(R.drawable.stat_sample, notification);
    }
    
    private boolean isServiceCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentServices(intent, 0);
        return list.size() > 0;
    }
    
    private void doStartService(Intent intent) {
		if (mServiceMessenger != null) {
	        try {
	        	Message message = Message.obtain(null, WATCHiTServiceInterface.START_SERVICE);
	        	message.setData(intent.getExtras());
				mServiceMessenger.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
	    	startService(intent);
		}
    }
    
    private void doStopService() {
    	if (mServiceMessenger != null) {
	        try {
	        	Message message = Message.obtain(null, WATCHiTServiceInterface.STOP_SERVICE);
				mServiceMessenger.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
				stopService(new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SERVICE));
			}
		} else {
			stopService(new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SERVICE));
		}
    }
    
    private void doBindService(Intent intent) {
    	if (!mIsBindCalled) {
		    // Establish a connection with the service.  We use an explicit
		    // class name because there is no reason to be able to let other
		    // applications replace our component.
    		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    		mIsBindCalled = true;
    	}
    }
    
    private void doBindAndStartService(Intent intent) {
    	mDelayedStartServiceIntent = intent;
    	doBindService(intent);
    }
    
    private void doUnbindService() {
        if (mIsBindCalled) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message message = Message.obtain(null, WATCHiTServiceInterface.UNREGISTER_CLIENT);
                    message.replyTo = mMessenger;
                    mServiceMessenger.send(message);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBindCalled = false;
            setIsBound(false);
        }
    }
    
    private void requestUpdate() {
	    if (mServiceMessenger != null) {
	        try {
	        	Message message = Message.obtain(null, WATCHiTServiceInterface.REQUEST_UPDATE);
				message.replyTo = mMessenger;
	        	mServiceMessenger.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
    }
    
    private void update(Bundle data) {
    	data.setClassLoader(getClassLoader());
    	setIsStarted(data.getBoolean(WATCHiTServiceInterface.IS_CONNECTING_TO_DEVICE, false));
    	setDeviceConnectionState(data.getInt(WATCHiTServiceInterface.DEVICE_CONNECTION_STATUS, WATCHiTServiceInterface.DEVICE_DISCONNECTED));
    }
    
	private void setIsBound(boolean isBound) {
		mIsBound = isBound;
		if (!mIsBound) {
			setIsStarted(false);
		}
	}
	
	private void setIsStarted(boolean isStarted) {
		mIsStarted = isStarted;
		if (!mIsStarted) {
			setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_DISCONNECTED);
		}
	}

	private void setDeviceConnectionState(int deviceConnectionState) {
		mDeviceConnectionState = deviceConnectionState;
		deviceConnectionChanged(deviceConnectionState);
		if (mProcedure != null && mProcedure.isRunning() && deviceConnectionState != WATCHiTServiceInterface.DEVICE_CONNECTED) {
			pauseTraining();
		}
	}
	
    public void startTraining() {
    	if (!mProcedure.isStarted()) {
    		mProcedure.reset();
	    	mTimer.start(5000, 100);
	    	doStartForeground();
    	}
    }
    
    public void resumeTraining() {
    	if (mProcedure.isPaused()) {
    		mProcedure.start();
	    	mTimer.start();
    	}
    }
    
    public void pauseTraining() {
    	if (mProcedure.isRunning()) {
    		mProcedure.pause();
	    	mTimer.stop(true);
    	}
    }
    
    public void stopTraining() {
    	if (mProcedure.isStarted()) {
    		mProcedure.stop();
	    	mTimer.stop(false);
	    	stopForeground(true);
    	}
    }
    
    public void resetTraining() {
    	stopTraining();
    	mProcedure.reset();
    }
    
    public void processData(byte[] data, int length) {
    	String tagValue = new String(data, 0, length);
    	
    	//Toast.makeText(this, "Tag: " + tagValue, Toast.LENGTH_SHORT).show();
    	
    	
    	if (tagValue.contains("rescued someone")) {
    		tagValue = "c";
    	} else if (tagValue.contains("sad")) {
    		tagValue = "e";
    	} else if (tagValue.contains("so and so")) {
    		tagValue = "s";
    	} else if (tagValue.contains("happy")) {
    		tagValue = "s";
    	}
    	
    	
    	
    	boolean updateProcedure = false;
    	if (tagValue.equalsIgnoreCase("c")) {
    		updateProcedure = mProcedure.completeCurrentStep();
    	} else if (tagValue.equalsIgnoreCase("e")) {
    		updateProcedure = mProcedure.addErrorToCurrentStep();
    	} else if (tagValue.equalsIgnoreCase("s")) {
    		updateProcedure = mProcedure.skipCurrentStep();
    	} else {
    		try {
	    		int stepIndex = Integer.parseInt(tagValue);
	    		updateProcedure = mProcedure.completeStepAtIndex(stepIndex);
    		} catch (NumberFormatException e) {
    			e.printStackTrace();
    		}
    	}
    	if (updateProcedure) {
			progressUpdated();
	    	if(!mProcedure.isStarted()) {
	    		mTimer.stop(false);
		    	stopForeground(true);
	    	}
		}
		
    }
	
	public int getDeviceConnectionState() {
		return mDeviceConnectionState;
	}

	public Procedure getProcedure() {
		return mProcedure;
	}
	
	public Procedure setProcedure(Procedure procedure) {
		if (mProcedure == null) {
			mProcedure = procedure;
		}
		return mProcedure;
	}
    
	public long getElapsedTime() {
		return mTimer.getElapsedTime();
	}
	
    
    // Listener ---------------------------------------------------
    

	public interface TrainingServiceListener {
    	public void onTrainingStart();
    	public void onTrainingStarted();
    	public void onTrainingResumed();
    	public void onTrainingPaused();
    	public void onTrainingStopped();
    	public void onProgressUpdated();
    	public void onDeviceConnectionChanged(int connectionState);
    	public void onTimerTicked(long milliseconds);
    }
    
    final ArrayList<TrainingServiceListener> mTrainingServiceListeners = new ArrayList<TrainingServiceListener>();
    
    public boolean registerTrainingServiceListener(TrainingServiceListener trainingServiceListener) {
    	if (!mTrainingServiceListeners.contains(trainingServiceListener)) {
    		mTrainingServiceListeners.add(trainingServiceListener);
    		return true;
    	}
    	return false;
    }
    
    public boolean unregisterTrainingServiceListener(TrainingServiceListener trainingServiceListener) {
    	return mTrainingServiceListeners.remove(trainingServiceListener);
    }
    
    public void trainingStart() {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onTrainingStart();
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void trainingStarted() {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onTrainingStarted();
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void trainingResumed() {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onTrainingResumed();
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void trainingPaused() {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onTrainingPaused();
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void trainingStopped() {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onTrainingStopped();
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void deviceConnectionChanged(int connectionState) {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onDeviceConnectionChanged(connectionState);
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void timerTicked(long milliseconds) {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onTimerTicked(milliseconds);
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
    
    public void progressUpdated() {
    	for (int i = mTrainingServiceListeners.size()-1; i >=0 ; i--) {
            try {
            	mTrainingServiceListeners.get(i).onProgressUpdated();
            } catch (Exception e) {
            	mTrainingServiceListeners.remove(i);
            }
        }
    }
}
