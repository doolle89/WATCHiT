package dusan.stefanovic.trainingapp.fragment;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import dusan.stefanovic.trainingapp.database.WATCHiTProcedureTrainerDbAdapter;
import dusan.stefanovic.treningapp.R;

public class CreateProcedureFragment extends Fragment {
	
	private FragmentTabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = (FragmentTabHost) inflater.inflate(R.layout.fragment_create_procedure, container, false);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("info").setIndicator("info"), CreateProcedureInfoFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("steps").setIndicator("steps"), CreateProcedureStepsFragment.class, null);
        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.create_procedure, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_create_procedure:
	            createProcedure();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    List<Fragment> fragments = getChildFragmentManager().getFragments();
	    for (Fragment fragment : fragments) {
	    	fragment.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	private void createProcedure() {
		CreateProcedureInfoFragment createProcedureInfoFragment = (CreateProcedureInfoFragment) getChildFragmentManager().findFragmentByTag("info");
		String title = createProcedureInfoFragment.getTitle();
		String description = createProcedureInfoFragment.getDescripton();
		String photoUrl = createProcedureInfoFragment.getPhotoUrl();
		
		String[] args = {title, description, photoUrl};
		AsyncTask<String, Void, Long> asyncTask = new AsyncTask<String, Void, Long>() {
			
			@Override
			protected void onPreExecute() {
				
			}

			@Override
			protected Long doInBackground(String... args) {
				WATCHiTProcedureTrainerDbAdapter dbAdapter = new WATCHiTProcedureTrainerDbAdapter(getActivity());
				dbAdapter.open();
				long result = dbAdapter.createProcedureTemplate(args[0], args[1], args[2]);
				dbAdapter.close();
				return result;
			}
			
			@Override
			protected void onPostExecute(Long result) {
				String message;
				if (result > -1) {
					message = "Success";
				} else {
					message = "Fail";
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			}
			
		};
		asyncTask.execute(args);
	}
}
