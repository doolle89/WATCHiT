package dusan.stefanovic.trainingapp.fragment;

import java.util.ArrayList;
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
import dusan.stefanovic.trainingapp.MainActivity;
import dusan.stefanovic.trainingapp.ProcedurePreviewActivity;
import dusan.stefanovic.trainingapp.TrainingActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

public class SelectProcedureFragment extends ListFragment {
	
	List<Procedure> mProcedures;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AsyncTask<Void, Void, List<Procedure>> asyncTask = new AsyncTask<Void, Void, List<Procedure>>() {

			@Override
			protected List<Procedure> doInBackground(Void... args) {
				DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
				dbAdapter.open();
				List<Procedure> procedures = dbAdapter.getAllProcedureTemplates();
				dbAdapter.close();
				return procedures;
			}
			
			protected void onPostExecute(List<Procedure> result) {
				mProcedures = result;
		        ProcedureListAdapter stepListAdapter = new ProcedureListAdapter(getActivity(), R.layout.list_item_step_training, mProcedures);
		        setListAdapter(stepListAdapter);
			}
			
		};
		asyncTask.execute();
    }
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ProcedurePreviewActivity.class);
        intent.putExtra("procedure", mProcedures.get(position));
        startActivity(intent);
    }
	
	public void setProceduresList(List<Procedure> procedures) {
		mProcedures = procedures;
		ProcedureListAdapter stepListAdapter = new ProcedureListAdapter(getActivity(), R.layout.list_item_step_training, mProcedures);
        setListAdapter(stepListAdapter);
	}
	
	public static class ProcedureListAdapter extends ArrayAdapter<Procedure> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView description;
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
                row = inflater.inflate(android.R.layout.simple_list_item_2, null);
                holder = new ViewHolder();
                holder.title = (TextView) row.findViewById(android.R.id.text1);
                holder.description = (TextView) row.findViewById(android.R.id.text2);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            
            final Procedure procedure = this.getItem(position);
            holder.title.setText(procedure.getTitle());
            holder.description.setText(procedure.getDescription());
            
            return row;
        }
    }
}
