package dusan.stefanovic.trainingapp;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.dialog.StepDialogFragment;
import dusan.stefanovic.treningapp.R;

public class ProcedureResultPreviewActivity extends ActionBarActivity {
	
	ActionBar mActionBar;
    TextView mIdTextView;
    TextView mUserIdTextView;
    TextView mNotesTextView;
    ListView mListView;
    
    Procedure mProcedure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_procedure_result_preview);
		
		mProcedure = getIntent().getParcelableExtra("procedure");
		
		mActionBar = getSupportActionBar(); 
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(mProcedure.getTitle());
		
		mIdTextView = (TextView) findViewById(R.id.textView_id);
		mIdTextView.setText(mProcedure.getId());
		mUserIdTextView = (TextView) findViewById(R.id.textView_user_id);
		mUserIdTextView.setText(mProcedure.getUserId());
		mNotesTextView = (TextView) findViewById(R.id.textView_notes);
		mNotesTextView.setText(mProcedure.getNotes());
		
		mListView = (ListView) findViewById(R.id.listView);
		StepListAdapter stepListAdapter = new StepListAdapter(this, android.R.layout.simple_list_item_1, mProcedure.getSteps());
        mListView.setAdapter(stepListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				StepDialogFragment dialogFragment = StepDialogFragment.getInstance(mProcedure.getStep(position));
				dialogFragment.show(getSupportFragmentManager(), "step_dialog_fragment");
			}
        	
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case android.R.id.home:
	        	finish();
	            return true;
	        case R.id.action_settings:
	            // openSettings();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public static class StepListAdapter extends ArrayAdapter<Step> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView description;
    	}
    	
    	public StepListAdapter(Context context, int resource, List<Step> objects) {
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
            
            final Step step = this.getItem(position);
            holder.title.setText(step.getTitle());
            // holder.description.setText(step.getDescription());
            holder.description.setText(getContext().getText(R.string.procedure_preview_activity_procedure_description));
            return row;
        }
    }
}
