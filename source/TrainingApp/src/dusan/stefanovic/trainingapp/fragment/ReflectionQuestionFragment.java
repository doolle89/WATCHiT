package dusan.stefanovic.trainingapp.fragment;

import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.fragment.RealityCheckFragment.RealityCheckListAdapter;
import dusan.stefanovic.treningapp.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ReflectionQuestionFragment extends Fragment {
	
	Procedure mProcedure;
	EditText mEditText;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reflection_question, container, false);
        mEditText = (EditText) rootView.findViewById(R.id.editText);
        return rootView;
	}

	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			ProcedureListener procedureListener = (ProcedureListener) getActivity();
			mProcedure = procedureListener.onProcedureRequested();
			mEditText.addTextChangedListener(new TextWatcher() {

				@Override
				public void afterTextChanged(Editable editable) {
					mProcedure.note = editable.toString();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					
				}
	        	
	        });
			
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
}
