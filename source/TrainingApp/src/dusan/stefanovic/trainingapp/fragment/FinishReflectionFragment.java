package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.trainingapp.fragment.RealityCheckFragment.RealityCheckListAdapter;
import dusan.stefanovic.trainingapp.fragment.SelectProcedureFragment.ProcedureListAdapter;
import dusan.stefanovic.treningapp.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FinishReflectionFragment extends Fragment {
	
	private Procedure mProcedure;
	private TextView mProcedureIdTextView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish_reflection, container, false);
        mProcedureIdTextView = (TextView) rootView.findViewById(R.id.procedure_id);
        return rootView;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		try {
			ProcedureListener procedureListener = (ProcedureListener) getActivity();
			mProcedure = procedureListener.onProcedureRequested();			
			if (mProcedure != null) {
				AsyncTask<Procedure, Void, Long> asyncTask = new AsyncTask<Procedure, Void, Long>() {

					@Override
					protected Long doInBackground(Procedure... args) {
						DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
						dbAdapter.open();
						long result = dbAdapter.createProcedureResult(args[0]);
						dbAdapter.close();
						return result;
					}
					
					protected void onPostExecute(Long result) {
						mProcedureIdTextView.setText(mProcedureIdTextView.getText() + " " + result);
					}
					
				};
				asyncTask.execute(mProcedure);
			}
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}

}
