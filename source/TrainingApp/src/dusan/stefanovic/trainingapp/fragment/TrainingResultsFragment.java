package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.ProcedureResultPreviewActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

public class TrainingResultsFragment extends ListFragment {
	
	Procedure mProcedure;
	List<Procedure> mProcedures;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			ProcedureListener trainingProcedureListener = (ProcedureListener) getActivity();
			mProcedure = trainingProcedureListener.onProcedureRequested();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
		AsyncTask<Long, Void, List<Procedure>> asyncTask = new AsyncTask<Long, Void, List<Procedure>>() {

			@Override
			protected List<Procedure> doInBackground(Long... args) {
				List<Procedure> procedures = null;
				if (getActivity() != null) {
					DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
					if (dbAdapter.open()) {
						procedures = dbAdapter.getAllProcedureResults(mProcedure.getTemplateId());
						dbAdapter.close();
					}
				}
				return procedures;
			}
			
			protected void onPostExecute(List<Procedure> result) {
				mProcedures = result;
				if (getActivity() != null && mProcedures != null && mProcedures.size() > 0) {
			        ProcedureListAdapter stepListAdapter = new ProcedureListAdapter(getActivity(), R.layout.list_item_step_training, mProcedures);
			        setListAdapter(stepListAdapter);
				}
			}
			
		};
		asyncTask.execute();
    }
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ProcedureResultPreviewActivity.class);
        intent.putExtra("procedure", mProcedures.get(position));
        startActivity(intent);
    }
	
	public static class ProcedureListAdapter extends ArrayAdapter<Procedure> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView time;
    	}
    	
    	public ProcedureListAdapter(Context context, int resource, List<Procedure> objects) {
    		super(context, resource, objects);
    	}

    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_step_training, null);
                holder = new ViewHolder();
                holder.title = (TextView) row.findViewById(R.id.step_title);
                holder.time = (TextView) row.findViewById(R.id.step_status);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            
            final Procedure procedure = this.getItem(position);
            holder.title.setText(procedure.getTitle());
            holder.time.setText(String.valueOf(procedure.getDuraton()));
            return row;
        }
    }
}
