package dusan.stefanovic.trainingapp.fragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.ResultPreviewProcedureActivity;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

public class LeaderboardFragment extends ListFragment {
    
    public static final int SORT_CRITERIA_SCORE = 0;
    public static final int SORT_CRITERIA_TIME = 1;
	
	private List<Procedure> mProcedures;
	private ProcedureListAdapter mListAdapter;
	
	private AutoCompleteTextView mFilterSpinner;
	private Spinner mSortSpinner;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_training_results, container, false);
        mFilterSpinner = (AutoCompleteTextView) rootView.findViewById(R.id.spinner_filter);
        mSortSpinner = (Spinner) rootView.findViewById(R.id.spinner_sort);        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mSortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (mListAdapter != null) {
					switch (position) {
						case SORT_CRITERIA_SCORE:
							mListAdapter.setSortComparator(Procedure.SCORE_COMPARATOR);
							break;
						case SORT_CRITERIA_TIME:
							mListAdapter.setSortComparator(Procedure.TIME_COMPARATOR);
							break;
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		String[] sortItems = getResources().getStringArray(R.array.leaderboard_fragment_sort_spinner_array);
		ArrayAdapter<String> sortSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sortItems);
		mSortSpinner.setAdapter(sortSpinnerArrayAdapter);
		
		mFilterSpinner.setHint("All procedures");
	    mFilterSpinner.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mFilterSpinner.showDropDown();
				return false;
			}
		});
	    mFilterSpinner.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(mFilterSpinner.getWindowToken(), 0);
			}
	    });
	    mFilterSpinner.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mListAdapter != null) {
					mListAdapter.getFilter().filter(s.toString());
				}
			}
	    	
	    });
	    
	    AsyncTask<Procedure, Void, List<String>> asyncTask = new AsyncTask<Procedure, Void, List<String> >() {

			@Override
			protected List<String> doInBackground(Procedure... args) {
				ArrayList<String> results = new ArrayList<String>();
				if (getActivity() != null) {
					DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
					dbAdapter.open();
					List<Procedure> procedures = dbAdapter.getAllProcedureTemplates();
					dbAdapter.close();
					for (Procedure procedure : procedures) {
						results.add(procedure.getTitle());
					}
				}
				return results;
			}
			
			@Override
			protected void onPostExecute(List<String> results) {
				if (getActivity() != null) {
					if (results != null && results.size() > 0) {
						ArrayAdapter<String> filterSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, results);
					    mFilterSpinner.setAdapter(filterSpinnerArrayAdapter);
					}
				}
			}
			
		};
		asyncTask.execute();
		
		loadList();
    }
	
	@Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), ResultPreviewProcedureActivity.class);
        intent.putExtra("procedure", mProcedures.get(position));
        startActivity(intent);
    }
	
	public void loadList() {
		AsyncTask<Long, Void, List<Procedure>> asyncTask = new AsyncTask<Long, Void, List<Procedure>>() {

			@Override
			protected List<Procedure> doInBackground(Long... args) {
				ArrayList<Procedure> results = new ArrayList<Procedure>();
				if (getActivity() != null) {
					DatabaseAdapter dbAdapter = new DatabaseAdapter(getActivity());
					dbAdapter.open();
					List<Procedure> procedures = dbAdapter.getAllProcedureResults();
					dbAdapter.close();
					ArrayList<String> userIds = new ArrayList<String>();
					for (int i=0; i<procedures.size()-1; i++) {
						Procedure procedure = procedures.get(i);
						String userId = procedure.getUserId();
						if (userId != null && !userId.contentEquals("") && !userIds.contains(userId)) {
							userIds.add(userId);
							for (int j=i+1; j<procedures.size(); j++) {
								if (userId.contentEquals(procedures.get(j).getUserId()) && procedure.getScore() < procedures.get(j).getScore()) {
									procedure = procedures.get(j);
								}
							}
							results.add(procedure);
						}
					}
					if (procedures.size() > 0) {
						String userId = procedures.get(procedures.size()-1).getUserId();
						if (userId != null && !userId.contentEquals("") && !userIds.contains(userId)) {
							results.add(procedures.get(procedures.size()-1));
						}
					}
				}
				return results;
			}
			
			protected void onPostExecute(List<Procedure> result) {
				mProcedures = result;
				if (getActivity() != null) {
					mListAdapter = new ProcedureListAdapter(getActivity(), 0, mProcedures);
					mListAdapter.setSortComparator(null);
			        setListAdapter(mListAdapter);
				}
			}
			
		};
		asyncTask.execute();
	}
	
	public static class ProcedureListAdapter extends ArrayAdapter<Procedure> implements Filterable {
		
		List<Procedure> dataList;
		List<Procedure> filteredList;
		Comparator<Procedure> sortComparator = Procedure.SCORE_COMPARATOR;
    	
    	static class ViewHolder {
    		TextView user;
    		TextView score;
    		TextView id;
    	}
    	
    	public ProcedureListAdapter(Context context, int resource, List<Procedure> objects) {
    		super(context, resource, objects);
    		dataList = new ArrayList<Procedure>(objects);
    		filteredList = objects;
    		
    	}

    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_item_result_procedure, null);
                holder = new ViewHolder();
                holder.user = (TextView) row.findViewById(R.id.textView_username);
                holder.score = (TextView) row.findViewById(R.id.textView_score);
                holder.id = (TextView) row.findViewById(R.id.textView_id);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            
            final Procedure procedure = this.getItem(position);
            String user = procedure.getUserId();
            holder.user.setText((user != null && !user.contentEquals("")) ? user : "Anonymous");
            holder.score.setText(String.format("%.2f", procedure.getScore() < 0 ? 0 : procedure.getScore()));
            holder.id.setText(procedure.getTitle());
            
            return row;
        }
    	
    	@Override
        public Filter getFilter() {
            Filter filter = new Filter() {

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<Procedure> filteredArrayList = new ArrayList<Procedure>();

	                String constraintString = constraint.toString().toLowerCase(Locale.getDefault());
	                for (int i=0; i<dataList.size(); i++) {
	                    Procedure procedure = dataList.get(i);
	                    if (procedure.getTitle().toLowerCase(Locale.getDefault()).startsWith(constraintString))  {
	                    	filteredArrayList.add(procedure);
	                    }
	                }

	                results.count = filteredArrayList.size();
	                results.values = filteredArrayList;

	                return results;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredList.clear();
					addAll((List<Procedure>) results.values);
					sort(sortComparator);
				}
            };

            return filter;
        }
    	
    	public void setSortComparator(Comparator<Procedure> comparator) {
    		if (comparator != null) {
    			sortComparator = comparator;
    		}
    		sort(sortComparator);
    	}
    }
}
