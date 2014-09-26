package dusan.stefanovic.trainingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.fragment.ProcedureListener;
import dusan.stefanovic.trainingapp.fragment.TrainingResultsFragment;
import dusan.stefanovic.treningapp.R;

public class ResultsProcedureActivity extends ActionBarActivity implements ProcedureListener {
	
	ActionBar mActionBar;
    
    Procedure mProcedure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container);
		
		mProcedure = getIntent().getParcelableExtra("procedure");
		
		mActionBar = getSupportActionBar(); 
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setTitle(mProcedure.getTitle());
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		TrainingResultsFragment newFragment = new TrainingResultsFragment();
		fragmentTransaction.add(R.id.fragment_container, newFragment);
		fragmentTransaction.commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
	        	finish();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public Procedure onProcedureRequested() {
		return mProcedure;
	}
}