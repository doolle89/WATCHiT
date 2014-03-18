package dusan.stefanovic.trainingapp.fragment;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import dusan.stefanovic.treningapp.R;

public class TrainingOverviewFragment extends Fragment {

	EditText mTimerEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_training_overview, container, false);
        //Bundle args = getArguments();
        mTimerEditText = (EditText) rootView.findViewById(R.id.timerEditText);
        return rootView;
    }
    
    public void updateTimer(long milliseconds) {
    	long seconds = (milliseconds / 1000) % 60;
    	long minutes = (milliseconds / 60000) % 60;
    	long hours = (milliseconds / 3600000) % 24;
    	
    	String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    	mTimerEditText.setText(time);
    }
}
