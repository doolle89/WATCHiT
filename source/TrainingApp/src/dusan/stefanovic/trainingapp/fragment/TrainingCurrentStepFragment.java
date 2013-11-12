package dusan.stefanovic.trainingapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class TrainingCurrentStepFragment extends Fragment {
	
	private TextView mTitleTextView;
	private TextView mDescriptionTextView;
	private ImageView mImageView;
	
	private Procedure mProcedure;
	private Step mStep;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_step, container, false);
        //Bundle args = getArguments();
        
        mTitleTextView = (TextView) rootView.findViewById(R.id.step_title);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.step_description);
        mImageView = (ImageView) rootView.findViewById(R.id.step_image);
        updateView();
        
        return rootView;
    }
	
	public void setProcedure(Procedure procedure) {
		mProcedure = procedure;
		setStep(mProcedure.getCurrentStep());
	}
	
	public void setStep(Step step) {
		mStep = step;
		updateView();
	}
	
	private void updateView() {
		if (mStep != null) {
			mTitleTextView.setText(mStep.getTitle());
	        mDescriptionTextView.setText(mStep.getDescription());
	        mImageView.setImageResource(R.drawable.ic_launcher);
		} else if (mProcedure != null) {
			mTitleTextView.setText(mProcedure.getTitle());
	        mDescriptionTextView.setText(mProcedure.getDescription());
	        mImageView.setImageResource(R.drawable.ic_launcher);
		}
	}
}
