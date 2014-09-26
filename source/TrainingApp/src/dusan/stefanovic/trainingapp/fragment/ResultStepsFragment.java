package dusan.stefanovic.trainingapp.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.dialog.StepDialogFragment;
import dusan.stefanovic.treningapp.R;

public class ResultStepsFragment extends ListFragment {
	
	private Procedure mProcedure;
	
	private View mFooterView;
	private TextView mScoreTextView;
    private TextView mTimeTextView;
    private TextView mErrorsTextView;
    private TextView mDateTextView;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result_steps, container, false);
        mFooterView = inflater.inflate(R.layout.list_footer_step_result, null);
        mScoreTextView = (TextView) rootView.findViewById(R.id.textView_score);
		mTimeTextView = (TextView) rootView.findViewById(R.id.textView_time);
		mErrorsTextView = (TextView) rootView.findViewById(R.id.textView_errors);
		mDateTextView = (TextView) rootView.findViewById(R.id.textView_date);
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			ProcedureListener trainingProcedureListener = (ProcedureListener) getActivity();
			mProcedure = trainingProcedureListener.onProcedureRequested();
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					if (position < mProcedure.getStepsNumber()) {
						StepDialogFragment dialogFragment = StepDialogFragment.getInstance(mProcedure.getStep(position));
						dialogFragment.show(getChildFragmentManager(), "step_dialog_fragment");
					}
					return false;
				}
		    });
			addFooter();
			update();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
	public void update() {
		if (getView() != null && mProcedure != null) {
			float score = mProcedure.getScore();
			mScoreTextView.setText(String.format("%.2f", score < 0 ? 0 : score));
			mTimeTextView.setText(formatTime(mProcedure.getDuration()));
			mErrorsTextView.setText(String.valueOf(mProcedure.getErrors()));
			
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy\nHH:mm:ss", Locale.getDefault());		    
		    mDateTextView.setText(dateFormat.format(new Date(mProcedure.getStartTime())));
			
			StepListAdapter stepListAdapter = new StepListAdapter(getActivity(), 0, mProcedure.getSteps());
	        setListAdapter(stepListAdapter);
		}
	}
	
	private void addFooter() {
		mFooterView.setBackgroundColor(getResources().getColor(R.color.step_in_progress));
		TextView textView = (TextView) mFooterView.findViewById(R.id.step_title);
		textView.setText("Total");
		textView = (TextView) mFooterView.findViewById(R.id.step_status);
		textView.setVisibility(View.GONE);
		textView = (TextView) mFooterView.findViewById(R.id.step_errors);
		textView.setText(mProcedure.getErrors() + " " + getResources().getQuantityString(R.plurals.training_steps_fragment_errors, mProcedure.getErrors()));;
		textView = (TextView) mFooterView.findViewById(R.id.step_score);
		textView.setText(String.format("%.2f", mProcedure.getScore()));
		textView = (TextView) mFooterView.findViewById(R.id.step_duration);
		textView.setText(formatTime(mProcedure.getDuration()));
		textView = (TextView) mFooterView.findViewById(R.id.step_score_compare);
		textView.setText(String.format("%.2f", mProcedure.getMaxScore()));
		textView = (TextView) mFooterView.findViewById(R.id.step_duration_compare);
		long optimalTime = mProcedure.getOptimalTime();
		textView.setText(optimalTime == 0 ? "-" : formatTime(optimalTime));		
		
		getListView().addFooterView(mFooterView);
	}
	
	public static class StepListAdapter extends ArrayAdapter<Step> {
		
    	static class ViewHolder {
    		View container;
    		TextView title;
    		TextView status;
    		TextView errors;
    		TextView score;
    		TextView duration;
    		TextView scoreCompare;
    		TextView durationCompare;
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
                row = inflater.inflate(R.layout.list_item_step_result, null);
                holder = new ViewHolder();
                holder.container = row.findViewById(R.id.step_layout);
                holder.title = (TextView) row.findViewById(R.id.step_title);
                holder.status = (TextView) row.findViewById(R.id.step_status);
                holder.errors = (TextView) row.findViewById(R.id.step_errors);
                holder.score = (TextView) row.findViewById(R.id.step_score);
                holder.duration = (TextView) row.findViewById(R.id.step_duration);
                holder.scoreCompare = (TextView) row.findViewById(R.id.step_score_compare);
                holder.durationCompare = (TextView) row.findViewById(R.id.step_duration_compare);
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
            holder.errors.setText(step.getErrors() + " " + getContext().getResources().getQuantityString(R.plurals.training_steps_fragment_errors, step.getErrors()));
            holder.score.setText(String.format("%.2f", step.getScore()));
            holder.duration.setText(formatTime(step.getDuration()));
            holder.scoreCompare.setText(String.format("%.2f", step.getMaxScore()));
            holder.durationCompare.setText(step.getOptimalTime() == 0 ? "-" : formatTime(step.getOptimalTime()));
            switch (step.getStatus()) {
            	case Step.STATUS_COMPLETED:
            		if (step.getErrors() == 0) {
            			holder.container.setBackgroundColor(getContext().getResources().getColor(R.color.step_completed));
            		} else {
            			holder.container.setBackgroundColor(getContext().getResources().getColor(R.color.step_completed_with_errors));
            		}
            		holder.status.setText(getContext().getText(R.string.step_status_completed));
            		break;
            	case Step.STATUS_SKIPPED:
            		holder.container.setBackgroundColor(getContext().getResources().getColor(R.color.step_skipped));
            		holder.status.setText(getContext().getText(R.string.step_status_skipped));
            		break;
            }
            return row;
        }
    	
    	
    }
	
	private static String formatTime(long milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60;
    	int minutes = (int) (milliseconds / 60000) % 60;
    	int hours = (int) (milliseconds / 3600000) % 24;
    	return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
	}
}
