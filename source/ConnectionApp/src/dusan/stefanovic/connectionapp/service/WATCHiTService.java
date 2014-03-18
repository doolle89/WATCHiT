
package dusan.stefanovic.connectionapp.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import dusan.stefanovic.connectionapp.R;
import dusan.stefanovic.connectionapp.connection.Connection;

public class WATCHiTService extends Service {
	
	private static final int NOTIFICATION_ID = 321421;
	
    /** Keeps track of all current registered clients. */
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();
    // Flag indicating is connecting mode on
    boolean mIsConnectingON = false;
    // Indicator when device should be reconnected in case
    // connection has been lost. Delay in ms and it's off if value < 0
    int mReconnectingAfter = 10000;
    // Device's physical address
    String mDeviceAddress;
    // Device connection state
    int mDeviceConnectionState;
    
    Notification mNotification;
    
    Connection mConnection;
    
    // Constants that indicate the current connection state
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    // Device message types
    public static final int READ = 11;
    public static final int WROTE = 12;
    public static final int DEVICE_NAME = 13;
    
    
    // Handler for handling messages from device (in particular case from Bluetooth Connection)
	static class DeviceHandler extends Handler {
		
		private final WeakReference<WATCHiTService> mWeakReference;
		
		Runnable mReconnectRunnable = new Runnable() {
    		
			@Override
			public void run() {
				WATCHiTService service = mWeakReference.get();
	        	if (service != null && service.mIsConnectingON) {
	        		service.reconnectDevice();
	        	}
			}
		
		};

		DeviceHandler(WATCHiTService service) {
			mWeakReference = new WeakReference<WATCHiTService>(service);
	    }
		
        @Override
        public void handleMessage(Message message) {
        	final WATCHiTService service = mWeakReference.get();
        	if (service != null) {
	            switch (message.what) {
		            case STATE_DISCONNECTED:
		                service.mDeviceConnectionState = WATCHiTServiceInterface.DEVICE_DISCONNECTED;
		            	// Toast.makeText(service, "Disconnected from device", Toast.LENGTH_SHORT).show();
		            	Message newMessage = Message.obtain(null, WATCHiTServiceInterface.DEVICE_DISCONNECTED);
		            	service.sendMessageToClients(newMessage);
		            	// Reconnect after delay
		            	if (service.mReconnectingAfter > 0 && service.mIsConnectingON) {
		            		startReconnecting();
		            	}
		            	service.updateNotification(R.drawable.ic_disconnected);
		            	Log.i("SERVICE", "STATE_DISCONNECTED");
		                break;
		            case STATE_CONNECTING:
		                service.mDeviceConnectionState = WATCHiTServiceInterface.DEVICE_CONNECTING;
		            	// Toast.makeText(service, "Connecting to device", Toast.LENGTH_SHORT).show();
		            	newMessage = Message.obtain(null, WATCHiTServiceInterface.DEVICE_CONNECTING);
		            	service.sendMessageToClients(newMessage);
		            	service.updateNotification(R.drawable.ic_connecting);
		            	Log.i("SERVICE", "STATE_CONNECTING");
		            	break;
		            case STATE_CONNECTED:
		                service.mDeviceConnectionState = WATCHiTServiceInterface.DEVICE_CONNECTED;
		            	// Toast.makeText(service, "Connected to device", Toast.LENGTH_SHORT).show();
		            	newMessage = Message.obtain(null, WATCHiTServiceInterface.DEVICE_CONNECTED);
		            	service.sendMessageToClients(newMessage);
		            	service.updateNotification(R.drawable.ic_connected);
		            	Log.i("SERVICE", "STATE_CONNECTED");
		            	break;
	                case READ:
	                	newMessage = Message.obtain(null, WATCHiTServiceInterface.TAG_READ);
	            		Bundle bundle = new Bundle();
	            		bundle.putByteArray(WATCHiTServiceInterface.TAG_DATA_CONTENT, (byte[]) message.obj);
	            		bundle.putInt(WATCHiTServiceInterface.TAG_DATA_LENGTH, message.arg1);
	            		newMessage.setData(bundle);
	            		service.sendMessageToClients(newMessage);
	                	//Toast.makeText(service, "Read: " + text, Toast.LENGTH_SHORT).show();
	                	/*
	                    byte[] readBuf = (byte[]) msg.obj;
	                    // construct a string from the valid bytes in the buffer
	                    String readMessage = new String(readBuf, 0, msg.arg1);
	                    mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
	                    */
	                    break;
	                case WROTE:
	                	// Toast.makeText(service, "Wrote to device", Toast.LENGTH_SHORT).show();
	                	/*
	                    byte[] writeBuf = (byte[]) msg.obj;
	                    // construct a string from the buffer
	                    String writeMessage = new String(writeBuf);
	                    mConversationArrayAdapter.add("Me:  " + writeMessage);
	                    */
	                    break;
	                case DEVICE_NAME:
	                	// Toast.makeText(service, "Device name", Toast.LENGTH_SHORT).show();
	                    // save the connected device's name
	                    /* 
	                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                    */
	                    break;
	                    
	                default:
	                    super.handleMessage(message);
	            }
        	}
        }
        
        public void startReconnecting() {
        	final WATCHiTService service = mWeakReference.get();
        	if (service != null) {
        		postDelayed(mReconnectRunnable, service.mReconnectingAfter);
        	}
        }
        
        public void stopReconnecting() {
            removeCallbacks(mReconnectRunnable);
        }
    }
    
    /**
     * Handler for handling messages from clients, others software components.
     */
	static class ClientHandler extends Handler {
		
		private final WeakReference<WATCHiTService> mWeakReference; 

		ClientHandler(WATCHiTService service) {
			mWeakReference = new WeakReference<WATCHiTService>(service);
	    }
		
        @Override
        public void handleMessage(Message message) {
        	final WATCHiTService service = mWeakReference.get();
        	if (service != null) {
	            switch (message.what) {
	                case WATCHiTServiceInterface.REGISTER_CLIENT:
	                	if (!service.mClients.contains(message.replyTo)) {
	                		service.mClients.add(message.replyTo);
	                		try {
	                			Message newMessage = Message.obtain(null, WATCHiTServiceInterface.CLIENT_REGISTERED);
								message.replyTo.send(newMessage);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
		                    service.sendUpdate(message.replyTo);
	                	}
	                    break;
	                case WATCHiTServiceInterface.UNREGISTER_CLIENT:
	                	if (service.mClients.remove(message.replyTo)) {
		                	try {
		                		Message newMessage = Message.obtain(null, WATCHiTServiceInterface.CLIENT_UNREGISTERED);
		                		message.replyTo.send(newMessage);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
	                	}
	                    break;
	                case WATCHiTServiceInterface.START_SERVICE:
                		Intent intent = new Intent(service, WATCHiTService.class);
                		intent.putExtras(message.getData());
                    	service.startService(intent);
	                    break;
	                case WATCHiTServiceInterface.STOP_SERVICE:
                		service.stopConnectingToDevice();
                		Message newMessage = Message.obtain(null, WATCHiTServiceInterface.SERVICE_STOPPED);
                    	service.sendMessageToClients(newMessage);
                    	service.stopSelf();
	                    break;
	                case WATCHiTServiceInterface.REQUEST_UPDATE:
	                	service.sendUpdate(message.replyTo);
	                    break;
	                    
	                default:
	                    super.handleMessage(message);
	            }
        	}
        }
    }
    
    final DeviceHandler mDeviceHandler = new DeviceHandler(this);
    final Messenger mClientMessenger = new Messenger(new ClientHandler(this));
    
    // Broadcast receiver for monitoring Bluetooth status
    
    
    @Override
    public void onCreate() {
        mConnection = Connection.Factory.createBluetoothConnection(mDeviceHandler);
        mDeviceConnectionState = WATCHiTServiceInterface.DEVICE_DISCONNECTED;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
    	if (!mIsConnectingON) {
            mConnection.registerStateChangeBroadcastReceiver(this);
            // String deviceAddress = intent.getExtras().getString(WATCHiTServiceInterface.DEVICE_ADDRESS);
            SharedPreferences sharedPreferences = getSharedPreferences("WATCHiT", 0);
            String deviceAddress = sharedPreferences.getString(WATCHiTServiceInterface.DEVICE_ADDRESS, "00:00:00:00:00:00");
    		if (intent.hasExtra(WATCHiTServiceInterface.DEVICE_ADDRESS)) {
    			deviceAddress = intent.getStringExtra(WATCHiTServiceInterface.DEVICE_ADDRESS);
    			SharedPreferences.Editor editor = sharedPreferences.edit();
    		    editor.putString(WATCHiTServiceInterface.DEVICE_ADDRESS, deviceAddress);
    		    editor.commit();
    		}
    		startConnectingToDevice(deviceAddress);
    		Message newMessage = Message.obtain(null, WATCHiTServiceInterface.SERVICE_STARTED);
        	sendMessageToClients(newMessage);
    		if(!mConnection.isEnabled()) {
    			Intent newIntent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SETTINGS);
        		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        		startActivity(newIntent);
    		}
        }
        return START_REDELIVER_INTENT;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mClientMessenger.getBinder();
    }
    
    @Override
    public void onDestroy() {
    	// Disconnect device
    	stopConnectingToDevice();
        // Tell the user we stopped.
        // Toast.makeText(this, "DESTROYED", Toast.LENGTH_SHORT).show();
        try {
    		mConnection.unregisterStateChangeBroadcastReceiver(this);
    	} catch (IllegalArgumentException  e) {
    		
		} 
    }

    private void doStartForeground() {
    	Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SETTINGS);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotification = new NotificationCompat.Builder(this)
								        .setContentTitle(getText(R.string.remote_service_label))
								        .setContentText(getText(R.string.remote_service_context))
								        .setSmallIcon(R.drawable.stat_sample)
								        .setTicker(getText(R.string.remote_service_started))
								        .setWhen(System.currentTimeMillis())
								        .setContentIntent(pendingIntent)
								        .build();
        startForeground(NOTIFICATION_ID, mNotification);
        updateNotification(R.drawable.ic_disconnected);
    }
    
    private void updateNotification(int icon) {
    	if (mIsConnectingON) {
	    	mNotification.icon = icon;
	    	((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, mNotification);
    	}
    }
    
    private void startConnectingToDevice(String deviceAddress) {
        // Get the device MAC address
        mDeviceAddress = deviceAddress;
		mIsConnectingON = true;
        doStartForeground();
        mConnection.connect(mDeviceAddress);
    }
    
    private void reconnectDevice() {
        if (mDeviceAddress != null) {
	        // Attempt to connect to the device
	        mConnection.connect(mDeviceAddress);
        }
    }
    
    private void stopConnectingToDevice() {
        // Disconnect from the device
    	mIsConnectingON = false;
    	mDeviceHandler.stopReconnecting();
        mConnection.stop();
        stopForeground(true);
    }
    
    private void sendUpdate(Messenger messenger) {
    	try {
    		Message newMessage = Message.obtain(null, WATCHiTServiceInterface.UPDATE);
    		Bundle bundle = new Bundle();
    		bundle.putBoolean(WATCHiTServiceInterface.IS_DEVICE_AVAILABLE, mConnection.isAvailable());
    		bundle.putBoolean(WATCHiTServiceInterface.IS_DEVICE_ENABLED,mConnection.isEnabled());
    		bundle.putBoolean(WATCHiTServiceInterface.IS_CONNECTING_TO_DEVICE, mIsConnectingON);
    		bundle.putInt(WATCHiTServiceInterface.DEVICE_CONNECTION_STATUS, mDeviceConnectionState);
			newMessage.setData(bundle);
			messenger.send(newMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
    
    private void sendMessageToClients(Message message) {
    	for (int i = mClients.size()-1; i >=0 ; i--) {
            try {
                mClients.get(i).send(message);
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }
    
	
}

