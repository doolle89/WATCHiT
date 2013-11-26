package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class RealityCheckFragment extends ListFragment {
	
	private Procedure mProcedure;
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			ProcedureListener procedureListener = (ProcedureListener) getActivity();
			mProcedure = procedureListener.onProcedureRequested();
			
			TextView textView = new TextView(getActivity());
			textView.setText("test");
			getListView().addHeaderView(textView);
			
			if (mProcedure != null) {
				RealityCheckListAdapter realityCheckListAdapterListAdapter = new RealityCheckListAdapter(getActivity(), R.layout.list_item_step_reality_check, mProcedure.getSteps());
		        setListAdapter(realityCheckListAdapterListAdapter);
			}
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
	public void refresh() {
		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
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
        	return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    	}
    }

}
