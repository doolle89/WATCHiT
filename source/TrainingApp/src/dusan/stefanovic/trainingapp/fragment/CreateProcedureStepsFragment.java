package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import dusan.stefanovic.dragsortlistview.DragSortListView;
import dusan.stefanovic.trainingapp.AddNewStepActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.dialog.StepDialogFragment;
import dusan.stefanovic.treningapp.R;

public class CreateProcedureStepsFragment extends ListFragment {
	
	public static final int ACTION_GET_STEP = 534;
	public static final String EXTRA_STEP_DATA = "step";
	public static final String EXTRA_TAB_INDEX = "tab_index";
	
	private int mCreateStepTabIndex = 0;
	
	Button mAddButton;
	
	Procedure mProcedure;
	StepListAdapter mStepListAdapter;
	
	private DragSortListView mDragSortListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setRetainInstance(true);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mDragSortListView = (DragSortListView) inflater.inflate(R.layout.fragment_create_procedure_steps, container, false);		
		mAddButton = (Button) inflater.inflate(R.layout.list_footer_button, null, false);
        return mDragSortListView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		try {
			ProcedureListener trainingProcedureListener = (ProcedureListener) getActivity();
			mProcedure = trainingProcedureListener.onProcedureRequested();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement ProcedureListener");
        }
		mAddButton.setText("Add step");
		mAddButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AddNewStepActivity.class);
				intent.putExtra(EXTRA_TAB_INDEX, mCreateStepTabIndex);
				startActivityForResult(intent, ACTION_GET_STEP);
			}
		});
		mDragSortListView.addFooterView(mAddButton);
		mDragSortListView.setDropListener(new DragSortListView.DropListener() {
        	
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    Step step = mStepListAdapter.getItem(from);
                    mStepListAdapter.remove(step);
                    mStepListAdapter.insert(step, to);
                }
            }
        });
		mDragSortListView.setRemoveListener(new DragSortListView.RemoveListener() {
        	
            @Override
            public void remove(int which) {
            	mStepListAdapter.remove(mStepListAdapter.getItem(which));
            }
        });
		
		if (mStepListAdapter == null) {
			mStepListAdapter = new StepListAdapter(getActivity(), R.layout.list_item_step_training, mProcedure.getSteps());
	        setListAdapter(mStepListAdapter);
		}
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		StepDialogFragment dialogFragment = StepDialogFragment.getInstance(mProcedure.getStep(position));
		dialogFragment.show(getChildFragmentManager(), "step_dialog_fragment");
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTION_GET_STEP) {
			if (resultCode == Activity.RESULT_OK) {
				Step step = data.getParcelableExtra(EXTRA_STEP_DATA);
				if (step.getTemplateId() == null || step.getTemplateId().equalsIgnoreCase("")) {
					mCreateStepTabIndex = 0;
				} else {
					mCreateStepTabIndex = 1;
				}
				mStepListAdapter.add(step);
			}
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
            // holder.description.setText(step.getDescription());
            holder.description.setText(getContext().getText(R.string.procedure_preview_activity_procedure_description));
            return row;
        }
    }
}
