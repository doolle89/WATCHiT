package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

public class StepLibraryFragment extends ListFragment {
	
	public interface StepLibraryFragmentListener {
		public void onStepSelected(Step step);
	}
	
	private StepLibraryFragmentListener mStepLibraryFragmentListener;
	
	List<Step> mSteps;
	StepListAdapter mStepListAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);    
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mStepListAdapter == null) {
			AsyncTask<Void, Void, List<Step>> asyncTask = new AsyncTask<Void, Void, List<Step>>() {

				@Override
				protected List<Step> doInBackground(Void... args) {
					DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
					dbAdapter.open();
					List<Step> steps = dbAdapter.getAllStepTemplates();
					dbAdapter.close();
					return steps;
				}
				
				protected void onPostExecute(List<Step> result) {
					mSteps = result;
					mStepListAdapter = new StepListAdapter(getActivity(), R.layout.list_item_step_training, mSteps);
			        setListAdapter(mStepListAdapter);
				}
				
			};
			asyncTask.execute();
		}
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		if (mStepLibraryFragmentListener != null) {
			mStepLibraryFragmentListener.onStepSelected(mSteps.get(position));
		}
    }
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    if (getActivity() != null && getActivity().getCurrentFocus() != null) {
		    if (isVisibleToUser) {
    			((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		    }
	    }
	}
	
	public void setStepLibraryFragmentListener(StepLibraryFragmentListener stepLibraryFragmentListener) {
		mStepLibraryFragmentListener = stepLibraryFragmentListener;
	}
	
	public void addStep(Step step) {
		mStepListAdapter.add(step);
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
