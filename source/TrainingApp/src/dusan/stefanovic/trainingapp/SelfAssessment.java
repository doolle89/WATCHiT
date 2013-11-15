package dusan.stefanovic.trainingapp;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class SelfAssessment extends ActionBarActivity {
	
	private ListView mListView;
	
	private Procedure mProcedure;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assessment);
        
        mProcedure = getIntent().getParcelableExtra("procedure");
        
        mListView = (ListView) findViewById(R.id.listView);
        SelfAssessmentListAdapter stepListAdapter = new SelfAssessmentListAdapter(this, R.layout.list_item_step_self_assessment, mProcedure.getSteps());
        mListView.setAdapter(stepListAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.self_assessment, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	
	            return true;
	        case R.id.action_reality_check:
	        	Intent intent = new Intent(this, RealityCheckActivity.class);
	        	intent.putExtra("procedure", mProcedure);
	        	startActivity(intent);
	        	finish();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public static class SelfAssessmentListAdapter extends ArrayAdapter<Step> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView status;
    		RatingBar ratingBar;
    	}
    	
    	public SelfAssessmentListAdapter(Context context, int resource, List<Step> objects) {
    		super(context, resource, objects);
    	}

    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_step_self_assessment, null);
                holder = new ViewHolder();
                holder.title = (TextView) row.findViewById(R.id.step_title);
                holder.status = (TextView) row.findViewById(R.id.step_status);
                holder.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
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
            holder.ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

				@Override
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					step.setSelfAssessment(rating);
				}
            	
            });
            return row;
        }
    }
}
