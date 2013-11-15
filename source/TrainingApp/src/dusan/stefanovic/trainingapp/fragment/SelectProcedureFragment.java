package dusan.stefanovic.trainingapp.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.ProcedurePreviewActivity;
import dusan.stefanovic.trainingapp.TrainingActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class SelectProcedureFragment extends ListFragment {
	
	List<Procedure> mProcedures;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mProcedures = new ArrayList<Procedure>();
	    
	    Procedure procedure = new Procedure("Procedure 1", "Procedure description description description " +
				"description description description description description " +
				"description description description description description");
		Step step = new Step("Step 1", "description 1");
		step.setOptimalTime(10000);
		procedure.addStep(step);
		step = new Step("Step 2", "description 2");
		step.setOptimalTime(5000);
		procedure.addStep(step);
		step = new Step("Step 3", "description 3");
		step.setOptimalTime(15000);
		procedure.addStep(step);
		step = new Step("Step 4", "description 4");
		step.setOptimalTime(30000);
		procedure.addStep(step);
		mProcedures.add(procedure);
		
		procedure = new Procedure("Procedure 2", "Procedure description description description " +
				"description description description description description " +
				"description description description description description");
		step = new Step("Step 1", "description 1");
		step.setOptimalTime(10000);
		procedure.addStep(step);
		step = new Step("Step 2", "description 2");
		step.setOptimalTime(5000);
		procedure.addStep(step);
		step = new Step("Step 3", "description 3");
		step.setOptimalTime(15000);
		procedure.addStep(step);
		mProcedures.add(procedure);
		
		procedure = new Procedure("Procedure 3", "Procedure description description description " +
				"description description description description description " +
				"description description description description description");
		step = new Step("Step 1", "description 1");
		step.setOptimalTime(10000);
		procedure.addStep(step);
		step = new Step("Step 2", "description 2");
		step.setOptimalTime(5000);
		procedure.addStep(step);
		step = new Step("Step 3", "description 3");
		step.setOptimalTime(15000);
		procedure.addStep(step);
		step = new Step("Step 4", "description 4");
		step.setOptimalTime(30000);
		procedure.addStep(step);
		step = new Step("Step 5", "description 5");
		step.setOptimalTime(20000);
		procedure.addStep(step);
		mProcedures.add(procedure);
		
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ProcedureListAdapter stepListAdapter = new ProcedureListAdapter(getActivity(), R.layout.list_item_step_training, mProcedures);
        setListAdapter(stepListAdapter);
        return rootView;
    }
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ProcedurePreviewActivity.class);
        intent.putExtra("procedure", mProcedures.get(position));
        startActivity(intent);
    }
	
	public void setProceduresList(List<Procedure> procedures) {
		mProcedures = procedures;
		ProcedureListAdapter stepListAdapter = new ProcedureListAdapter(getActivity(), R.layout.list_item_step_training, mProcedures);
        setListAdapter(stepListAdapter);
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
