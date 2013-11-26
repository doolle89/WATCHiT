package dusan.stefanovic.trainingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {
	
	public static final int STATUS_COMPLETED = 1;
	public static final int STATUS_SKIPPED = 2;
	public static final int STATUS_IN_PROGRESS = 3;
	public static final int STATUS_PENDING = 4;
	public static final int STATUS_PAUSED = 5;
	
	private long mTemplateId;
	private String mTitle;
	private String mDescription;
	private String mPhotoUrl;
	private int mStatus;
	private long mDuration;
	private long mStartTime;
	private long mEndTime;
	
	private int mScore;
	private long mOptimalTime;
	private float mSelfAssessment;
	
	private long mStartMeasuring;
	
	public Step(String title, String description) {
		mTitle = title;
		mDescription = description;
		mStatus = STATUS_PENDING;
		mDuration = 0;
		mScore = 0;
    }
	
	public Step(long templateId, String title, String description, String photoUrl, long optimalTime) {
		mTemplateId = templateId;
		mTitle = title;
		mDescription = description;
		mPhotoUrl = photoUrl;
		mOptimalTime = optimalTime;
		mStatus = STATUS_PENDING;
		mDuration = 0;
		mScore = 0;
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
		} else if (mStatus == STATUS_PAUSED) {
			mEndTime = System.currentTimeMillis();
			mStatus = STATUS_SKIPPED;
		} else if (mStatus == STATUS_PENDING) {
			mStartTime = System.currentTimeMillis();
			mEndTime = mStartTime;
			mStatus = STATUS_SKIPPED;
		}
	}
	
	public long getTemplateId() {
		return mTemplateId;
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
		return Math.round(mDuration * 0.000001);
	}

	public long getStartTime() {
		return mStartTime;
	}
	
	public long getEndTime() {
		return mEndTime;
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int score) {
		mScore = score;
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
	
	

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mTitle);
		out.writeString(mDescription);
		out.writeInt(mStatus);
		out.writeLong(mDuration);
		out.writeLong(mStartTime);
		out.writeLong(mEndTime);
		out.writeInt(mScore);
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
		mTitle = in.readString();
		mDescription = in.readString();
		mStatus = in.readInt();
		mDuration = in.readLong();
		mStartTime = in.readLong();
		mEndTime = in.readLong();
		mScore = in.readInt();
		mOptimalTime = in.readLong();
		mSelfAssessment = in.readFloat();
    }
}
