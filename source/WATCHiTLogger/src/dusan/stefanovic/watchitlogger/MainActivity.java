package dusan.stefanovic.watchitlogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	
	private boolean mIsLoggingStarted = false;
	private StringBuilder mStringBuilder = new StringBuilder();
	
	private Button mStartButton;
	private Button mStopButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mStartButton = (Button) findViewById(R.id.button_start);
		mStartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mStartButton.setEnabled(false);
				mStopButton.setEnabled(true);
				startLogging();
			}
		});
		
		mStopButton = (Button) findViewById(R.id.button_stop);
		mStopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mStartButton.setEnabled(true);
				mStopButton.setEnabled(false);
				stopLogging();
			}
		});
	}
	
	@Override
	public void onDataReceived(byte[] data, int length) {
    	int offset = 0;
    	String tagValue = "";
    	try {
    		tagValue = new String(data, offset, length - offset);
    		if (mIsLoggingStarted) {
	    		mStringBuilder.append(getTimeStamp() + ":   " + tagValue + "\n");
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	Toast.makeText(this, tagValue, Toast.LENGTH_SHORT).show();
	}
	
	private void startLogging() {
		mIsLoggingStarted = true;
		mStringBuilder.setLength(0);
		mStringBuilder.append(getTimeStamp() + ":   START\n");
	}
	
	private void stopLogging() {
		mIsLoggingStarted = false;
		mStringBuilder.append(getTimeStamp() + ":   STOP\n");
		String message = "Error occurred during saving";
		if (TextFileWriter.writeToFile(mStringBuilder.toString())) {
			message = "Saved successfully";
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	private String getTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
	}
}
