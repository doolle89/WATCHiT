package dusan.stefanovic.trainingapp.data;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Procedure implements Parcelable {

	String mTitle;
	List<Step> mSteps;
	
	public Procedure(String title) {
		mTitle = title;
		mSteps = new ArrayList<Step>();
	}
	
	public void addStep(Step step) {
		mSteps.add(step);
	}
	
	public Step getStep(int i) {
		return mSteps.get(i);
	}
	
	public int getStepsNumber() {
		return mSteps.size();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mTitle);
		out.writeTypedList(mSteps);
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
		mTitle = in.readString();
		mSteps = new ArrayList<Step>();
		in.readTypedList(mSteps, Step.CREATOR);
    }
}
