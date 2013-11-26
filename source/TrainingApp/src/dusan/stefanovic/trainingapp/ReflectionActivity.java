/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dusan.stefanovic.trainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.fragment.CompareToFragment;
import dusan.stefanovic.trainingapp.fragment.DummyFragment;
import dusan.stefanovic.trainingapp.fragment.ProcedureListener;
import dusan.stefanovic.trainingapp.fragment.RealityCheckFragment;
import dusan.stefanovic.trainingapp.fragment.SelfAssessmentFragment;
import dusan.stefanovic.treningapp.R;

public class ReflectionActivity extends ActionBarActivity implements ProcedureListener {

    SectionPagerAdapter mSectionPagerAdapter;

    ViewPager mViewPager;
    Button mNextButton;
    
    Procedure mProcedure;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflection);
        
        mProcedure = getIntent().getParcelableExtra("procedure");
        if (mProcedure == null) {
        	finish();
        }
        
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}

			@Override
			public void onPageSelected(int position) {
				if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1) {
					mNextButton.setEnabled(false);
				}
				if (position == SectionPagerAdapter.STEP_2) {
					RealityCheckFragment realityCheckFragment = (RealityCheckFragment) mViewPager.getAdapter().instantiateItem(mViewPager, SectionPagerAdapter.STEP_2);
					realityCheckFragment.refresh();
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
        	
        });
        
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mViewPager.getCurrentItem() < mViewPager.getAdapter().getCount() - 1) {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
				}
			}
        	
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public Procedure onProcedureRequested() {
		return mProcedure;
	}
    
    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
    	
    	static final int STEP_1 = 0;
    	static final int STEP_2 = 1;
    	static final int STEP_3 = 2;

        public SectionPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
        	Fragment fragment = null;
        	switch (i) {
        		case STEP_1:
        			fragment = new SelfAssessmentFragment();
        			break;
        		case STEP_2:
        			fragment = new RealityCheckFragment();
        			break;
        		case STEP_3:
        			fragment = new CompareToFragment();
        			break;
        			
        		default:
        			fragment = new RealityCheckFragment();
        			break;
        	}
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	switch (position) {
	    		case STEP_1:
	    			return getText(R.string.training_activity_tab_results);
	    		case STEP_2:
	    			return getText(R.string.training_activity_tab_results);
	    		case STEP_3:
	    			return getText(R.string.training_activity_tab_results);
	    	}
	        return null;
        }
    }
}
