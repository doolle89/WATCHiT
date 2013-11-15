package dusan.stefanovic.trainingapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.treningapp.R;

public class TrainingProgressFragment extends Fragment {

	private EditText mTimerEditText;
	private ProgressBar mProgressBar;
	
	private Procedure mProcedure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_training_progress, container, false);
        //Bundle args = getArguments();
        mTimerEditText = (EditText) rootView.findViewById(R.id.timerEditText);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        return rootView;
    }
    
    @Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			TrainingProcedureListener trainingProcedureListener = (TrainingProcedureListener) getActivity();
			mProcedure = trainingProcedureListener.onProcedureRequested();
			update();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
	public void update() {
		if (getView() != null && mProcedure != null) {
			mProgressBar.setProgress(mProcedure.getProgress());
		}
	}
    
    public void updateTimer(long milliseconds) {
    	if (getView() != null) {
	    	long seconds = (milliseconds / 1000) % 60;
	    	long minutes = (milliseconds / 60000) % 60;
	    	long hours = (milliseconds / 3600000) % 24;
	    	
	    	String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    	mTimerEditText.setText(time);
    	}
    }
}
