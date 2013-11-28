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
	        public static final String COLUMN_NAME_TITLE = "title";
	        public static final String COLUMN_NAME_DESCRIPTION = "description";
	        public static final String COLUMN_NAME_PHOTO_URL = "photo_url";
	        public static final String COLUMN_NAME_OPTIMAL_TIME = "optimal_time";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
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
	        public static final String COLUMN_NAME_TITLE = "title";
	        public static final String COLUMN_NAME_DESCRIPTION = "description";
	        public static final String COLUMN_NAME_PHOTO_URL = "photo_url";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_PHOTO_URL + TEXT_TYPE +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
    
	    public static abstract class ProcedureTemplateStepTemplateConnection {
	    	
	        public static final String TABLE_NAME = "procedure_template_step_template";
	        public static final String COLUMN_NAME_PROCEDURE_TEMPLATE_ID = "procedure_template_id";
	        public static final String COLUMN_NAME_STEP_TEMPLATE_ID = "step_template_id";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    COLUMN_NAME_PROCEDURE_TEMPLATE_ID + " INTEGER NOT NULL," +
        	    COLUMN_NAME_STEP_TEMPLATE_ID + " INTEGER NOT NULL," +
        	    "PRIMARY KEY (" + COLUMN_NAME_PROCEDURE_TEMPLATE_ID + COMMA_SEP + COLUMN_NAME_STEP_TEMPLATE_ID + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_PROCEDURE_TEMPLATE_ID + ") REFERENCES " + ProcedureTemplateEntry.TABLE_NAME + "(" + ProcedureTemplateEntry._ID + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_STEP_TEMPLATE_ID + ") REFERENCES " + StepTemplateEntry.TABLE_NAME + "(" + StepTemplateEntry._ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class StepResultEntry implements BaseColumns {
	    	
	        public static final String TABLE_NAME = "step_result";
	        public static final String COLUMN_NAME_TEMPLATE_ID = "template_id";
	        public static final String COLUMN_NAME_STATUS = "status";
	        public static final String COLUMN_NAME_DURATION = "duration";
	        public static final String COLUMN_NAME_START_TIME = "start_time";
	        public static final String COLUMN_NAME_END_TIME = "end_time";
	        public static final String COLUMN_NAME_SCORE = "score";
	        public static final String COLUMN_NAME_SELF_ASSESSMENT = "self_assessment";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_TEMPLATE_ID + " INTEGER NOT NULL" + COMMA_SEP +
        	    COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_DURATION + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_START_TIME + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_END_TIME + INTEGER_TYPE + COMMA_SEP +
        	    COLUMN_NAME_SCORE + TEXT_TYPE + COMMA_SEP +
        	    COLUMN_NAME_SELF_ASSESSMENT + REAL_TYPE + COMMA_SEP +
        	    "FOREIGN KEY (" + COLUMN_NAME_TEMPLATE_ID + ") REFERENCES " + StepTemplateEntry.TABLE_NAME + "(" + StepTemplateEntry._ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class ProcedureResultEntry implements BaseColumns {
	    	
	        public static final String TABLE_NAME = "procedure_result";
	        public static final String COLUMN_NAME_TEMPLATE_ID = "template_id";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    _ID + " INTEGER PRIMARY KEY," +
        	    COLUMN_NAME_TEMPLATE_ID + " INTEGER NOT NULL" + COMMA_SEP +
        	    "FOREIGN KEY (" + COLUMN_NAME_TEMPLATE_ID + ") REFERENCES " + ProcedureTemplateEntry.TABLE_NAME + "(" + ProcedureTemplateEntry._ID + ")" +
        	    " )";

        	public static final String SQL_DELETE_ENTRIE =
        		"DROP TABLE IF EXISTS " + TABLE_NAME;
	    }
	    
	    public static abstract class ProcedureResultStepResultConnection {
	    	
	        public static final String TABLE_NAME = "procedure_result_step_result";
	        public static final String COLUMN_NAME_PROCEDURE_RESULT_ID = "procedure_result_id";
	        public static final String COLUMN_NAME_STEP_RESULT_ID = "step_result_id";
	        
	        public static final String SQL_CREATE_ENTRIE =
        	    "CREATE TABLE " + TABLE_NAME + " (" +
        	    COLUMN_NAME_PROCEDURE_RESULT_ID + " INTEGER NOT NULL," +
        	    COLUMN_NAME_STEP_RESULT_ID + " INTEGER NOT NULL," +
        	    "PRIMARY KEY (" + COLUMN_NAME_PROCEDURE_RESULT_ID + COMMA_SEP + COLUMN_NAME_STEP_RESULT_ID + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_PROCEDURE_RESULT_ID + ") REFERENCES " + ProcedureResultEntry.TABLE_NAME + "(" + ProcedureResultEntry._ID + ")" +
        	    "FOREIGN KEY (" + COLUMN_NAME_STEP_RESULT_ID + ") REFERENCES " + StepResultEntry.TABLE_NAME + "(" + StepResultEntry._ID + ")" +
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
	
	private Context mContext;
	private SQLiteHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    public DatabaseAdapter(Context context) {
        mContext = context;
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
	
    public long createStepTemplate(String title, String description, String photoUrl, long optimalTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(StepTemplateEntry.COLUMN_NAME_TITLE, title);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_DESCRIPTION, description);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_PHOTO_URL, photoUrl);
        initialValues.put(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME, optimalTime);
        return mDb.insert(StepTemplateEntry.TABLE_NAME, null, initialValues);
    }
    
    public long createStepTemplate(Step step) {
    	return createStepTemplate(step.getTitle(), step.getDescription(), step.getPhotoUrl(), step.getOptimalTime());
    }
    
    public boolean deleteStepTemplate(long templateId) {
        return mDb.delete(StepTemplateEntry.TABLE_NAME, StepTemplateEntry._ID + "=" + templateId, null) > 0;
    }
    
    public boolean deleteStepTemplate(Step step) {
        return deleteStepTemplate(step.getTemplateId());
    }
    
    public List<Step> getAllStepTemplates() {
    	String table = StepTemplateEntry.TABLE_NAME;
    	String[] columns = {StepTemplateEntry._ID, StepTemplateEntry.COLUMN_NAME_TITLE, StepTemplateEntry.COLUMN_NAME_DESCRIPTION, StepTemplateEntry.COLUMN_NAME_PHOTO_URL, StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME};
    	String selection = null;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	
    	ArrayList<Step> steps = new ArrayList<Step>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
	        	long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
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
    
    public Step getStepTemplate(long templateId) {    	
    	String table = StepTemplateEntry.TABLE_NAME;
    	String[] columns = {StepTemplateEntry.COLUMN_NAME_TITLE, StepTemplateEntry.COLUMN_NAME_DESCRIPTION, StepTemplateEntry.COLUMN_NAME_PHOTO_URL, StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME};
    	String selection = StepTemplateEntry._ID + "=" + templateId;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	Step step = null;
        if (cursor != null && cursor.moveToFirst()) {
        	// long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
        	step = new Step(templateId, title, description, photoUrl, optimalTime);
        }
        return step;
    }
    
    
    
    public long createProcedureTemplate(String title, String description, String photoUrl) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_TITLE, title);
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION, description);
        initialValues.put(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL, photoUrl);
        return mDb.insert(ProcedureTemplateEntry.TABLE_NAME, null, initialValues);
    }
    
    public long createProcedureTemplate(Procedure procedure) {
    	long procedureId = createProcedureTemplate(procedure.getTitle(), procedure.getDescription(), procedure.getPhotoUrl());
    	for (Step step : procedure.getSteps()) {
    		long stepId = createStepTemplate(step);
    		connectProcedureTemplateStepTemplate(procedureId, stepId);
    	}
    	return procedureId;
    }
    
    public boolean deleteProcedureTemplate(long templateId) {
        return mDb.delete(ProcedureTemplateEntry.TABLE_NAME, ProcedureTemplateEntry._ID + "=" + templateId, null) > 0;
    }
    
    public boolean deleteProcedureTemplate(Procedure procedure) {
        return deleteProcedureTemplate(procedure.getTemplateId());
    }
    
    public List<Procedure> getAllProcedureTemplates() {
    	String table = ProcedureTemplateEntry.TABLE_NAME;
    	String[] columns = {ProcedureTemplateEntry._ID, ProcedureTemplateEntry.COLUMN_NAME_TITLE, ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION, ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL};
    	String selection = null;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	
    	ArrayList<Procedure> procedures = new ArrayList<Procedure>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
	        	long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry._ID));
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
    
    public Procedure getProcedureTemplate(long templateId) {    	
    	String table = ProcedureTemplateEntry.TABLE_NAME;
    	String[] columns = {ProcedureTemplateEntry.COLUMN_NAME_TITLE, ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION, ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL};
    	String selection = ProcedureTemplateEntry._ID + "=" + templateId;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	Procedure procedure = null;
        if (cursor != null && cursor.moveToFirst()) {
        	// long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	procedure = new Procedure(templateId, title, description, photoUrl);
        	procedure.setSteps(getProcedureTemplateSteps(templateId));
        }
        return procedure;
    }
    
    public List<Step> getProcedureTemplateSteps(long templateId) {
    	String query = "SELECT * FROM " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + " INNER JOIN " + StepTemplateEntry.TABLE_NAME + " ON " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + "." + ProcedureTemplateStepTemplateConnection.COLUMN_NAME_STEP_TEMPLATE_ID + "=" + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry._ID + " WHERE " + ProcedureTemplateStepTemplateConnection.TABLE_NAME + "." + ProcedureTemplateStepTemplateConnection.COLUMN_NAME_PROCEDURE_TEMPLATE_ID + "=" + templateId;
    	ArrayList<Step> steps = new ArrayList<Step>();
    	Cursor cursor = mDb.rawQuery(query, null);
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		long stepTemplateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
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
    
    public long connectProcedureTemplateStepTemplate(long procedureId, long stepId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureTemplateStepTemplateConnection.COLUMN_NAME_PROCEDURE_TEMPLATE_ID, procedureId);
        initialValues.put(ProcedureTemplateStepTemplateConnection.COLUMN_NAME_STEP_TEMPLATE_ID, stepId);
        return mDb.insert(ProcedureTemplateStepTemplateConnection.TABLE_NAME, null, initialValues);
    }
    
    
    
    
    
    public long createStepResult(long templateId, int status, long duration, long startTime, long endTime, int score, float selfAssessment) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(StepResultEntry.COLUMN_NAME_TEMPLATE_ID, templateId);
        initialValues.put(StepResultEntry.COLUMN_NAME_STATUS, status);
        initialValues.put(StepResultEntry.COLUMN_NAME_DURATION, duration);
        initialValues.put(StepResultEntry.COLUMN_NAME_START_TIME, startTime);
        initialValues.put(StepResultEntry.COLUMN_NAME_END_TIME, endTime);
        initialValues.put(StepResultEntry.COLUMN_NAME_SCORE, score);
        initialValues.put(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT, selfAssessment);
        return mDb.insert(StepResultEntry.TABLE_NAME, null, initialValues);
    }
    
    public long createStepResult(Step step) {
    	return createStepResult(step.getTemplateId(),step.getStatus(), step.getDurationNano(), step.getStartTime(), step.getEndTime(), step.getScore(), step.getSelfAssessment());
    }
    
    public boolean deleteStepResult(long resultId) {
        return mDb.delete(StepResultEntry.TABLE_NAME, StepResultEntry._ID + "=" + resultId, null) > 0;
    }
    
    public boolean deleteStepResult(Step step) {
        return deleteStepResult(step.getId());
    }
    
    public List<Step> getAllStepResults(long templateId) {
    	String query = "SELECT * FROM " + StepTemplateEntry.TABLE_NAME + " INNER JOIN " + StepResultEntry.TABLE_NAME + " ON " + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry._ID + "=" + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry._ID + "=" + templateId;
    	Cursor cursor = mDb.rawQuery(query, null);
    	ArrayList<Step> steps = new ArrayList<Step>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
	        	
	        	long resultId = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry._ID));
	        	int status = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_STATUS));
	        	long duration = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_DURATION));
	        	long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_START_TIME));
	        	long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_END_TIME));
	        	int score = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SCORE));
	        	float selfAssessment = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT));
	        	
	        	
	        	Step step = new Step(templateId, title, description, photoUrl, optimalTime, resultId, status, duration, startTime, endTime, score, selfAssessment);
	        	steps.add(step);
        	}
        }
        return steps;
    }
    
    public Step getStepResult(long resultId) {
    	String query = "SELECT * FROM " + StepTemplateEntry.TABLE_NAME + " INNER JOIN " + StepResultEntry.TABLE_NAME + " ON " + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry._ID + "=" + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + StepResultEntry.TABLE_NAME + "." + StepResultEntry._ID + "=" + resultId;
    	Cursor cursor = mDb.rawQuery(query, null);
    	Step step = null;
        if (cursor != null && cursor.moveToFirst()) {
        	long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
        	
        	// long resultId = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry._ID));
        	int status = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_STATUS));
        	long duration = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_DURATION));
        	long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_START_TIME));
        	long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_END_TIME));
        	int score = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SCORE));
        	float selfAssessment = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT));
        	
        	
        	step = new Step(templateId, title, description, photoUrl, optimalTime, resultId, status, duration, startTime, endTime, score, selfAssessment);
        }
        return step;
    }
    
    
    
    
    public long createProcedureResult(long templateId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID, templateId);
        return mDb.insert(ProcedureResultEntry.TABLE_NAME, null, initialValues);
    }
    
    public long createProcedureResult(Procedure procedure) {
    	long procedureId = createProcedureResult(procedure.getTemplateId());
    	for (Step step : procedure.getSteps()) {
    		long stepId = createStepResult(step);
    		connectProcedureResultStepResult(procedureId, stepId);
    	}
    	return procedureId;
    }
    
    public boolean deleteProcedureResult(long resultId) {
        return mDb.delete(ProcedureResultEntry.TABLE_NAME, ProcedureResultEntry._ID + "=" + resultId, null) > 0;
    }
    
    public boolean deleteProcedureResult(Procedure procedure) {
        return deleteProcedureResult(procedure.getId());
    }
    
    public List<Procedure> getAllProcedureResults(long templateId) {
    	String query = "SELECT * FROM " + ProcedureTemplateEntry.TABLE_NAME + " INNER JOIN " + ProcedureResultEntry.TABLE_NAME + " ON " + ProcedureTemplateEntry.TABLE_NAME + "." + ProcedureTemplateEntry._ID + "=" + ProcedureResultEntry.TABLE_NAME + "." + ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + ProcedureTemplateEntry.TABLE_NAME + "." + ProcedureTemplateEntry._ID + "=" + templateId;
    	Cursor cursor = mDb.rawQuery(query, null);
    	ArrayList<Procedure> procedures = new ArrayList<Procedure>();
        if (cursor != null) {
        	while (cursor.moveToNext()) {
	        	long resultId = cursor.getLong(cursor.getColumnIndexOrThrow(ProcedureResultEntry._ID));
	        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
	        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
	        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
	        	Procedure procedure = new Procedure(templateId, title, description, photoUrl, resultId);
	        	procedure.setSteps(getProcedureResultSteps(resultId));
	        	
	        	procedures.add(procedure);
        	}
        }
        return procedures;
    }
    
    public Procedure getProcedureResult(long resultId) {
    	String query = "SELECT * FROM " + ProcedureTemplateEntry.TABLE_NAME + " INNER JOIN " + ProcedureResultEntry.TABLE_NAME + " ON " + ProcedureTemplateEntry.TABLE_NAME + "." + ProcedureTemplateEntry._ID + "=" + ProcedureResultEntry.TABLE_NAME + "." + ProcedureResultEntry.COLUMN_NAME_TEMPLATE_ID + " WHERE " + ProcedureResultEntry.TABLE_NAME + "." + ProcedureResultEntry._ID + "=" + resultId;
    	Cursor cursor = mDb.rawQuery(query, null);
    	Procedure procedure = null;
        if (cursor != null && cursor.moveToFirst()) {
        	long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(ProcedureTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	procedure = new Procedure(templateId, title, description, photoUrl, resultId);
        	procedure.setSteps(getProcedureResultSteps(resultId));
        }
        return procedure;
    }
    
    public List<Step> getProcedureResultSteps(long resultId) {
    	String query = "SELECT * FROM " + ProcedureResultStepResultConnection.TABLE_NAME + " INNER JOIN " + StepResultEntry.TABLE_NAME + " ON " + ProcedureResultStepResultConnection.TABLE_NAME + "." + ProcedureResultStepResultConnection.COLUMN_NAME_STEP_RESULT_ID + "=" + StepResultEntry.TABLE_NAME + "." + StepResultEntry._ID + " INNER JOIN " + StepTemplateEntry.TABLE_NAME + " ON " + StepResultEntry.TABLE_NAME + "." + StepResultEntry.COLUMN_NAME_TEMPLATE_ID + "=" + StepTemplateEntry.TABLE_NAME + "." + StepTemplateEntry._ID + " WHERE " + ProcedureResultStepResultConnection.TABLE_NAME + "." + ProcedureResultStepResultConnection.COLUMN_NAME_PROCEDURE_RESULT_ID + "=" + resultId;
    	ArrayList<Step> steps = new ArrayList<Step>();
    	Cursor cursor = mDb.rawQuery(query, null);
        if (cursor != null) {
        	while (cursor.moveToNext()) {
        		long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
            	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
            	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
            	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
            	long optimalTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_OPTIMAL_TIME));
            	
            	long stepResultId = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry._ID));
            	int status = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_STATUS));
            	long duration = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_DURATION));
            	long startTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_START_TIME));
            	long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_END_TIME));
            	int score = cursor.getInt(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SCORE));
            	float selfAssessment = cursor.getFloat(cursor.getColumnIndexOrThrow(StepResultEntry.COLUMN_NAME_SELF_ASSESSMENT));
            	
            	Step step = new Step(templateId, title, description, photoUrl, optimalTime, stepResultId, status, duration, startTime, endTime, score, selfAssessment);
            	steps.add(step);
        	}
        }
        return steps;
    }
    
    public long connectProcedureResultStepResult(long procedureId, long stepId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ProcedureResultStepResultConnection.COLUMN_NAME_PROCEDURE_RESULT_ID, procedureId);
        initialValues.put(ProcedureResultStepResultConnection.COLUMN_NAME_STEP_RESULT_ID, stepId);
        return mDb.insert(ProcedureResultStepResultConnection.TABLE_NAME, null, initialValues);
    }
}
