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

package dusan.stefanovic.connectionapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothConnection {
    // Debugging
    private static final String TAG = "BluetoothConnection";
    private static final boolean D = true;

    // Unique UUID for this application
    private static final UUID WATCHiT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothConnection(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = WATCHiTService.STATE_DISCONNECTED;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        
        if (mState != state) {
	        mState = state;
	        mHandler.obtainMessage(state).sendToTarget();
        }
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }
    
    public synchronized void connect(String deviceAddress) {
    	if (mAdapter != null) {
	    	// Get the BluetoothDevice object
	        BluetoothDevice device = mAdapter.getRemoteDevice(deviceAddress);
	        connect(device);
    	}
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
    	if (mAdapter != null) {
	        if (D) Log.d(TAG, "connect to: " + device);
	
	        // Cancel any thread attempting to make a connection
	        if (mState == WATCHiTService.STATE_CONNECTING) {
	            if (mConnectThread != null) {
	            	mConnectThread.cancel();
	            	mConnectThread = null;
	            }
	        }
	
	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {
	        	mConnectedThread.cancel();
	        	mConnectedThread = null;
	        }
	
	        // Start the thread to connect with the given device
	        mConnectThread = new ConnectThread(device);
	        mConnectThread.start();
	        setState(WATCHiTService.STATE_CONNECTING);
    	}
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
        	mConnectThread.cancel();
        	mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
        	mConnectedThread.cancel();
        	mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        setState(WATCHiTService.STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(WATCHiTService.STATE_DISCONNECTED);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != WATCHiTService.STATE_CONNECTED) {
            	return;
            }
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(WATCHiTService.STATE_DISCONNECTED);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        setState(WATCHiTService.STATE_DISCONNECTED);
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        volatile boolean mIsRunning;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mIsRunning = false;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
            	tmp = device.createRfcommSocketToServiceRecord(WATCHiT_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            mIsRunning = true;
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
            	if (mIsRunning) {
	            	cancel();
	                connectionFailed();
            	}
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothConnection.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public synchronized void cancel() {
            try {
                mIsRunning = false;
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        StringBuilder stringBuilder = new StringBuilder();

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    
                    /*
                    String receivedStirng = new String(buffer, 0, bytes);           // create string from bytes array
					stringBuilder.append(receivedStirng); 
					int endOfLineIndex = stringBuilder.indexOf("\r\n");             // determine the end-of-line
					if (endOfLineIndex > 0) {                                       // if end-of-line,
						String string = stringBuilder.substring(0, endOfLineIndex);// extract string
						stringBuilder.delete(0, stringBuilder.length());            // clear
						
						// Send data to Service
						Bundle bundle = new Bundle();
						bundle.putString("watchit_data", string);
						Message message = mHandler.obtainMessage(WATCHiTService.READ);
						message.setData(bundle);
						message.sendToTarget();
					}
					*/
                    
                    // skip first 3 bytes to remove prefix
                    String receivedStirng = new String(buffer, 3, bytes - 3);
                    Bundle bundle = new Bundle();
					bundle.putString("watchit_data", receivedStirng);
					Message message = mHandler.obtainMessage(WATCHiTService.READ);
					message.setData(bundle);
					message.sendToTarget();
					
                    // Send the obtained bytes to the UI Activity
                    // mHandler.obtainMessage(WATCHiTCommunicationService.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(WATCHiTService.WROTE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public synchronized void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
