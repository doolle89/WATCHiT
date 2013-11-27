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
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class SelfAssessmentFragment extends ListFragment {

	private Procedure mProcedure;private View mHeaderView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mHeaderView = inflater.inflate(R.layout.list_header_step_self_assessment, null, false);
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
				SelfAssessmentListAdapter selfAssessmentListAdapterListAdapter = new SelfAssessmentListAdapter(getActivity(), R.layout.list_item_step_self_assessment, mProcedure.getSteps());
		        setListAdapter(selfAssessmentListAdapterListAdapter);
			}
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TrainingProcedureListener");
        }
	}
	
	public void update() {
		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
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
