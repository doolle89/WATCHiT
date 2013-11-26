package dusan.stefanovic.trainingapp.database;

import java.util.ArrayList;
import java.util.List;

import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.trainingapp.database.WATCHiTProcedureTrainerDbAdapter.WATCHiTProcedureTrainerContract.ProcedureTemplateEntry;
import dusan.stefanovic.trainingapp.database.WATCHiTProcedureTrainerDbAdapter.WATCHiTProcedureTrainerContract.StepTemplateEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class WATCHiTProcedureTrainerDbAdapter {

	public static abstract class WATCHiTProcedureTrainerContract {
		
		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		
		public static final String[] SQL_CREATE_ENTRIES = {
			StepTemplateEntry.SQL_CREATE_ENTRIE,
			ProcedureTemplateEntry.SQL_CREATE_ENTRIE
		};
		
		public static final String[] SQL_DELETE_ENTRIES = {
			StepTemplateEntry.SQL_DELETE_ENTRIE,
			ProcedureTemplateEntry.SQL_DELETE_ENTRIE
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
        	    COLUMN_NAME_OPTIMAL_TIME + TEXT_TYPE +
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
    
    public WATCHiTProcedureTrainerDbAdapter(Context context) {
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
    	return createProcedureTemplate(procedure.getTitle(), procedure.getDescription(), procedure.getPhotoUrl());
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
	        	procedures.add(procedure);
        	}
        }
        return procedures;
    }
    
    public Procedure getProcedureTemplate(long templateId) {    	
    	String table = StepTemplateEntry.TABLE_NAME;
    	String[] columns = {StepTemplateEntry.COLUMN_NAME_TITLE, StepTemplateEntry.COLUMN_NAME_DESCRIPTION, StepTemplateEntry.COLUMN_NAME_PHOTO_URL};
    	String selection = StepTemplateEntry._ID + "=" + templateId;
    	String[] selectionArgs = null;
    	String groupBy = null;
    	String having = null;
    	String orderBy = null;
    	Cursor cursor = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    	Procedure procedure = null;
        if (cursor != null && cursor.moveToFirst()) {
        	// long templateId = cursor.getLong(cursor.getColumnIndexOrThrow(StepTemplateEntry._ID));
        	String title = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_TITLE));
        	String description = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_DESCRIPTION));
        	String photoUrl = cursor.getString(cursor.getColumnIndexOrThrow(StepTemplateEntry.COLUMN_NAME_PHOTO_URL));
        	procedure = new Procedure(templateId, title, description, photoUrl);
        }
        return procedure;
    }
}
