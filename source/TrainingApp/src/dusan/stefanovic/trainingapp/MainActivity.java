package dusan.stefanovic.trainingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.treningapp.R;

public class MainActivity extends ActionBarActivity {
	
	private boolean mLogedIn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (mLogedIn) {
			Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
        });
        
        initWithProcedure();
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
	        //case R.id.action_search:
	            // openSearch();
	           // return true;
	        case R.id.action_settings:
	            // openSettings();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
    private void initWithProcedure() {
		
		AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... args) {
				DatabaseAdapter dbAdapter = new DatabaseAdapter(MainActivity.this);
				dbAdapter.open();
				if (dbAdapter.getAllProcedureTemplates().size() == 0) {
					Procedure procedure = new Procedure("Procedura Percorso Trauma", 
							"", 
							"");
					Step step = new Step("Passo 1", "Sicurezza ambientale e personale", "");
					step.setOptimalTime(3000);
					procedure.addStep(step);
					step = new Step("Passo 2", "Soccorritore B con approccio frontale immobilizza manualmente il capo del soggetto. Soccorritore A (leader) immobilizza il capo dalla parte posteriore, liberando cosi il soccorritore B, e si prepara A coordinare il soccorso. I soccorritori B e C si posizionano lateralmente e si preparano ad effettuare il logg roll coordinati dal soccorritore A", "");
					step.setOptimalTime(25000);
					procedure.addStep(step);
					step = new Step("Passo 3", "Una volta che il soggetto si trova in posizione supina, i soccorritori B e C posizionano il collare cervicale. B e C preparano la barella atraumatica (cucchiaio) presentandola a lato del soggetto, per poter misurare la lunghezza. B eC aprono il cucchiaio e posizionano una parte della stessa a lato dx e sx del soggetto", "");
					step.setOptimalTime(30000);
					procedure.addStep(step);
					step = new Step("Passo 4", "B e C eseguono un log roll con escursione minima, per poter inserire un lato del cucchiaio effettuano la stessa procedura dal lato opposto agganciano il cucchiaio posizionano le cinghie e le stringono trasferiscono il soggetto sulla tavola spinale, sempre coordinati dal leader tolgono le cinghie aprono il cucchiaio sganciando prima la parte dei piedi.", "");
					step.setOptimalTime(115000);
					procedure.addStep(step);
					step = new Step("Passo 5", "B e C eseguono protezione termica coprendo il soggetto con la coperta termica ”metallina”, posizionano il ragno srotolandolo dalle spalle verso i piedi posizionano i velcri in modo simmetrico partendo dalle spalle (fissare subito i velcri spalle). Un soccorritore stringe il ragno partendo dai piedi, avendo cura di non tirare eccessivamente la cinghia toracica; questo e’ l’unico momento dove e’ consentito passare sopra il soggetto; sempre i soccorritori B e C posizionano i cuscini laterali ed i fermacapo.", "");
					step.setOptimalTime(110000);
					procedure.addStep(step);
					step = new Step("Passo 6", "B e C coordinati dal leader, come in tutte le operazioni, trasferiscono il soggetto sulla barella autocaricante, precedentemente estratta dal vano sanitario dell’ambulanza; i soccorritori B C salgono sull’ ambulanza mentre il soccorritore A (autista) provvede ad assicurarsi della corretta chiusura dei portelloni del mezzo di soccorso poi sale in ambulanza, controlla che nel vano sanitario siano tutti posizionati correttamente (cintura di sicurezza allacciate, ecc. ecc.) e ricevuto il benestare dal responsabile del vano sanitario, parte per l’ospedale di competenza.", "");
					step.setOptimalTime(60000);
					procedure.addStep(step);
					dbAdapter.createProcedureTemplate(procedure);
				}
				dbAdapter.close();
				return null;
			}
		};
		asyncTask.execute();
	}
}
