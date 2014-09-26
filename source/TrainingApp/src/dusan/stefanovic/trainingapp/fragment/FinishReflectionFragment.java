package dusan.stefanovic.trainingapp.fragment;

import java.util.List;
import java.util.UUID;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.imc.mirror.sdk.ConnectionConfiguration;
import de.imc.mirror.sdk.DataObject;
import de.imc.mirror.sdk.Space;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.android.PrivateSpace;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.InvalidDataException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.trainingapp.util.MSFHelper;
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
}
