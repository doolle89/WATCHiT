package dusan.stefanovic.trainingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {
	
	public static final int STATUS_COMPLETED = 1;
	public static final int STATUS_SKIPPED = 2;
	public static final int STATUS_IN_PROGRESS = 3;
	public static final int STATUS_PENDING = 4;
	
	public String mTitle;
	public String mDescription;
	
	public int mStatus;
	public long mTime;
	public int mScore;
	
	public Step(String title, String description) {
		mTitle = title;
		mDescription = description;
		mStatus = STATUS_PENDING;
		mTime = 0;
		mScore = 0;
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
		out.writeLong(mTime);
		out.writeInt(mScore);
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
		mTime = in.readLong();
		mScore = in.readInt();
    }
}
