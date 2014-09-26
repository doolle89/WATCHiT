package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.CreateProcedureActivity;
import dusan.stefanovic.trainingapp.PreviewProcedureActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

public class SelectProcedureFragment extends ListFragment {
	
	public static final int ACTION_GET_PROCEDURE = 537;
	
	private List<Procedure> mProcedures;
	private ProcedureListAdapter mProcedureListAdapter;
	
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
				DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
				dbAdapter.open();
				List<Procedure> procedures = dbAdapter.getAllProcedureTemplates();
				dbAdapter.close();
				return procedures;
			}
			
			protected void onPostExecute(List<Procedure> result) {
				mProcedures = result;
				mProcedureListAdapter = new ProcedureListAdapter(getActivity(), R.layout.list_item_step_training, mProcedures);
		        setListAdapter(mProcedureListAdapter);
			}
			
		};
		asyncTask.execute();
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.select_procedure, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_create_procedure:
	            startActivityForResult(new Intent(getActivity(), CreateProcedureActivity.class), ACTION_GET_PROCEDURE);
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), PreviewProcedureActivity.class);
        intent.putExtra("procedure", mProcedures.get(position));
        startActivity(intent);
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_GET_PROCEDURE) {
			if (resultCode == Activity.RESULT_OK) {
				Procedure procedure = data.getParcelableExtra(CreateProcedureActivity.EXTRA_PROCEDURE_DATA);
				mProcedureListAdapter.add(procedure);
			}
		}
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
