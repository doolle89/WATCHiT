/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dusan.stefanovic.virtualwatchit;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class MainActivity extends Activity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    
    // WATCHiT signal messages
    private static final String SIGNAL_COMPLETE = "c";
    private static final String SIGNAL_ERROR = "e";
    private static final String SIGNAL_SKIP = "s";

    private Button mCompleteButton;
    private Button mErrorButton;
    private Button mSkipButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothConnectionServer mBluetoothConnectionServer = null;
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new IncomingHandler(this);
    	
    static class IncomingHandler extends Handler {
    	
    	private final WeakReference<MainActivity> mWeakReference; 

		IncomingHandler(MainActivity activity) {
			mWeakReference = new WeakReference<MainActivity>(activity);
	    }
		
        @Override
        public void handleMessage(Message msg) {
        	final MainActivity activity = mWeakReference.get();
        	if (activity != null) {
	            switch (msg.what) {
	            case MESSAGE_STATE_CHANGE:
	                switch (msg.arg1) {
	                case BluetoothConnectionServer.STATE_CONNECTED:
	                	
	                    break;
	                case BluetoothConnectionServer.STATE_LISTEN:
	                case BluetoothConnectionServer.STATE_NONE:
	                	
	                    break;
	                }
	                break;
	            case MESSAGE_WRITE:
	                // construct a string from the buffer
	            	
	                // byte[] writeBuf = (byte[]) msg.obj;
	                // String writeMessage = new String(writeBuf);
	                break;
	            case MESSAGE_READ:
	            	// construct a string from the valid bytes in the buffer
	            	
	                // byte[] readBuf = (byte[]) msg.obj;
	                // String readMessage = new String(readBuf, 0, msg.arg1);
	                break;
	            case MESSAGE_DEVICE_NAME:
	                // save the connected device's name
	                activity.mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                Toast.makeText(activity.getApplicationContext(), "Connected to " + activity.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                break;
	            case MESSAGE_TOAST:
	                Toast.makeText(activity.getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
	                break;
	            }
        	}
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the window layout
        setContentView(R.layout.activitiy_main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        mCompleteButton = (Button) findViewById(R.id.button_complete);
    	mCompleteButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage(SIGNAL_COMPLETE);
            }
    		
        });
    	
    	mErrorButton = (Button) findViewById(R.id.button_error);
    	mErrorButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage(SIGNAL_ERROR);
            }
    		
        });
    	
    	mSkipButton = (Button) findViewById(R.id.button_skip);
    	mSkipButton.setOnClickListener(new OnClickListener() {
    		
    		@Override
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                sendMessage(SIGNAL_SKIP);
            }
    		
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mBluetoothConnectionServer = new BluetoothConnectionServer(this, mHandler);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        // Otherwise, setup the chat session
        } else {
            if (mBluetoothConnectionServer != null) {
            	setDiscoverable(true);
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetoothConnectionServer != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothConnectionServer.getState() == BluetoothConnectionServer.STATE_NONE) {
            	// Start the Bluetooth chat services
            	mBluetoothConnectionServer.start();
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        
    }

    @Override
    public void onStop() {
        super.onStop();
        setDiscoverable(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBluetoothConnectionServer != null) {
        	mBluetoothConnectionServer.stop();
        }
    }

    private void setDiscoverable(boolean isDiscoverable) {
    	if (isDiscoverable) {
	        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	            startActivity(discoverableIntent);
	        }
    	} else {
    		if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	    		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
	            // startActivity(discoverableIntent);
    		}
    	}
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBluetoothConnectionServer.getState() != BluetoothConnectionServer.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBluetoothConnectionServer.write(send);
            ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ENABLE_BLUETOOTH:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled
            	setDiscoverable(true);
            	
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
