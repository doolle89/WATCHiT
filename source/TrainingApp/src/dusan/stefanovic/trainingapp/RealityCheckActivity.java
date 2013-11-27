package dusan.stefanovic.trainingapp;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.service.WATCHiTServiceInterface;
import dusan.stefanovic.treningapp.R;

public class RealityCheckActivity extends ActionBarActivity {
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private ActionBar mActionBar;
	private ListView mListView;
	
	private Procedure mProcedure;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reality_check);
        
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        
        final String[] dropdownValues = getResources().getStringArray(R.array.reality_check_items_array);;

        // Specify a SpinnerAdapter to populate the dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActionBar.getThemedContext(), android.R.layout.simple_spinner_item, android.R.id.text1, dropdownValues);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set up the dropdown list navigation in the action bar.
        mActionBar.setListNavigationCallbacks(adapter, new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				switch (position) {
					case 0:
						RealityCheckListAdapter stepListAdapter = new RealityCheckListAdapter(RealityCheckActivity.this, R.layout.list_item_step_reality_check, mProcedure.getSteps());
				        mListView.setAdapter(stepListAdapter);
						break;
					case 1:
						CompareListAdapter step1ListAdapter = new CompareListAdapter(RealityCheckActivity.this, R.layout.list_item_step_compare, mProcedure.getSteps());
				        mListView.setAdapter(step1ListAdapter);
						break;
				}
				return true;
			}
        	
        });
        
        mProcedure = getIntent().getParcelableExtra("procedure");
        
        mListView = (ListView) findViewById(R.id.listView);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			mActionBar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, mActionBar.getSelectedNavigationIndex());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
	        	finish();
	            return true;
	        case R.id.action_reality_check:
	        	Intent intent = new Intent(WATCHiTServiceInterface.ACTION_START_WATCHiT_SETTINGS);
	        	intent.putExtra("procedure", mProcedure);
	        	//startActivity(intent);
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public static class RealityCheckListAdapter extends ArrayAdapter<Step> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView status;
    		TextView startTime;
    		TextView endTime;
    		TextView duration;
    		RatingBar ratingBar;
    	}
    	
    	public RealityCheckListAdapter(Context context, int resource, List<Step> objects) {
    		super(context, resource, objects);
    	}

    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_step_reality_check, null);
                holder = new ViewHolder();
                holder.title = (TextView) row.findViewById(R.id.step_title);
                holder.status = (TextView) row.findViewById(R.id.step_status);
                holder.startTime = (TextView) row.findViewById(R.id.step_start_time);
                holder.endTime = (TextView) row.findViewById(R.id.step_end_time);
                holder.duration = (TextView) row.findViewById(R.id.step_duration);
                holder.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            
            final Step step = this.getItem(position);
            holder.title.setText(step.getTitle());
            switch (step.getStatus()) {
	        	case Step.STATUS_COMPLETED:
	        		holder.status.setText(getContext().getText(R.string.step_status_completed));
	        		break;
	        	case Step.STATUS_SKIPPED:
	        		holder.status.setText(getContext().getText(R.string.step_status_skipped));
	        		break;
        	}
            holder.startTime.setText(formatTime(step.getStartTime()));
            holder.endTime.setText(formatTime(step.getEndTime()));
            holder.duration.setText(formatTime(step.getDuration()));
            holder.ratingBar.setRating(step.getSelfAssessment());
            return row;
        }
    	
    	private String formatTime(long milliseconds) {
    		long seconds = (milliseconds / 1000) % 60;
        	long minutes = (milliseconds / 60000) % 60;
        	long hours = (milliseconds / 3600000) % 24;
        	return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    	}
    }
	
	public static class CompareListAdapter extends ArrayAdapter<Step> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView status;
    		TextView startTime;
    		TextView endTime;
    		TextView duration;
    		TextView durationCompare;
    	}
    	
    	public CompareListAdapter(Context context, int resource, List<Step> objects) {
    		super(context, resource, objects);
    	}

    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_step_compare, null);
                holder = new ViewHolder();
                holder.title = (TextView) row.findViewById(R.id.step_title);
                holder.status = (TextView) row.findViewById(R.id.step_status);
                holder.startTime = (TextView) row.findViewById(R.id.step_start_time);
                holder.endTime = (TextView) row.findViewById(R.id.step_end_time);
                holder.duration = (TextView) row.findViewById(R.id.step_duration);
                holder.durationCompare = (TextView) row.findViewById(R.id.step_duration_compare);
                row.setTag(holder);
            } else {
                holder= (ViewHolder) row.getTag();
            }
            
            final Step step = this.getItem(position);
            holder.title.setText(step.getTitle());
            switch (step.getStatus()) {
	        	case Step.STATUS_COMPLETED:
	        		holder.status.setText(getContext().getText(R.string.step_status_completed));
	        		break;
	        	case Step.STATUS_SKIPPED:
	        		holder.status.setText(getContext().getText(R.string.step_status_skipped));
	        		break;
        	}
            holder.startTime.setText(formatTime(step.getStartTime()));
            holder.endTime.setText(formatTime(step.getEndTime()));
            holder.duration.setText(formatTime(step.getDuration()));
            holder.durationCompare.setText(formatTime(step.getOptimalTime()));
            return row;
        }
    	
    	private String formatTime(long milliseconds) {
    		long seconds = (milliseconds / 1000) % 60;
        	long minutes = (milliseconds / 60000) % 60;
        	long hours = (milliseconds / 3600000) % 24;
        	return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    	}
    }
}
