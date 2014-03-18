package dusan.stefanovic.trainingapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.treningapp.R;

public class TrainingCurrentStepFragment extends Fragment {
	
	private TextView mTitleTextView;
	private TextView mDescriptionTextView;
	//private ImageView mImageView;
	
	private Procedure mProcedure;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_step, container, false);
        //Bundle args = getArguments();
        
        mTitleTextView = (TextView) rootView.findViewById(R.id.step_title);
        mDescriptionTextView = (TextView) rootView.findViewById(R.id.step_description);
        //mImageView = (ImageView) rootView.findViewById(R.id.step_image);
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			ProcedureListener procedureListener = (ProcedureListener) getActivity();
			mProcedure = procedureListener.onProcedureRequested();
			update();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
	public void update() {
		if (getView() != null && mProcedure != null) {
			if (mProcedure.getCurrentStep() != null) {
				mTitleTextView.setText(mProcedure.getCurrentStep().getTitle());
		        mDescriptionTextView.setText(mProcedure.getCurrentStep().getDescription());
		        // mImageView.setImageResource(R.drawable.ic_launcher);
			} else {	 
				mTitleTextView.setText(mProcedure.getTitle());
		        mDescriptionTextView.setText(mProcedure.getDescription());
		        // mImageView.setImageResource(R.drawable.ic_launcher);
			}
		}
	}
}
