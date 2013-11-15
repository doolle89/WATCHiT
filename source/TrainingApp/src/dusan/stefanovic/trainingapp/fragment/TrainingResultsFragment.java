package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class TrainingResultsFragment extends ListFragment {
	
	List<Step> mSteps;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
	
	public void setStepsList(List<Step> steps) {
		mSteps = steps;
		StepListAdapter stepListAdapter = new StepListAdapter(getActivity(), R.layout.list_item_step_training, mSteps);
        setListAdapter(stepListAdapter);
	}
	
	public static class StepListAdapter extends ArrayAdapter<Step> {
    	
    	static class ViewHolder {
    		View container;
    		TextView title;
    		TextView status;
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
                row = inflater.inflate(R.layout.list_item_step_training, null);
                holder = new ViewHolder();
                holder.container = row.findViewById(R.id.step_layout);
                holder.title = (TextView) row.findViewById(R.id.step_title);
                holder.status = (TextView) row.findViewById(R.id.step_status);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            
            final Step step = this.getItem(position);
            holder.title.setText(step.getTitle());
            switch (step.getStatus()) {
            	case Step.STATUS_COMPLETED:
            		holder.container.setBackgroundColor(Color.GREEN);
            		holder.status.setText(getContext().getText(R.string.step_status_completed));
            		break;
            	case Step.STATUS_SKIPPED:
            		holder.container.setBackgroundColor(Color.RED);
            		holder.status.setText(getContext().getText(R.string.step_status_skipped));
            		break;
            	case Step.STATUS_IN_PROGRESS:
            		holder.container.setBackgroundColor(Color.CYAN);
            		holder.status.setText(getContext().getText(R.string.step_status_in_progress));
            		break;
            	case Step.STATUS_PENDING:
            		holder.container.setBackgroundColor(Color.YELLOW);
            		holder.status.setText(getContext().getText(R.string.step_status_pending));
            		break;
            	case Step.STATUS_PAUSED:
            		holder.container.setBackgroundColor(Color.LTGRAY);
            		holder.status.setText(getContext().getText(R.string.step_status_paused));
            		break;
            }
            return row;
        }
    }
}
