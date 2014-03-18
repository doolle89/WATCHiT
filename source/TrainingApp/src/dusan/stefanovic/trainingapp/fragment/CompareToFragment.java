package dusan.stefanovic.trainingapp.fragment;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class CompareToFragment extends ListFragment {
	
	private Procedure mProcedure;
	private View mHeaderView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mHeaderView = inflater.inflate(R.layout.list_header_step_compare, null, false);
        return rootView;
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			ProcedureListener procedureListener = (ProcedureListener) getActivity();
			mProcedure = procedureListener.onProcedureRequested();
			
			getListView().addHeaderView(mHeaderView);
			if (mProcedure != null) {
				CompareListAdapter compareToListAdapterListAdapter = new CompareListAdapter(getActivity(), R.layout.list_item_step_compare, mProcedure.getSteps());
		        setListAdapter(compareToListAdapterListAdapter);
			}
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
public static class CompareListAdapter extends ArrayAdapter<Step> {
    	
    	static class ViewHolder {
    		TextView title;
    		TextView status;
    		TextView errors;
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
                holder.errors = (TextView) row.findViewById(R.id.step_errors);
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
            holder.errors.setText(step.getErrors() + " " + getContext().getResources().getQuantityString(R.plurals.training_steps_fragment_errors, step.getErrors()));
            holder.startTime.setText(formatTime(step.getStartTime()));
            holder.endTime.setText(formatTime(step.getEndTime()));
            holder.duration.setText(formatTime(step.getDuration()));
            holder.durationCompare.setText(formatTime(step.getOptimalTime()));
            return row;
        }
    	
    	private String formatTime(long milliseconds) {
    		int seconds = (int) (milliseconds / 1000) % 60;
        	int minutes = (int) (milliseconds / 60000) % 60;
        	int hours = (int) (milliseconds / 3600000) % 24;
        	return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    	}
    }

}
