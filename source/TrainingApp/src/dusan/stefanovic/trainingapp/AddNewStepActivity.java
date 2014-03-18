package dusan.stefanovic.trainingapp;


import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.fragment.CreateProcedureStepsFragment;
import dusan.stefanovic.trainingapp.fragment.CreateStepFragment;
import dusan.stefanovic.trainingapp.fragment.CreateStepFragment.CreateStepFragmentListener;
import dusan.stefanovic.trainingapp.fragment.StepLibraryFragment;
import dusan.stefanovic.trainingapp.fragment.StepLibraryFragment.StepLibraryFragmentListener;
import dusan.stefanovic.treningapp.R;

public class AddNewStepActivity extends ActionBarActivity implements ActionBar.TabListener, CreateStepFragmentListener {

	
	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_pager);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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
		
		int tabIndex = getIntent().getIntExtra(CreateProcedureStepsFragment.EXTRA_TAB_INDEX, 0);
		mViewPager.setCurrentItem(tabIndex);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	public void onStepSaved(Step step) {
		mSectionsPagerAdapter.getStepLibraryFragment().addStep(step);
		mViewPager.setCurrentItem(1);
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new CreateStepFragment();
				case 1:
					StepLibraryFragment stepLibraryFragment = new StepLibraryFragment();
					stepLibraryFragment.setStepLibraryFragmentListener(new StepLibraryFragmentListener() {
						
						@Override
						public void onStepSelected(Step step) {
							Intent intent = new Intent();
							intent.putExtra(CreateProcedureStepsFragment.EXTRA_STEP_DATA, step);
							setResult(Activity.RESULT_OK, intent);
							finish();
						}
					});
					return stepLibraryFragment;
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
					return getString(R.string.add_new_step_activity_title_new).toUpperCase(locale);
				case 1:
					return getString(R.string.add_new_step_activity_title_library).toUpperCase(locale);
			}
			return null;
		}
		
		public StepLibraryFragment getStepLibraryFragment() {
        	List<Fragment> fragments = getSupportFragmentManager().getFragments();
        	if (fragments != null) {
        		for (Fragment fragment : fragments) {
        			if (fragment instanceof StepLibraryFragment) {
        				return (StepLibraryFragment) fragment;
        			}
        		}
        	}
        	return null;
        }
	}

}
