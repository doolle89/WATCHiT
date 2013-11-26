package dusan.stefanovic.trainingapp;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.dialog.StepDialogFragment;
import dusan.stefanovic.treningapp.R;

public class ProcedurePreviewActivity extends ActionBarActivity {
	
	ActionBar mActionBar;
    ImageView mImageView;
    ListView mListView;
    Button mStartButton;
    
    Procedure mProcedure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_procedure_preview);
		
		mProcedure = getIntent().getParcelableExtra("procedure");
		
		mActionBar = getSupportActionBar(); 
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(mProcedure.getTitle());
		
		mImageView = (ImageView) findViewById(R.id.imageView);
		
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
		
		mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ProcedurePreviewActivity.this, TrainingActivity.class);
		        intent.putExtra("procedure", mProcedure);
		        startActivity(intent);
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
            holder.description.setText(step.getDescription());
            
            return row;
        }
    }
}
