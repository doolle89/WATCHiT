package dusan.stefanovic.trainingapp.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dusan.stefanovic.trainingapp.fragment.StepLibraryFragment;
import dusan.stefanovic.trainingapp.fragment.StepLibraryFragment.StepLibraryFragmentListener;
import dusan.stefanovic.treningapp.R;

public class StepLibraryDialogFragment extends DialogFragment {
	
	private StepLibraryFragmentListener mStepLibraryFragmentListener;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_template_step_library, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getDialog().setTitle("Step library");
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		StepLibraryFragment newFragment = new StepLibraryFragment();
		newFragment.setStepLibraryFragmentListener(mStepLibraryFragmentListener);
		fragmentTransaction.add(R.id.fragment_container, newFragment);
		fragmentTransaction.commit();
	}

	public void setStepLibraryFragmentListener(StepLibraryFragmentListener stepLibraryFragmentListener) {
		mStepLibraryFragmentListener = stepLibraryFragmentListener;
	}
}
