package dusan.stefanovic.trainingapp.fragment;

import dusan.stefanovic.treningapp.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrainingCurrentStepFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_training_progress, container, false);
        //Bundle args = getArguments();
        return rootView;
    }
}
