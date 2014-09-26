package dusan.stefanovic.watchitlogger;

import java.lang.ref.WeakReference;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import dusan.stefanovic.connectionapp.service.WATCHiTServiceInterface;

public class BaseActivity extends ActionBarActivity {
	
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
	
	static class IncomingHandler extends Handler {
		
		private final WeakReference<BaseActivity> mWeakReference; 

		IncomingHandler(BaseActivity activity) {
			mWeakReference = new WeakReference<BaseActivity>(activity);
	    }
		
        @Override
        public void handleMessage(Message message) {
        	final BaseActivity activity = mWeakReference.get();
        	if (activity != null) {
	            switch (message.what) {
		            case WATCHiTServiceInterface.SERVICE_STARTED:
		            	activity.setIsStarted(true);
		                break;
		            case WATCHiTServiceInterface.SERVICE_STOPPED:
		            	activity.setIsStarted(false);
			            break;
		            case WATCHiTServiceInterface.CLIENT_REGISTERED:
		            	activity.setIsBound(true);
		            	activity.requestUpdate();
		                break;
		            case WATCHiTServiceInterface.CLIENT_UNREGISTERED:
		            	activity.setIsBound(false);
		                break;
		            case WATCHiTServiceInterface.DEVICE_DISCONNECTED:
		            	activity.setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_DISCONNECTED);
				        break;
		            case WATCHiTServiceInterface.DEVICE_CONNECTING:
		            	activity.setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_CONNECTING);
		                break;
		            case WATCHiTServiceInterface.DEVICE_CONNECTED:
		            	activity.setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_CONNECTED);
			            break;
		            case WATCHiTServiceInterface.UPDATE:
		            	activity.onUpdate(message.getData());
		            	break;
		            case WATCHiTServiceInterface.TAG_READ:
		            	byte[] data = message.getData().getByteArray(WATCHiTServiceInterface.TAG_DATA_CONTENT);
		            	int length = message.getData().getInt(WATCHiTServiceInterface.TAG_DATA_LENGTH);
		            	activity.onDataReceived(data, length);
		            	break;
		            	
		            default:
		                super.handleMessage(message);
	            }
        	}
        }
    }
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    
    private ServiceConnection mConnection = new ServiceConnection() {
    	
    	@Override
        public void onServiceConnected(ComponentName className, IBinder service) {
        	mServiceMessenger = new Messenger(service);
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

    	@Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
        	mServiceMessenger = null;
        	setIsBound(false);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SERVICE);
        doBindAndStartService(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        doStopService();
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SETTINGS);
        	startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
    
    private void onUpdate(Bundle data) {
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
	}

	public void onDataReceived(byte[] data, int length) {
		
	}
}
