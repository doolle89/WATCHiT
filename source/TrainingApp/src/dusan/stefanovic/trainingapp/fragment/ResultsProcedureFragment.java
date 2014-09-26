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
import dusan.stefanovic.trainingapp.ResultsProcedureActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;

public class ResultsProcedureFragment extends ListFragment {
	
	private List<Procedure> mProcedures;
	private ProcedureResultsListAdapter mProcedureListAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);	    
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AsyncTask<Void, Void, List<Procedure>> asyncTask = new AsyncTask<Void, Void, List<Procedure>>() {

			@Override
			protected List<Procedure> doInBackground(Void... args) {
				List<Procedure> procedures = null;
				if (getActivity() != null) {
					DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
					dbAdapter.open();
					procedures = dbAdapter.getAllProcedureTemplates();
					dbAdapter.close();
				}
				return procedures;
			}
			
			protected void onPostExecute(List<Procedure> result) {
				if (getActivity() != null) {
					mProcedures = result;
					mProcedureListAdapter = new ProcedureResultsListAdapter(getActivity(), 0, mProcedures);
			        setListAdapter(mProcedureListAdapter);
				}
			}
			
		};
		asyncTask.execute();
    }
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ResultsProcedureActivity.class);
        intent.putExtra("procedure", mProcedures.get(position));
        startActivity(intent);
    }
	
	public static class ProcedureResultsListAdapter extends ArrayAdapter<Procedure> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView description;
    	}
    	
    	public ProcedureResultsListAdapter(Context context, int resource, List<Procedure> objects) {
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
            holder.description.setText("Touch to see results");
            
            return row;
        }
    }
}
