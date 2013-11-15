package dusan.stefanovic.trainingapp.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import dusan.stefanovic.trainingapp.data.Step;
import dusan.stefanovic.treningapp.R;

public class StepDialogFragment extends DialogFragment {
	
	TextView mTitle;
	ImageView mImage;
	TextView mDescription;
	Button mCloseButton;
	
	public static StepDialogFragment getInstance(Step step) {
		StepDialogFragment instance = new StepDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("step", step);
        instance.setArguments(bundle);
        return instance;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_step, container, false);
        
        mTitle = (TextView) rootView.findViewById(R.id.step_title);
        mImage = (ImageView) rootView.findViewById(R.id.step_image);
        mDescription = (TextView) rootView.findViewById(R.id.step_description);
        mCloseButton = (Button) rootView.findViewById(R.id.step_close_button);
        mCloseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				dismiss();
			}
        	
        });
        
        Step step = getArguments().getParcelable("step");
        if (step != null) {
        	mTitle.setText(step.getTitle());
        	//mImage.setImageResource(0);
        	mDescription.setText(step.getDescription());
        } else {
        	dismiss();
        }
        
        return rootView;
	}

}
