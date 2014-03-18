package dusan.stefanovic.trainingapp.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

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
				mProcedureIdTextView.setText(mProcedureIdTextView.getText() + " " + mProcedure.getId());
			}
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
	public void save() {
		AsyncTask<Procedure, Void, String> asyncTask = new AsyncTask<Procedure, Void, String>() {

			@Override
			protected String doInBackground(Procedure... args) {
				String result = null;
				if (getActivity() != null) {
					DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
					dbAdapter.open();
					result = dbAdapter.createProcedureResult(args[0]);
					dbAdapter.close();
				}
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				
			}
			
		};
		asyncTask.execute(mProcedure);
	}

}
