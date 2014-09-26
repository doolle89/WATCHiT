package dusan.stefanovic.trainingapp;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.trainingapp.fragment.CreateProcedureInfoFragment;
import dusan.stefanovic.trainingapp.fragment.CreateProcedureStepsFragment;
import dusan.stefanovic.trainingapp.fragment.ProcedureListener;
import dusan.stefanovic.treningapp.R;

public class CreateProcedureActivity extends ActionBarActivity implements ActionBar.TabListener, ProcedureListener {

	public static final String EXTRA_PROCEDURE_DATA = "procedure";
	
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	private Procedure mProcedure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_pager);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		mProcedure = new Procedure();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_procedure, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case android.R.id.home:
		    	showQuitDialog();
	            return true;
		    case R.id.action_create_procedure:
		    	createAndPublishProcedure();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
		
	}

	@Override
	public Procedure onProcedureRequested() {
		return mProcedure;
	}
	
	@Override
    public void onBackPressed() {
		showQuitDialog();
    }
	
	protected void showQuitDialog() {
		DialogFragment dialog = new QuitDialogFragment();
		dialog.show(getSupportFragmentManager(), "quit_dialog");
	}
	
	public void createAndPublishProcedure() {
    	CreateProcedureInfoFragment createProcedureInfoFragment = mSectionsPagerAdapter.getCreateProcedureInfoFragment();
    	if (createProcedureInfoFragment != null) { 
			mProcedure.setTitle(createProcedureInfoFragment.getTitle());
			mProcedure.setDescription(createProcedureInfoFragment.getDescripton());
			mProcedure.setPhotoUrl(createProcedureInfoFragment.getPhotoUrl());
			
			if (mProcedure.getTitle().equalsIgnoreCase("")) {
				Toast.makeText(CreateProcedureActivity.this, "Please enter procedure title first", Toast.LENGTH_SHORT).show();
				return;
			} else if (mProcedure.getSteps().size() < 3) {
				Toast.makeText(CreateProcedureActivity.this, "Procedure must contain at least 3 steps", Toast.LENGTH_SHORT).show();
				return;
			}
			AsyncTask<Procedure, Void, Procedure> asyncTask = new AsyncTask<Procedure, Void, Procedure>() {

				@Override
				protected Procedure doInBackground(Procedure... args) {
					if (CreateProcedureActivity.this != null) {
						DatabaseAdapter dbAdapter = new DatabaseAdapter(CreateProcedureActivity.this);
						dbAdapter.open();
						dbAdapter.createProcedureTemplate(args[0]);
						dbAdapter.close();
					}
					return args[0];
				}
				
				@Override
				protected void onPostExecute(Procedure result) {
					if (CreateProcedureActivity.this != null) {
						if (result.getTemplateId() != null) {
							Toast.makeText(CreateProcedureActivity.this, "Procedure created seccessfully", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							intent.putExtra(EXTRA_PROCEDURE_DATA, result);
							setResult(Activity.RESULT_OK, intent);
							finish();
						} else {
							Toast.makeText(CreateProcedureActivity.this, "Unable to create procedure", Toast.LENGTH_SHORT).show();
						}
					}
				}
				
			};
			asyncTask.execute(mProcedure);
			
    	}
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new CreateProcedureInfoFragment();
				case 1:
					return new CreateProcedureStepsFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale locale = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.create_procedure_activity_title_info).toUpperCase(locale);
				case 1:
					return getString(R.string.create_procedure_activity_title_steps).toUpperCase(locale);
			}
			return null;
		}
		
		public CreateProcedureInfoFragment getCreateProcedureInfoFragment() {
        	List<Fragment> fragments = getSupportFragmentManager().getFragments();
        	if (fragments != null) {
        		for (Fragment fragment : fragments) {
        			if (fragment instanceof CreateProcedureInfoFragment) {
        				return (CreateProcedureInfoFragment) fragment;
        			}
        		}
        	}
        	return null;
        }
	}
	
	public static class QuitDialogFragment extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	builder.setTitle(getText(R.string.create_procedure_activity_quit_dialog_title));
	    	builder.setMessage(getText(R.string.create_procedure_activity_quit_dialog_message));
	    	builder.setPositiveButton(getText(R.string.button_yes), new DialogInterface.OnClickListener() {
	    		
	    		@Override
	    		public void onClick(DialogInterface dialog, int which) {
	    	        getActivity().finish();
	            }
	    		
	    	});
	        builder.setNegativeButton(getText(R.string.button_no), new DialogInterface.OnClickListener() {
	        	
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        		
	            }
	        	
	        });
	        return builder.create();
		}
		 
	}

}
