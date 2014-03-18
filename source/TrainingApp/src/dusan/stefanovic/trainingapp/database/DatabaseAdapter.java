package dusan.stefanovic.trainingapp.database;

import java.util.ArrayList;
import java.util.List;

import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter.WATCHiTProcedureTrainerContract.ProcedureResultEntry;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter.WATCHiTProcedureTrainerContract.ProcedureResultStepResultConnection;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter.WATCHiTProcedureTrainerContract.ProcedureTemplateEntry;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter.WATCHiTProcedureTrainerContract.ProcedureTemplateStepTemplateConnection;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter.WATCHiTProcedureTrainerContract.StepResultEntry;
import dusan.stefanovic.trainingapp.database.DatabaseAdapter.WATCHiTProcedureTrainerContract.StepTemplateEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.Settings.Secure;

public final class DatabaseAdapter {

	public static abstract class WATCHiTProcedureTrainerContract {
		
		private static final String TEXT_TYPE = " TEXT";
		private static final String INTEGER_TYPE = " INTEGER";
		private static final String REAL_TYPE = " REAL";
		private static final String COMMA_SEP = ",";
		
		public static final String[] SQL_CREATE_ENTRIES = {
			StepTemplateEntry.SQL_CREATE_ENTRIE,
			ProcedureTemplateEntry.SQL_CREATE_ENTRIE,
			ProcedureTemplateStepTemplateConnection.SQL_CREATE_ENTRIE,
			StepResultEntry.SQL_CREATE_ENTRIE,
			ProcedureResultEntry.SQL_CREATE_ENTRIE,
			ProcedureResultStepResultConnection.SQL_CREATE_ENTRIE,
		};
		
		public static final String[] SQL_DELETE_ENTRIES = {
			StepTemplateEntry.SQL_DELETE_ENTRIE,
			ProcedureTemplateEntry.SQL_DELETE_ENTRIE,
			ProcedureTemplateStepTemplateConnection.SQL_DELETE_ENTRIE,
			StepResultEntry.SQL_DELETE_ENTRIE,
			ProcedureResultEntry.SQL_DELETE_ENTRIE,
			ProcedureResultStepResultConnection.SQL_DELETE_ENTRIE
		};
		
	    public static abstract class StepTemplateEntry implements BaseColumns {
	    	
	        public static final String TABLE_NAME = "step_template";
	        public static final String COLUMN_NAME_GLOBAL_ID = "global_id";
	        public static final String COLUMN_NAME_TITLE = "title";
	        public static final String COLUMN_NAME_DESCRIPTION = "description";
	        public static final String COLUMN_NAME_PHOTO_URL = "photo_url";
	        public static final String COLUMN_NAME_OPTIMAL_TIME = "optimal_time";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_GLOBAL_ID + " TEXT UNIQUE NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_PHOTO_URL + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_OPTIMAL_TIME + INTEGER_TYPE +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class ProcedureTemplateEntry implements BaseColumns {
	    	
	        public static final String TABLE_NAME = "procedure_template";
	        public static final String COLUMN_NAME_GLOBAL_ID = "global_id";
	        public static final String COLUMN_NAME_TITLE = "title";
	        public static final String COLUMN_NAME_DESCRIPTION = "description";
	        public static final String COLUMN_NAME_PHOTO_URL = "photo_url";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_GLOBAL_ID + " TEXT UNIQUE NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_PHOTO_URL + TEXT_TYPE +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
    
	    public static abstract class ProcedureTemplateStepTemplateConnection {
	    	
	        public static final String TABLE_NAME = "procedure_template_step_template";
	        public static final String COLUMN_NAME_PROCEDURE_TEMPLATE_GLOBAL_ID = "procedure_template_global_id";
	        public static final String COLUMN_NAME_STEP_TEMPLATE_GLOBAL_ID = "step_template_global_id";
	        public static final String COLUMN_NAME_STEP_INDEX = "step_index";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    COLUMN_NAME_PROCEDURE_TEMPLATE_GLOBAL_ID + " TEXT NOT NULL," +
        	    COLUMN_NAME_STEP_TEMPLATE_GLOBAL_ID + " TEXT NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_STEP_INDEX + " INTEGER NOT NULL" + COMMA_SEP +
        	    "PRIMARY KEY (" + COLUMN_NAME_PROCEDURE_TEMPLATE_GLOBAL_ID + COMMA_SEP + COLUMN_NAME_STEP_TEMPLATE_GLOBAL_ID + COMMA_SEP + COLUMN_NAME_STEP_INDEX + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_PROCEDURE_TEMPLATE_GLOBAL_ID + ") REFERENCES " + ProcedureTemplateEntry.TABLE_NAME + "(" + ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_STEP_TEMPLATE_GLOBAL_ID + ") REFERENCES " + StepTemplateEntry.TABLE_NAME + "(" + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class StepResultEntry implements BaseColumns {
	    	
	        public static final String TABLE_NAME = "step_result";
	        public static final String COLUMN_NAME_GLOBAL_ID = "global_id";
	        public static final String COLUMN_NAME_TEMPLATE_ID = "template_id";
	        public static final String COLUMN_NAME_STATUS = "status";
	        public static final String COLUMN_NAME_DURATION = "duration";
	        public static final String COLUMN_NAME_START_TIME = "start_time";
	        public static final String COLUMN_NAME_END_TIME = "end_time";
	        public static final String COLUMN_NAME_ERRORS = "errors";
	        public static final String COLUMN_NAME_SCORE = "score";
	        public static final String COLUMN_NAME_SELF_ASSESSMENT = "self_assessment";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_GLOBAL_ID + " TEXT UNIQUE NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_TEMPLATE_ID + " TEXT NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_DURATION + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_START_TIME + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_END_TIME + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_ERRORS + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_SCORE + REAL_TYPE + COMMA_SEP +
        	    COLUMN_NAME_SELF_ASSESSMENT + REAL_TYPE + COMMA_SEP +
        	    "FOREIGN KEY (" + COLUMN_NAME_TEMPLATE_ID + ") REFERENCES " + StepTemplateEntry.TABLE_NAME + "(" + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class ProcedureResultEntry implements BaseColumns {
	    	
	        public static final String TABLE_NAME = "procedure_result";
	        public static final String COLUMN_NAME_GLOBAL_ID = "global_id";
	        public static final String COLUMN_NAME_TEMPLATE_ID = "template_id";
	        public static final String COLUMN_NAME_USER_ID = "user_id";
	        public static final String COLUMN_NAME_NOTES = "notes";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_GLOBAL_ID + " TEXT UNIQUE NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_TEMPLATE_ID + " TEXT NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_NOTES + TEXT_TYPE + COMMA_SEP +
        	    "FOREIGN KEY (" + COLUMN_NAME_TEMPLATE_ID + ") REFERENCES " + ProcedureTemplateEntry.TABLE_NAME + "(" + ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class ProcedureResultStepResultConnection {
	    	
	        public static final String TABLE_NAME = "procedure_result_step_result";
	        public static final String COLUMN_NAME_PROCEDURE_RESULT_GLOBAL_ID = "procedure_result_global_id";
	        public static final String COLUMN_NAME_STEP_RESULT_GLOBAL_ID = "step_result_global_id";
	        public static final String COLUMN_NAME_STEP_INDEX = "step_index";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    COLUMN_NAME_PROCEDURE_RESULT_GLOBAL_ID + " TEXT NOT NULL," +
        	    COLUMN_NAME_STEP_RESULT_GLOBAL_ID + " TEXT NOT NULL," +
        	    COLUMN_NAME_STEP_INDEX + " INTEGER NOT NULL" + COMMA_SEP +
        	    "PRIMARY KEY (" + COLUMN_NAME_PROCEDURE_RESULT_GLOBAL_ID + COMMA_SEP + COLUMN_NAME_STEP_RESULT_GLOBAL_ID + COMMA_SEP + COLUMN_NAME_STEP_INDEX + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_PROCEDURE_RESULT_GLOBAL_ID + ") REFERENCES " + ProcedureResultEntry.TABLE_NAME + "(" + ProcedureResultEntry.COLUMN_NAME_GLOBAL_ID + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_STEP_RESULT_GLOBAL_ID + ") REFERENCES " + StepResultEntry.TABLE_NAME + "(" + StepResultEntry.COLUMN_NAME_GLOBAL_ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	}
	
	public class SQLiteHelper extends SQLiteOpenHelper {
		
		public static final int DATABASE_VERSION = 1;
	    public static final String DATABASE_NAME = "WATCHiTProcedureTrainer.db";

		public SQLiteHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (int i = 0; i < WATCHiTProcedureTrainerContract.SQL_CREATE_ENTRIES.length; i++) {
				db.execSQL(WATCHiTProcedureTrainerContract.SQL_CREATE_ENTRIES[i]);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // This database is only a cache for online data, so its upgrade policy is
	        // to simply to discard the data and start over
			for (int i = 0; i < WATCHiTProcedureTrainerContract.SQL_CREATE_ENTRIES.length; i++) {
				db.execSQL(WATCHiTProcedureTrainerContract.SQL_DELETE_ENTRIES[i]);
			}
	        onCreate(db);
	    }
		
		@Override
	    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }

	}
	
	private static final long ID_SEPARATOR = 1000000; 
	
	private Context mContext;
	private SQLiteHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private String androidId;
    
    public DatabaseAdapter(Context context) {
        mContext = context;
        androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        androidId = androidId.substring(androidId.length() - 5);
    }
    
    public boolean open() {
        mDbHelper = new SQLiteHelper(mContext);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
        	mDb = null;
        }
        return mDb != null;
    }

    public void close() {
        mDbHelper.close();
    }
    
    private long lastId(String tableName) {
    	long result = 0;
    	String query = "SELECT ROWID from " + tableName + " order by ROWID DESC limit 1";
    	Cursor cursor = mDb.rawQuery(query, null);
    	if (cursor != null && cursor.moveToFirst()) {
    	    result = cursor.getLong(0);
    	}
    	return result;
    }
	
    public String createStepTemplate(String title, String description, String photoUrl, long optimalTime) {
    	String globalId = androidId + (ID_SEPARATOR + lastId(StepTemplateEntry.TABLE_NAME) + 1);
        ContentValues initialValues = new ContentValues();
        initialValues.put(StepTemplateEntry.COLUMN_NAME_GLOBAL_ID, globalId);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_TITLE, title);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_DESCRIPTION, description);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_PHOTO_URL, photoUrl);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME, optimalTime);
        long result = mDb.replace(StepTemplateEntry.TABLE_NAME, null, initialValues); // razmisliti o replace
        if (result == -1) {
        	globalId = null;
        }
        return globalId;
    }
    
    public String createStepTemplate(Step step) {
    	String stepId = createStepTemplate(step.getTitle(), step.getDescription(), step.getPhotoUrl(), step.getOptimalTime());
    	if (stepId != null) {
        	step.setTemplateId(stepId);
    	}
    	return stepId;
    }
    
    protected boolean deleteStepTemplate(String templateId) {
        return mDb.delete(StepTemplateEntry.TABLE_NAME, StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=?", new String[] {templateId}) > 0;
    }
    
    protected boolean deleteStepTemplate(Step step) {
        return deleteStepTemplate(step.getTemplateId());
    }
    
    public List<Step> getAllStepTemplates() {
    	String table = StepTemplateEntry.TABLE_NAME;
    	String[] columns = {StepTemplateEntry.COLUMN_NAME_GLOBAL_ID, StepTemplateEntry.COLUMN_NAME_TITLE, StepTemplateEntry.COLUMN_NAME_DESCRIPTION, StepTemplateEntry.COLUMN_NAME_PHOTO_URL, StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME};
    	String selection = null;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	
    	ArrayList<Step> steps = new ArrayList<Step>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
	        	String templateId = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_GLOBAL_ID));
	        	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
	        	Step step = new Step(templateId, title, description, photoUrl, optimalTime);
	        	steps.add(step);
        	}
        }
        return steps;
    }
    
    public Step getStepTemplate(String templateId) {    	
    	String table = StepTemplateEntry.TABLE_NAME;
    	String[] columns = {StepTemplateEntry.COLUMN_NAME_TITLE, StepTemplateEntry.COLUMN_NAME_DESCRIPTION, StepTemplateEntry.COLUMN_NAME_PHOTO_URL, StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME};
    	String selection = StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=?";
    	String[] selectionArgs = {templateId};
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	Step step = null;
        if (cursor != null && cursor.moveToFirst()) {
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
        	step = new Step(templateId, title, description, photoUrl, optimalTime);
        }
        return step;
    }
    
    
    
    public String createProcedureTemplate(String title, String description, String photoUrl) {
    	String globalId = androidId + (ID_SEPARATOR + lastId(ProcedureTemplateEntry.TABLE_NAME) + 1);
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID, globalId);
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_TITLE, title);
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION, description);
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL, photoUrl);
        long result = mDb.insert(ProcedureTemplateEntry.TABLE_NAME, null, initialValues);
        if (result == -1) {
        	globalId = null;
        }
        return globalId;
    }
    
    public String createProcedureTemplate(Procedure procedure) {
    	String procedureId = createProcedureTemplate(procedure.getTitle(), procedure.getDescription(), procedure.getPhotoUrl());
    	if (procedureId != null) {
    		List<Step> steps = procedure.getSteps();
	    	for (int i=0; i<steps.size(); i++) {
	    		Step step = steps.get(i);
	    		String stepId = step.getTemplateId();
	    		if (stepId == null || stepId.equalsIgnoreCase("")) {
	    			stepId = createStepTemplate(step);
	    		}
	    		connectProcedureTemplateStepTemplate(procedureId, stepId, i);
	    	}
	    	procedure.setTemplateId(procedureId);
    	}
    	return procedureId;
    }
    
    protected boolean deleteProcedureTemplate(String templateId) {
        return mDb.delete(ProcedureTemplateEntry.TABLE_NAME, ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=?", new String[] {templateId}) > 0;
    }
    
    protected boolean deleteProcedureTemplate(Procedure procedure) {
        return deleteProcedureTemplate(procedure.getTemplateId());
    }
    
    public List<Procedure> getAllProcedureTemplates() {
    	String table = ProcedureTemplateEntry.TABLE_NAME;
    	String[] columns = {ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID, ProcedureTemplateEntry.COLUMN_NAME_TITLE, ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION, ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL};
    	String selection = null;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	
    	ArrayList<Procedure> procedures = new ArrayList<Procedure>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
	        	String templateId = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID));
	        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	Procedure procedure = new Procedure(templateId, title, description, photoUrl);
	        	procedure.setSteps(getProcedureTemplateSteps(templateId));
	        	procedures.add(procedure);
        	}
        }
        return procedures;
    }
    
    public Procedure getProcedureTemplate(String templateId) {    	
    	String table = ProcedureTemplateEntry.TABLE_NAME;
    	String[] columns = {ProcedureTemplateEntry.COLUMN_NAME_TITLE, ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION, ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL};
    	String selection = ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=?";
    	String[] selectionArgs = {templateId};
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	Procedure procedure = null;
        if (cursor != null && cursor.moveToFirst()) {
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	procedure = new Procedure(templateId, title, description, photoUrl);
        	procedure.setSteps(getProcedureTemplateSteps(templateId));
        }
        return procedure;
    }
    
    public List<Step> getProcedureTemplateSteps(String templateId) {
    	String query = "SELECT * FROM " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + " INNER JOIN " + StepTemplateEntry.TABLE_NAME + " ON " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + "." + ProcedureTemplateStepTemplateConnection.COLUMN_NAME_STEP_TEMPLATE_GLOBAL_ID + "=" + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + " WHERE " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + "." + ProcedureTemplateStepTemplateConnection.COLUMN_NAME_PROCEDURE_TEMPLATE_GLOBAL_ID + "=? order by " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + "." + ProcedureTemplateStepTemplateConnection.COLUMN_NAME_STEP_INDEX + " ASC";
    	ArrayList<Step> steps = new ArrayList<Step>();
    	Cursor cursor = mDb.rawQuery(query, new String[] {templateId});
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		String stepTemplateId = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_GLOBAL_ID));
        		String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
	        	Step step = new Step(stepTemplateId, title, description, photoUrl, optimalTime);
	        	steps.add(step);
        	}
        }
        return steps;
    }
    
    public long connectProcedureTemplateStepTemplate(String procedureId, String stepId, int position) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureTemplateStepTemplateConnection.COLUMN_NAME_PROCEDURE_TEMPLATE_GLOBAL_ID, procedureId);
        initialValues.put(ProcedureTemplateStepTemplateConnection.COLUMN_NAME_STEP_TEMPLATE_GLOBAL_ID, stepId);
        initialValues.put(ProcedureTemplateStepTemplateConnection.COLUMN_NAME_STEP_INDEX, position);
        return mDb.insert(ProcedureTemplateStepTemplateConnection.TABLE_NAME, null, initialValues);
    }
    
    
    
    
    
    public String createStepResult(String templateId, int status, long duration, long startTime, long endTime, int errors, float score, float selfAssessment) {
    	String globalId = androidId + (ID_SEPARATOR + lastId(StepResultEntry.TABLE_NAME) + 1);
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(StepResultEntry.COLUMN_NAME_GLOBAL_ID, globalId);
        initialValues.put(StepResultEntry.COLUMN_NAME_TEMPLATE_ID, templateId);
        initialValues.put(StepResultEntry.COLUMN_NAME_STATUS, status);
        initialValues.put(StepResultEntry.COLUMN_NAME_DURATION, duration);
        initialValues.put(StepResultEntry.COLUMN_NAME_START_TIME, startTime);
        initialValues.put(StepResultEntry.COLUMN_NAME_END_TIME, endTime);
        initialValues.put(StepResultEntry.COLUMN_NAME_SCORE, score);
        initialValues.put(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT, selfAssessment);
        long result = mDb.insert(StepResultEntry.TABLE_NAME, null, initialValues);
        if (result == -1) {
        	globalId = null;
        }
        return globalId;
    }
    
    public String createStepResult(Step step) {
    	String stepId = createStepResult(step.getTemplateId(),step.getStatus(), step.getDurationNano(), step.getStartTime(), step.getEndTime(), step.getErrors(), step.getScore(), step.getSelfAssessment());
    	if (stepId != null) {
    		step.setId(stepId);
    	}
    	return stepId;
    }
    
    protected boolean deleteStepResult(String resultId) {
        return mDb.delete(StepResultEntry.TABLE_NAME, StepResultEntry.COLUMN_NAME_GLOBAL_ID + "=resultId", new String[] {resultId}) > 0;
    }
    
    protected boolean deleteStepResult(Step step) {
        return deleteStepResult(step.getId());
    }
    
    public List<Step> getAllStepResults(String templateId) {
    	String query = "SELECT * FROM " + StepTemplateEntry.TABLE_NAME + " INNER JOIN " + StepResultEntry.TABLE_NAME + " ON " + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=" + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=?";
    	Cursor cursor = mDb.rawQuery(query, new String[] {templateId});
    	ArrayList<Step> steps = new ArrayList<Step>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
	        	
	        	String resultId = cursor.getString(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_GLOBAL_ID));
	        	int status = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_STATUS));
	        	long duration = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_DURATION));
	        	long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_START_TIME));
	        	long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_END_TIME));
	        	int errors = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_ERRORS));
	        	float score = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SCORE));
	        	float selfAssessment = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT));
	        	
	        	Step step = new Step(templateId, title, description, photoUrl, optimalTime, resultId, status, duration, startTime, endTime, errors, score, selfAssessment);
	        	steps.add(step);
        	}
        }
        return steps;
    }
    
    public Step getStepResult(String resultId) {
    	String query = "SELECT * FROM " + StepTemplateEntry.TABLE_NAME + " INNER JOIN " + StepResultEntry.TABLE_NAME + " ON " + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=" + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_GLOBAL_ID + "=?";
    	Cursor cursor = mDb.rawQuery(query, new String[] {resultId});
    	Step step = null;
        if (cursor != null && cursor.moveToFirst()) {
        	String templateId = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_GLOBAL_ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
        	
        	int status = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_STATUS));
        	long duration = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_DURATION));
        	long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_START_TIME));
        	long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_END_TIME));
        	int errors = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_ERRORS));
        	float score = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SCORE));
        	float selfAssessment = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT));
        	
        	step = new Step(templateId, title, description, photoUrl, optimalTime, resultId, status, duration, startTime, endTime, errors, score, selfAssessment);
        }
        return step;
    }
    
    
    
    
    
    public String getNewProcedureId() {
    	return androidId + (ID_SEPARATOR + lastId(ProcedureResultEntry.TABLE_NAME) + 1);
    }
    
    public String createProcedureResult(String templateId, String userId, String note) {
    	String globalId = androidId + (ID_SEPARATOR + lastId(ProcedureResultEntry.TABLE_NAME) + 1);
        ContentValues initialValues = new ContentValues();
    	initialValues.put(ProcedureResultEntry.COLUMN_NAME_GLOBAL_ID, globalId);
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID, templateId);
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_USER_ID, userId);
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_NOTES, note);
        long result = mDb.insert(ProcedureResultEntry.TABLE_NAME, null, initialValues);
        if (result == -1) {
        	globalId = null;
        }
        return globalId;
    }
    
    public String createProcedureResult(String globalId, String templateId, String userId, String note) {
        ContentValues initialValues = new ContentValues();
    	initialValues.put(ProcedureResultEntry.COLUMN_NAME_GLOBAL_ID, globalId);
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID, templateId);
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_USER_ID, userId);
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_NOTES, note);
        long result = mDb.insert(ProcedureResultEntry.TABLE_NAME, null, initialValues);
        if (result == -1) {
        	globalId = null;
        }
        return globalId;
    }
    
    public String createProcedureResult(Procedure procedure) {
    	String procedureId = procedure.getId();
    	if (procedureId == null) {
    		procedureId = createProcedureResult(procedure.getTemplateId(), procedure.getUserId(), procedure.getNotes());
    	} else {
	    	procedureId = createProcedureResult(procedureId, procedure.getTemplateId(), procedure.getUserId(), procedure.getNotes());
    	}
    	if (procedureId != null) {
    		List<Step> steps = procedure.getSteps();
	    	for (int i=0; i<steps.size(); i++) {
	    		Step step = steps.get(i);
	    		String stepId = createStepResult(step);
	    		connectProcedureResultStepResult(procedureId, stepId, i);
	    	}
	    	procedure.setId(procedureId);
    	}
    	return procedureId;
    }
    
    protected boolean deleteProcedureResult(String resultId) {
        return mDb.delete(ProcedureResultEntry.TABLE_NAME, ProcedureResultEntry.COLUMN_NAME_GLOBAL_ID + "=?", new String[] {resultId}) > 0;
    }
    
    protected boolean deleteProcedureResult(Procedure procedure) {
        return deleteProcedureResult(procedure.getId());
    }
    
    public List<Procedure> getAllProcedureResults(String templateId) {
    	String query = "SELECT * FROM " + ProcedureTemplateEntry.TABLE_NAME + " INNER JOIN " + ProcedureResultEntry.TABLE_NAME + " ON " + ProcedureTemplateEntry.TABLE_NAME + "." + ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=" + ProcedureResultEntry.TABLE_NAME + "." + ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + ProcedureTemplateEntry.TABLE_NAME + "." + ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=?";
    	Cursor cursor = mDb.rawQuery(query, new String[] {templateId});
    	ArrayList<Procedure> procedures = new ArrayList<Procedure>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
	        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	
	        	String resultId = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureResultEntry.COLUMN_NAME_GLOBAL_ID));
	        	String userId = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureResultEntry.COLUMN_NAME_USER_ID));
	        	String notes = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureResultEntry.COLUMN_NAME_NOTES));
	        	
	        	Procedure procedure = new Procedure(templateId, title, description, photoUrl, resultId, userId, notes);
	        	procedure.setSteps(getProcedureResultSteps(resultId));
	        	procedures.add(procedure);
        	}
        }
        return procedures;
    }
    
    public Procedure getProcedureResult(String resultId) {
    	String query = "SELECT * FROM " + ProcedureTemplateEntry.TABLE_NAME + " INNER JOIN " + ProcedureResultEntry.TABLE_NAME + " ON " + ProcedureTemplateEntry.TABLE_NAME + "." + ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID + "=" + ProcedureResultEntry.TABLE_NAME + "." + ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + ProcedureResultEntry.TABLE_NAME + "." + ProcedureResultEntry.COLUMN_NAME_GLOBAL_ID + "=?";
    	Cursor cursor = mDb.rawQuery(query, new String[] {resultId});
    	Procedure procedure = null;
        if (cursor != null && cursor.moveToFirst()) {
        	String templateId = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_GLOBAL_ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	
        	String userId = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureResultEntry.COLUMN_NAME_USER_ID));
        	String notes = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureResultEntry.COLUMN_NAME_NOTES));
        	
        	procedure = new Procedure(templateId, title, description, photoUrl, resultId, userId, notes);
        	procedure.setSteps(getProcedureResultSteps(resultId));
        }
        return procedure;
    }
    
    public List<Step> getProcedureResultSteps(String resultId) {
    	String query = "SELECT * FROM " + ProcedureResultStepResultConnection.TABLE_NAME + " INNER JOIN " + StepResultEntry.TABLE_NAME + " ON " + ProcedureResultStepResultConnection.TABLE_NAME + "." + ProcedureResultStepResultConnection.COLUMN_NAME_STEP_RESULT_GLOBAL_ID + "=" + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_GLOBAL_ID + " INNER JOIN " + StepTemplateEntry.TABLE_NAME + " ON " + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_TEMPLATE_ID + "=" + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry.COLUMN_NAME_GLOBAL_ID + " WHERE " + ProcedureResultStepResultConnection.TABLE_NAME + "." + ProcedureResultStepResultConnection.COLUMN_NAME_PROCEDURE_RESULT_GLOBAL_ID + "=? order by " + ProcedureResultStepResultConnection.TABLE_NAME + "." + ProcedureResultStepResultConnection.COLUMN_NAME_STEP_INDEX + " ASC";
    	ArrayList<Step> steps = new ArrayList<Step>();
    	Cursor cursor = mDb.rawQuery(query, new String[] {resultId});
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		String templateId = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_GLOBAL_ID));
            	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
            	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
            	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
            	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
            	
            	String stepResultId = cursor.getString(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_GLOBAL_ID));
            	int status = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_STATUS));
            	long duration = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_DURATION));
            	long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_START_TIME));
            	long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_END_TIME));
            	int errors = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_ERRORS));
            	float score = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SCORE));
            	float selfAssessment = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT));
            	
            	Step step = new Step(templateId, title, description, photoUrl, optimalTime, stepResultId, status, duration, startTime, endTime, errors, score, selfAssessment);
            	steps.add(step);
        	}
        }
        return steps;
    }
    
    public long connectProcedureResultStepResult(String procedureId, String stepId, int position) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureResultStepResultConnection.COLUMN_NAME_PROCEDURE_RESULT_GLOBAL_ID, procedureId);
        initialValues.put(ProcedureResultStepResultConnection.COLUMN_NAME_STEP_RESULT_GLOBAL_ID, stepId);
        initialValues.put(ProcedureResultStepResultConnection.COLUMN_NAME_STEP_INDEX, position);
        return mDb.insert(ProcedureResultStepResultConnection.TABLE_NAME, null, initialValues);
    }
    
    public List<String> getAllUserIds() {
    	String query = "SELECT DISTINCT " + ProcedureResultEntry.COLUMN_NAME_USER_ID + " FROM " + ProcedureResultEntry.TABLE_NAME;
    	Cursor cursor = mDb.rawQuery(query, null);
    	ArrayList<String> userIds = new ArrayList<String>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		String userId = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureResultEntry.COLUMN_NAME_USER_ID));
        		if (userId != null && !userId.equalsIgnoreCase("")) {
        			userIds.add(userId);
        		}
        	}
        }
        return userIds;
    }
}
