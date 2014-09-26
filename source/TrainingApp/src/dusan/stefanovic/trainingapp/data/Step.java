package dusan.stefanovic.trainingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {
	
	public static final float ERROR_FACTOR = 0.1f;
	public static final long MAX_SCORE = 900000; 
	
	public static final int STATUS_COMPLETED = 1;
	public static final int STATUS_SKIPPED = 2;
	public static final int STATUS_IN_PROGRESS = 3;
	public static final int STATUS_PENDING = 4;
	public static final int STATUS_PAUSED = 5;
	
	private String mTemplateId;
	private String mId;
	private String mTitle;
	private String mDescription;
	private String mPhotoUrl;
	private int mStatus;
	private long mDuration;
	private long mStartTime;
	private long mEndTime;
	
	private int mErrors;
	private long mOptimalTime;
	private float mSelfAssessment;
	
	private long mStartMeasuring;
	
	public Step(String title, String description, String photoUrl) {
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mStatus = STATUS_PENDING;
		mDuration = 0;
		mErrors = 0;
    }
	
	public Step(String title, String description, String photoUrl, long optimalTime) {
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mOptimalTime = optimalTime;
		mStatus = STATUS_PENDING;
		mDuration = 0;
		mErrors = 0;
    }
	
	public Step(String templateId, String title, String description, String photoUrl, long optimalTime) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mOptimalTime = optimalTime;
		mStatus = STATUS_PENDING;
		mDuration = 0;
		mErrors = 0;
    }
	
	public Step(String templateId, String title, String description, String photoUrl, long optimalTime, String resultId, int status, long duration, long startTime, long endTime, int errors, float selfAssessment) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mOptimalTime = optimalTime;
		mId = resultId;
		mStatus = status;
		mDuration = duration;
		mStartTime = startTime;
		mEndTime = endTime;
		mErrors = errors;
		mSelfAssessment = selfAssessment;
    }
	
	public void pending() {
		mDuration = 0;
		mStartTime = 0;
		mEndTime = 0;
		mStatus = STATUS_PENDING;
	}
	
	public void start() {
		if (mStatus == STATUS_PENDING) {
			mStartMeasuring = System.nanoTime();
	    	mStartTime = System.currentTimeMillis();
			mStatus = STATUS_IN_PROGRESS;
		} else if (mStatus == STATUS_PAUSED) {
			mStartMeasuring = System.nanoTime();
	    	mStatus = STATUS_IN_PROGRESS;
		}
	}
	
	public void pause() {
		if (mStatus == STATUS_IN_PROGRESS) {
			mDuration += System.nanoTime() - mStartMeasuring;
			mStatus = STATUS_PAUSED;
		}
	}
	
	public void complete() {
		if (mStatus == STATUS_IN_PROGRESS) {
			mDuration += System.nanoTime() - mStartMeasuring;
			mEndTime = System.currentTimeMillis();
			mStatus = STATUS_COMPLETED;
		} 
	}
	
	public void completeInsteadOfStep(Step step) {
		if (mStatus == STATUS_PENDING) {
			mEndTime = System.currentTimeMillis();
			mDuration = step.mDuration;
			mStartTime = step.mStartTime;
			step.mEndTime = step.mStartTime;
			step.mDuration = 0;
			mStatus = STATUS_COMPLETED;
		}
	}
	
	public void skip() {
		if (mStatus == STATUS_IN_PROGRESS) {
			mDuration += System.nanoTime() - mStartMeasuring;
			mEndTime = System.currentTimeMillis();
			mStatus = STATUS_SKIPPED;
		} else if (mStatus == STATUS_PAUSED) { //proveriti ovo
			mEndTime = System.currentTimeMillis();
			mStatus = STATUS_SKIPPED;
		} else if (mStatus == STATUS_PENDING) {
			mStartTime = System.currentTimeMillis();
			mEndTime = mStartTime;
			mStatus = STATUS_SKIPPED;
		}
	}
	
	public void addError() {
		if (mStatus == STATUS_IN_PROGRESS) {
			mErrors++;
		}
	}
	
	public String getTemplateId() {
		return mTemplateId;
	}
	
	public void setTemplateId(String templateId) {
		mTemplateId = templateId;;
	}

	public String getId() {
		return mId;
	}
	
	public void setId(String id) {
		mId = id;
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

	public int getStatus() {
		return mStatus;
	}

	public long getDuration() {
		return Math.round(mDuration * 1e-6);
	}

	public long getDurationNano() {
		return mDuration;
	}
	
	public long getStartTime() {
		return mStartTime;
	}
	
	public long getEndTime() {
		return mEndTime;
	}

	public int getErrors() {
		return mErrors;
	}

	public void setErrors(int errors) {
		mErrors = errors;
	}

	public float getScore() {
		float result = 0;
		if (mStatus == STATUS_COMPLETED) {
			if (mOptimalTime > 0) {
				long deltaTime = Math.abs(mOptimalTime - getDuration());
				long errorTime = Math.round(mOptimalTime * ERROR_FACTOR * mErrors);
				result = mOptimalTime - deltaTime - errorTime;
			} else {
				long maxScore = MAX_SCORE;
				long errorTime = Math.round(maxScore * ERROR_FACTOR * mErrors);
				result = maxScore - getDuration() - errorTime;
			}
		} else if (mStatus == STATUS_SKIPPED) {
			long errorTime = Math.round(getDuration() * ERROR_FACTOR * mErrors);
			result = 0 - getDuration() - errorTime;
		}
		return result * 1e-3f;
	}

	public long getOptimalTime() {
		return mOptimalTime;
	}

	public void setOptimalTime(long optimalTime) {
		mOptimalTime = optimalTime;
	}

	public float getSelfAssessment() {
		return mSelfAssessment;
	}

	public void setSelfAssessment(float selfAssessment) {
		mSelfAssessment = selfAssessment;
	}
	
	public float getMaxScore() {
		float result = mOptimalTime;
		if (result == 0) {
			result = MAX_SCORE;
		}
		return result  * 1e-3f;
	}
	
	

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mTemplateId);
		out.writeString(mId);
		out.writeString(mTitle);
		out.writeString(mDescription);
		out.writeString(mPhotoUrl);
		out.writeInt(mStatus);
		out.writeLong(mDuration);
		out.writeLong(mStartTime);
		out.writeLong(mEndTime);
		out.writeInt(mErrors);
		out.writeLong(mOptimalTime);
		out.writeFloat(mSelfAssessment);
	}

	public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
    
    private Step(Parcel in) {
    	mTemplateId = in.readString();
    	mId = in.readString();
		mTitle = in.readString();
		mDescription = in.readString();
		mPhotoUrl = in.readString();
		mStatus = in.readInt();
		mDuration = in.readLong();
		mStartTime = in.readLong();
		mEndTime = in.readLong();
		mErrors = in.readInt();
		mOptimalTime = in.readLong();
		mSelfAssessment = in.readFloat();
    }
}
