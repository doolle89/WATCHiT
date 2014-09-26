package dusan.stefanovic.trainingapp.app;

import java.util.HashSet;
import java.util.List;

import android.app.Application;
import android.os.AsyncTask;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter;
import dusan.stefanovic.trainingapp.util.MSFHelper;
import dusan.stefanovic.trainingapp.util.MSFHelper.OnReceiveProcedureResultListener;

public class TrainingApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		initWithProcedure();
	}

	private void initWithProcedure() {
		AsyncTask<Void, Void, MSFHelper> asyncTask = new AsyncTask<Void, Void, MSFHelper>() {

			@Override
			protected MSFHelper doInBackground(Void... args) {
				DatabaseAdapter dbAdapter = new DatabaseAdapter(TrainingApp.this);
				dbAdapter.open();
				if (dbAdapter.getAllProcedureTemplates().size() == 0) {
					
					
					Procedure procedure = new Procedure("Procedura Percorso Trauma", "", "");
					Step step = new Step("PPT Passo 1", "Sicurezza ambientale e personale", "");
					step.setOptimalTime(3000);
					procedure.addStep(step);
					step = new Step("PPT Passo 2", "Soccorritore B con approccio frontale immobilizza manualmente il capo del soggetto. Soccorritore A (leader) immobilizza il capo dalla parte posteriore, liberando cosi il soccorritore B, e si prepara A coordinare il soccorso. I soccorritori B e C si posizionano lateralmente e si preparano ad effettuare il logg roll coordinati dal soccorritore A", "");
					step.setOptimalTime(25000);
					procedure.addStep(step);
					step = new Step("PPT Passo 3", "Una volta che il soggetto si trova in posizione supina, i soccorritori B e C posizionano il collare cervicale. B e C preparano la barella atraumatica (cucchiaio) presentandola a lato del soggetto, per poter misurare la lunghezza. B eC aprono il cucchiaio e posizionano una parte della stessa a lato dx e sx del soggetto", "");
					step.setOptimalTime(30000);
					procedure.addStep(step);
					step = new Step("PPT Passo 4", "B e C eseguono un log roll con escursione minima, per poter inserire un lato del cucchiaio effettuano la stessa procedura dal lato opposto agganciano il cucchiaio posizionano le cinghie e le stringono trasferiscono il soggetto sulla tavola spinale, sempre coordinati dal leader tolgono le cinghie aprono il cucchiaio sganciando prima la parte dei piedi.", "");
					step.setOptimalTime(115000);
					procedure.addStep(step);
					step = new Step("PPT Passo 5", "B e C eseguono protezione termica coprendo il soggetto con la coperta termica ”metallina”, posizionano il ragno srotolandolo dalle spalle verso i piedi posizionano i velcri in modo simmetrico partendo dalle spalle (fissare subito i velcri spalle). Un soccorritore stringe il ragno partendo dai piedi, avendo cura di non tirare eccessivamente la cinghia toracica; questo e’ l’unico momento dove e’ consentito passare sopra il soggetto; sempre i soccorritori B e C posizionano i cuscini laterali ed i fermacapo.", "");
					step.setOptimalTime(110000);
					procedure.addStep(step);
					step = new Step("PPT Passo 6", "B e C coordinati dal leader, come in tutte le operazioni, trasferiscono il soggetto sulla barella autocaricante, precedentemente estratta dal vano sanitario dell’ambulanza; i soccorritori B C salgono sull’ ambulanza mentre il soccorritore A (autista) provvede ad assicurarsi della corretta chiusura dei portelloni del mezzo di soccorso poi sale in ambulanza, controlla che nel vano sanitario siano tutti posizionati correttamente (cintura di sicurezza allacciate, ecc. ecc.) e ricevuto il benestare dal responsabile del vano sanitario, parte per l’ospedale di competenza.", "");
					step.setOptimalTime(60000);
					procedure.addStep(step);
					dbAdapter.createProcedureTemplate(procedure);
					
					
					procedure = new Procedure("Scheda di valutazione RCP ad 1 soccorritore AED Adulto", "", "");
					step = new Step("Passo 1", "Valuta la sicurezza ambientale", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 2", "Valuta lo stato di coscienza e respiro normale", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 3", "Attiva il sistema di emergenza/richiede o recupera un AED", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 4", "Scopre il torace della vittima e individua il punto delle mani sul torace per la RCP", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 5", "Eroga il primo ciclo di compressioni alla frequenza corretta (Accettabile < 23 secondi per 30 compressioni )", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 6", "Apre le vie aeree usando la manovra head tilt-chin lift", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 7", "Eroga 2 ventilazioni (ciascuna di 1 secondo)", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 8", "Prosegue cicli di 30 compressioni e 2 ventilazioni\nCorretta posizione mani\nCorretta profondità\nCompleto rilasciamento del torace", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 9", "Accende l’AED", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 10", "Sceglie le placche adeguate e le posiziona correttamente sul torace", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 11", "Fa sicurezza per l’analisi (Deve eseguire un controllo visivo e verbale)", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 12", "Fa sicurezza per lo shock poi preme il tasto shock (Deve eseguire un controllo visivo e verbale)\nTempo massimo dall’arrivo dell’AED < 90 secondi", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 13", "Riprende immediatamente le compressioni toraciche dopo lo shock", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 14", "Eroga il ciclo di compressioni nella posizione corretta e con adeguata profondità e completo rilasciamento del torace", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					step = new Step("Passo 15", "Eroga 2 ventilazioni (ciascuna di 1 secondo)", "");
					step.setOptimalTime(0);
					procedure.addStep(step);
					dbAdapter.createProcedureTemplate(procedure);
				

					// dbAdapter.createDefaultData();
				}		
				
				/*
				MSFHelper msfHelper = new MSFHelper(TrainingApp.this);
				List<Procedure> procedures = msfHelper.getAllProcedureResults();
				HashSet<String> existingTemplateIds = new HashSet<String>();
				for (Procedure procedure : procedures) {
					String templateId = procedure.getTemplateId();
					if (existingTemplateIds.contains(templateId) || dbAdapter.isProcedureTemplate(templateId)) {
						dbAdapter.createProcedureResult(procedure);
						existingTemplateIds.add(templateId);
					}
				}
				dbAdapter.close();
				
				/*
				msfHelper.registerOnReceiveProcedureResultListener(new OnReceiveProcedureResultListener() {
					
					@Override
					public void onProcedureResultReceivedListener(Procedure procedure) {
						DatabaseAdapter dbAdapter = new DatabaseAdapter(TrainingApp.this);
						dbAdapter.open();
						if (dbAdapter.isProcedureTemplate(procedure.getTemplateId())) {
							dbAdapter.createProcedureResult(procedure);
						}
						dbAdapter.close();
					}
				});
				*/
				
				return null;
			}
		};
		asyncTask.execute();
	}
}
