package dusan.stefanovic.connectionapp.service;

public class WATCHiTServiceInterface {
	
	public static final String ACTION_START_WATCHiT_SERVICE = "dusan.stefanovic.connectionapp.service.WATCHiT_SERVICE";
	public static final String ACTION_START_WATCHiT_SETTINGS = "dusan.stefanovic.connectionapp.SETTINGS";
	
	// Call
    public static final int REGISTER_CLIENT = 1;
    public static final int UNREGISTER_CLIENT = 2;
    public static final int START_SERVICE = 3;
    public static final int STOP_SERVICE = 4;
    public static final int REQUEST_UPDATE = 5;
    
    // Callback
    public static final int CLIENT_REGISTERED = 31;
    public static final int CLIENT_UNREGISTERED = 32;
    public static final int SERVICE_STARTED = 33;
    public static final int SERVICE_STOPPED = 34;
    public static final int DEVICE_DISCONNECTED = 35;
    public static final int DEVICE_CONNECTING = 36;
    public static final int DEVICE_CONNECTED = 37;
    public static final int UPDATE = 38;
    public static final int TAG_READ = 39;
    
    // Data
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String DEVICE_NAME = "device_name";
    public static final String RECONNECTING_AFTER = "reconnecting_after";
    public static final String IS_DEVICE_AVAILABLE = "is_device_available";
    public static final String IS_DEVICE_ENABLED = "is_device_enabled";
    public static final String IS_CONNECTING_TO_DEVICE = "is_connecting_to_device";
    public static final String DEVICE_CONNECTION_STATUS = "device_connection_status";
    public static final String TAG_DATA_CONTENT = "tag_data_content";
    public static final String TAG_DATA_LENGTH = "tag_data_length";
}
