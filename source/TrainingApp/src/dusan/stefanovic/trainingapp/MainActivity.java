package dusan.stefanovic.trainingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class MainActivity extends ActionBarActivity {
	
    TextView mTextView;
    
    Procedure procedure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
                intent.putExtra("Procedure", procedure);
                startActivity(intent);
            }
        });
        
        mTextView = (TextView) findViewById(R.id.textView1);
        
        procedure = new Procedure("TestProcedure");
        procedure.addStep(new Step("Step 1", "description 1"));
        procedure.addStep(new Step("Step 2", "description 2"));
        procedure.addStep(new Step("Step 3", "description 3"));
        procedure.addStep(new Step("Step 4", "description 4"));
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:
	            // openSearch();
	            return true;
	        case R.id.action_settings:
	            // openSettings();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
    

}
