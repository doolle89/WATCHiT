package dusan.stefanovic.trainingapp.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Procedure implements Parcelable {
	
	public static final int STATE_STOPPED = 1;
	public static final int STATE_PAUSED = 2;
	public static final int STATE_RUNNING = 3;

	private long mTemplateId;
	private long mId;
	private String mTitle;
	private String mDescription;
	private String mPhotoUrl;
	private List<Step> mSteps;
	private int mState;
	private int mCurrentStepIndex;
	
	private Step lastStepInProgress;
	
	public Procedure(String title, String description, String photoUrl) {
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mSteps = new ArrayList<Step>();
		mState = STATE_STOPPED;
		mCurrentStepIndex = -1;
		lastStepInProgress = null;
	}
	
	public Procedure(long templateId, String title, String description, String photoUrl) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mSteps = new ArrayList<Step>();
		mState = STATE_STOPPED;
		mCurrentStepIndex = -1;
		lastStepInProgress = null;
	}
	
	public Procedure(long templateId, String title, String description, String photoUrl, long resultId) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mId = resultId;
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
	
	public long getTemplateId() {
		return mTemplateId;
	}

	public long getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}
	
	public String getDescription() {
		return mDescription;
	}

	public String getPhotoUrl() {
		return mPhotoUrl;
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
	
	public long getDuraton() {
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
		out.writeLong(mTemplateId);
		out.writeLong(mId);
		out.writeString(mTitle);
		out.writeString(mDescription);
		out.writeString(mPhotoUrl);
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
    	mTemplateId = in.readLong();
    	mId = in.readLong();
		mTitle = in.readString();
		mDescription = in.readString();
		mPhotoUrl = in.readString();
		mSteps = new ArrayList<Step>();
		in.readTypedList(mSteps, Step.CREATOR);
		mState = in.readInt();
		mCurrentStepIndex = in.readInt();
    }
}
