package dusan.stefanovic.trainingapp.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Procedure implements Parcelable {
	
	public static final int STATE_STOPPED = 1;
	public static final int STATE_PAUSED = 2;
	public static final int STATE_RUNNING = 3;

	private String mTemplateId;
	private String mId;
	private String mUserId;
	private String mTitle;
	private String mDescription;
	private String mPhotoUrl;
	private String mNotes;
	private List<Step> mSteps;
	private int mState;
	
	private int mCurrentStepIndex;
	
	private Step lastStepInProgress;
	
	public Procedure() {
		mSteps = new ArrayList<Step>();
		mState = STATE_STOPPED;
		mCurrentStepIndex = -1;
		lastStepInProgress = null;
	}
	
	public Procedure(String title, String description, String photoUrl) {
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mSteps = new ArrayList<Step>();
		mState = STATE_STOPPED;
		mCurrentStepIndex = -1;
		lastStepInProgress = null;
	}
	
	public Procedure(String templateId, String title, String description, String photoUrl) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mSteps = new ArrayList<Step>();
		mState = STATE_STOPPED;
		mCurrentStepIndex = -1;
		lastStepInProgress = null;
	}
	
	public Procedure(String templateId, String title, String description, String photoUrl, String resultId, String userId, String notes) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mId = resultId;
		mUserId = userId;
		mNotes = notes;
		mSteps = new ArrayList<Step>();
		mState = STATE_STOPPED;
		mCurrentStepIndex = -1;
		lastStepInProgress = null;
	}
	
	public void start() {
		if(mState == STATE_STOPPED) {
			reset();
			startNextStep();
	    	mState = STATE_RUNNING;
		} else if(mState == STATE_PAUSED) {
			mSteps.get(mCurrentStepIndex).start();
	    	mState = STATE_RUNNING;
		}
	}

	public void pause() {
		if(mState == STATE_RUNNING) {
			mSteps.get(mCurrentStepIndex).pause();
	    	mState = STATE_PAUSED;
		}
	}
	
	public void stop() {
		if(mState != STATE_STOPPED) {
			while (mCurrentStepIndex < mSteps.size()) {
				mSteps.get(mCurrentStepIndex++).skip();
			}
	    	mState = STATE_STOPPED;
		}
	}
	
	public void reset() {
		mCurrentStepIndex = -1;
		for (Step step : mSteps) {
			step.pending();
		}
		lastStepInProgress = null;
	}
	
	public boolean completeCurrentStep() {
		if(mState == STATE_RUNNING) {
			mSteps.get(mCurrentStepIndex).complete();
			startNextStep();
			return true;
		}
		return false;
	}
	
	public boolean skipCurrentStep() {
		if(mState == STATE_RUNNING) {
			mSteps.get(mCurrentStepIndex).skip();
			startNextStep();
			return true;
		}
		return false;
	}
	
	public boolean addErrorToCurrentStep() {
		if(mState == STATE_RUNNING) {
			mSteps.get(mCurrentStepIndex).addError();
			return true;
		}
		return false;
	}
	
	public boolean completeStepAtIndex(int stepIndex) {
		if (mCurrentStepIndex > stepIndex) {
			return false;
		}
		if(mState == STATE_RUNNING) {
	    	while (mCurrentStepIndex < stepIndex && mCurrentStepIndex < mSteps.size()) {
	    		mSteps.get(mCurrentStepIndex++).skip();
	    	}
	    	if (mCurrentStepIndex < mSteps.size()) {
	    		if (mSteps.get(mCurrentStepIndex).getStatus() == Step.STATUS_PENDING) {
	    			mSteps.get(mCurrentStepIndex).completeInsteadOfStep(lastStepInProgress);
	    		} else {
	    			mSteps.get(mCurrentStepIndex).complete();
	    		}
	    		startNextStep();
			} else {
				stop();
			}
	    	return true;
		}
		return false;
	}
	
	public boolean skipStepAtIndex(int stepIndex) {
		if (mCurrentStepIndex > stepIndex) {
			return false;
		}
		if(mState == STATE_RUNNING) {
			while (mCurrentStepIndex < stepIndex && mCurrentStepIndex < mSteps.size()) {
	    		mSteps.get(mCurrentStepIndex++).skip();
	    	}
	    	if (mCurrentStepIndex < mSteps.size()) {
	    		mSteps.get(mCurrentStepIndex).skip();
	    		startNextStep();
			} else {
				stop();
			}
	    	return true;
		}
		return false;
	}
	
	private void startNextStep() {
		if (++mCurrentStepIndex < mSteps.size()) {
			mSteps.get(mCurrentStepIndex).start();
			lastStepInProgress = mSteps.get(mCurrentStepIndex);
		} else {
			stop();
		}
	}
	
	public boolean isStarted() {
		return mState != STATE_STOPPED;
	}
	
	public boolean isRunning() {
		return mState == STATE_RUNNING;
	}
	
	public boolean isPaused() {
		return mState == STATE_PAUSED;
	}
	
	public void addStep(Step step) {
		mSteps.add(step);
	}
	
	public String getTemplateId() {
		return mTemplateId;
	}
	
	public void setTemplateId(String templateId) {
		mTemplateId = templateId;
	}

	public String getId() {
		return mId;
	}
	
	public void setId(String id) {
		mId = id;
	}
	
	public String getUserId() {
		return mUserId;
	}
	
	public void setUserId(String userId) {
		mUserId = userId;
	}

	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public void setDescription(String description) {
		mDescription = description;
	}

	public String getPhotoUrl() {
		return mPhotoUrl;
	}
	
	public void setPhotoUrl(String photoUrl) {
		mPhotoUrl = photoUrl;
	}

	public String getNotes() {
		return mNotes;
	}

	public void setNotes(String notes) {
		mNotes = notes;
	}
	
	public float getScore() {
		float score = 0;
		for (Step step : mSteps) {
			score += step.getScore();
		}
		return score;
	}
	
	public float getMaxScore() {
		float score = 0;
		for (Step step : mSteps) {
			score += step.getMaxScore();
		}
		return score;
	}
	
	public long getOptimalTime() {
		long time = 0;
		for (Step step : mSteps) {
			time += step.getOptimalTime();
		}
		return time;
	}
	
	public int getErrors() {
		int errors = 0;
		for (Step step : mSteps) {
			errors += step.getErrors();
		}
		return errors;
	}

	public Step getCurrentStep() {
		Step currentStep = null;
		if (mCurrentStepIndex >= 0 && mCurrentStepIndex < mSteps.size()) {
			currentStep = mSteps.get(mCurrentStepIndex);
		}
		return currentStep;
	}
	
	public Step getStep(int i) {
		return mSteps.get(i);
	}
	
	public List<Step> getSteps() {
		return mSteps;
	}
	
	public void setSteps(List<Step> steps) {
		mSteps = steps;
	}

	public int getStepsNumber() {
		return mSteps.size();
	}
	
	public long getDuration() {
		long duration = 0;
		for (Step step : mSteps) {
			duration += step.getDuration();
		}
		return duration;
	}
	
	public long getStartTime() {
		long startTime = 0;
		if (!mSteps.isEmpty()) {
			startTime = mSteps.get(0).getStartTime();;
		}
		return startTime;
	}
	
	public long getEndTime() {
		long endTime = 0;
		if (!mSteps.isEmpty()) {
			endTime = mSteps.get(mSteps.size() - 1).getEndTime();
		}
		return endTime;
	}
	
	public int getProgress() {
		return Math.round(mCurrentStepIndex / (float) mSteps.size() * 100);
	}
	
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mTemplateId);
		out.writeString(mId);
		out.writeString(mUserId);
		out.writeString(mTitle);
		out.writeString(mDescription);
		out.writeString(mPhotoUrl);
		out.writeString(mNotes);
		out.writeTypedList(mSteps);
		out.writeInt(mState);
		out.writeInt(mCurrentStepIndex);
	}
	
	public static final Parcelable.Creator<Procedure> CREATOR = new Parcelable.Creator<Procedure>() {
        public Procedure createFromParcel(Parcel in) {
            return new Procedure(in);
        }

        public Procedure[] newArray(int size) {
            return new Procedure[size];
        }
    };
    
    private Procedure(Parcel in) {
    	mTemplateId = in.readString();
    	mId = in.readString();
    	mUserId = in.readString();
		mTitle = in.readString();
		mDescription = in.readString();
		mPhotoUrl = in.readString();
		mNotes = in.readString();
		mSteps = new ArrayList<Step>();
		in.readTypedList(mSteps, Step.CREATOR);
		mState = in.readInt();
		mCurrentStepIndex = in.readInt();
    }
    
    public static final Comparator<Procedure> SCORE_COMPARATOR = new Comparator<Procedure>() {

		@Override
		public int compare(Procedure lhs, Procedure rhs) {
			return Float.compare(rhs.getScore(), lhs.getScore());
		}
    	
    };
    
    public static final Comparator<Procedure> TIME_COMPARATOR = new Comparator<Procedure>() {

		@Override
		public int compare(Procedure lhs, Procedure rhs) {
			return (int) (lhs.getDuration() - rhs.getDuration());
		}
    	
    };
    
    public static final Comparator<Procedure> DATE_COMPARATOR = new Comparator<Procedure>() {

		@Override
		public int compare(Procedure lhs, Procedure rhs) {
			return (int) (rhs.getStartTime() - lhs.getStartTime());
		}
    	
    };
}
