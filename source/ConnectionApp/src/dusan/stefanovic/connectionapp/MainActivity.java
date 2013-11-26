package dusan.stefanovic.connectionapp;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import dusan.stefanovic.connectionapp.service.WATCHiTServiceInterface;

public class MainActivity extends Activity {
	
	// Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;
	
    // Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

	// Layout Views
	private ToggleButton toggleButton;
	private ImageView imageView;

    // Member fields
	private BluetoothAdapter bluetoothAdapter;
	
	// Messenger for communicating with service
    Messenger mServiceMessenger = null;
    // Flag indicating whether we have called bind on the service
    boolean mIsBindCalled;
    // Flag indicating whether we are bound to the service
    boolean mIsBound;
    // Flag indicating whether service is started
    boolean mIsStarted;
    // Device connection state
    int mDeviceConnectionState;
    
    
    /**
     * Handler of incoming messages from service.
     */
	static class IncomingHandler extends Handler {
		
		private final WeakReference<MainActivity> mWeakReference; 

		IncomingHandler(MainActivity activity) {
			mWeakReference = new WeakReference<MainActivity>(activity);
	    }
		
        @Override
        public void handleMessage(Message message) {
        	final MainActivity activity = mWeakReference.get();
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
		            	// activity.requestUpdate();
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
		            	activity.update(message.getData());
		            	break;
		            default:
		                super.handleMessage(message);
	            }
        	}
        }
    }
    
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    
    /**
     * Class for interacting with the main interface of the service.
     */
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
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            // Toast.makeText(MainActivity.this, R.string.remote_service_bound, Toast.LENGTH_SHORT).show();
        }

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
		setContentView(R.layout.activity_main);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
            // Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
		
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		toggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (toggleButton.isChecked()) {
					if (bluetoothAdapter.isEnabled()) {
						startDeviceListActivity();
					}
					else {
					    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					}
				}
				else {
					doStopService();
					//doUnbindService();
				}
				toggleButton.setChecked(false);
			}
		});
		
		imageView = (ImageView) findViewById(R.id.imageView);
		
		doBindService();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		requestUpdate();
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            if (resultCode == Activity.RESULT_OK) {
            	String address = data.getExtras().getString(WATCHiTServiceInterface.DEVICE_ADDRESS);
                // Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
    			Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SERVICE);
    			intent.putExtras(data);
                doStartService(intent);
            }
            else {
                // Device not selected
                Log.d(TAG, "Device not selected");
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
            	if (!mIsStarted) {
            		startDeviceListActivity();
            	}
            } 
            else {
            	doStopService();
                Toast.makeText(this, R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
            }
        }
    }

	private void setIsBound(boolean isBound) {
		mIsBound = isBound;
		if (!mIsBound) {
			setIsStarted(false);
		}
	}
	
	private void setIsStarted(boolean isStarted) {
		mIsStarted = isStarted;
		toggleButton.setChecked(mIsStarted);
		if (!mIsStarted) {
            setDeviceConnectionState(WATCHiTServiceInterface.DEVICE_DISCONNECTED);
		} else if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	private void setDeviceConnectionState(int deviceConnectionState) {
		mDeviceConnectionState = deviceConnectionState;
		switch (mDeviceConnectionState) {
			case WATCHiTServiceInterface.DEVICE_DISCONNECTED:
	        	imageView.setImageResource(R.drawable.ic_disconnected);
		        break;
			case WATCHiTServiceInterface.DEVICE_CONNECTING:
	        	imageView.setImageResource(R.drawable.ic_connecting);
	            break;
	        case WATCHiTServiceInterface.DEVICE_CONNECTED:
	        	imageView.setImageResource(R.drawable.ic_connected);
	            break;
		}
	}

	private void startDeviceListActivity() {
		// Launch the DeviceListActivity to see devices and do scan
		Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE); 
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
    	// stopService(new Intent(WATCHiTServiceInterface.INTENT_ACTION));
    	if (mServiceMessenger != null) {
	        try {
	        	Message message = Message.obtain(null, WATCHiTServiceInterface.STOP_SERVICE);
				mServiceMessenger.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
    }
    
    private void doBindService() {
    	if (!mIsBindCalled) {
		    // Establish a connection with the service.  We use an explicit
		    // class name because there is no reason to be able to let other
		    // applications replace our component.
		    bindService(new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SERVICE), mConnection, Context.BIND_AUTO_CREATE);
		    mIsBindCalled = true;
		    // Toast.makeText(MainActivity.this, getText(R.string.remote_service_binding), Toast.LENGTH_SHORT).show();
    	}
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
            // Toast.makeText(MainActivity.this, getText(R.string.remote_service_unbound), Toast.LENGTH_SHORT).show();
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

}
