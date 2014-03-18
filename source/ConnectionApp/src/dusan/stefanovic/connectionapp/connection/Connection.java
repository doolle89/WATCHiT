package dusan.stefanovic.connectionapp.connection;

import dusan.stefanovic.connectionapp.BluetoothDeviceListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public interface Connection {

	public boolean isAvailable();
    public boolean isEnabled();
    public Intent registerStateChangeBroadcastReceiver(Context context);
    public void unregisterStateChangeBroadcastReceiver(Context context);
    public int getState();
    public void connect(String deviceAddress);
    public void stop();
    
    public static class Factory {
    	
    	static Intent mEnableDeviceIntent;
    	static Intent mChooseDeviceIntent;
    	
    	public static Connection createBluetoothConnection(Handler handler) {
    		mEnableDeviceIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    		mChooseDeviceIntent = new Intent(BluetoothDeviceListActivity.ACTION_START);
    		return new BluetoothConnection(handler);
    	}
    	
    	public static Intent getEnableDeviceIntent() {
    		return new Intent(mEnableDeviceIntent);
    	}
    	
    	public static Intent getChooseDeviceIntent() {
    		return new Intent(mChooseDeviceIntent);
    	}
    }
    
}
