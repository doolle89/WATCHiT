package dusan.stefanovic.trainingapp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.jdom2.Element;

import android.content.Context;
import de.imc.mirror.sdk.ConnectionConfiguration;
import de.imc.mirror.sdk.DataObject;
import de.imc.mirror.sdk.DataObjectListener;
import de.imc.mirror.sdk.OfflineModeHandler.Mode;
import de.imc.mirror.sdk.SerializableDataObjectFilter;
import de.imc.mirror.sdk.android.ConnectionConfigurationBuilder;
import de.imc.mirror.sdk.android.ConnectionHandler;
import de.imc.mirror.sdk.android.DataHandler;
import de.imc.mirror.sdk.android.DataObjectBuilder;
import de.imc.mirror.sdk.android.PrivateSpace;
import de.imc.mirror.sdk.android.SpaceHandler;
import de.imc.mirror.sdk.exceptions.ConnectionStatusException;
import de.imc.mirror.sdk.exceptions.InvalidDataException;
import de.imc.mirror.sdk.exceptions.SpaceManagementException;
import de.imc.mirror.sdk.exceptions.UnknownEntityException;
import dusan.stefanovic.trainingapp.data.Procedure;
import dusan.stefanovic.trainingapp.data.Step;

public class MSFHelper {
	
	public  interface OnReceiveProcedureResultListener {
		public void onProcedureResultReceivedListener(Procedure procedure);
	}
	
	private Context mContext;
	
	private DataHandler mDataHandler;
	private PrivateSpace mPrivateSpace;
	
	private boolean mIsOnline;
	
	public MSFHelper(Context context) {
		mContext = context;
		
		mIsOnline = init();
	}

	protected boolean init() {
		String domain = "mirror-dev.de"; // If you don’t use the PTS, change this.
		String appId = "trainerapp"; // Adapt this so it fits the app you’re using.
		String sesionId = UUID.randomUUID().toString().replaceAll("-", "");
		String userName = "trainerapp";
		String userPass = "987654321";
		
		ConnectionConfigurationBuilder builder = new ConnectionConfigurationBuilder(domain, appId + "-" + sesionId);
		ConnectionConfiguration connectionConfig = builder.build();
		ConnectionHandler connectionHandler = new ConnectionHandler(userName, userPass, connectionConfig);
		
		try {
			connectionHandler.connect();
		} catch (ConnectionStatusException e) {
			// add proper exception handling
			return false;
		}
		
		SpaceHandler spaceHandler = new SpaceHandler(mContext, connectionHandler, "reflection_spaces_database");
		spaceHandler.setMode(Mode.ONLINE);
		
		mPrivateSpace = spaceHandler.getDefaultSpace();
		if (mPrivateSpace == null) {
			try {
				mPrivateSpace = spaceHandler.createDefaultSpace();
			} catch (SpaceManagementException e) {
				// failed to create space
				// add proper exception handling
				return false;
			} catch (ConnectionStatusException e) {
				// cannot create a space when offline
				// add proper exception handling
				return false;
			}
		} 
		
		mDataHandler = new DataHandler(connectionHandler, spaceHandler);
		mDataHandler.setMode(Mode.ONLINE);
		return true;
	}
	
	public boolean registerOnReceiveProcedureResultListener(final OnReceiveProcedureResultListener onReceiveProcedureResultListener) {
		boolean result = false;
			if (mIsOnline) {
			try {
				mDataHandler.registerSpace(mPrivateSpace.getId());
				mDataHandler.addDataObjectListener(new DataObjectListener() {
					
					@Override
					public void handleDataObject(DataObject dataObject, String spaceId) {
						Procedure procedure = unmarshalProcedureResult(dataObject);
						onReceiveProcedureResultListener.onProcedureResultReceivedListener(procedure);
					}
				});
				result = true;
			} catch (UnknownEntityException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public boolean publishProcedureResult(Procedure procedure) {
		boolean result = false;
		if (mIsOnline) {
			DataObject dataObject = marshalProcedureResult(procedure);
			// publish the data
			try {
				mDataHandler.publishDataObject(dataObject, mPrivateSpace.getId());
				result = true;
			} catch (UnknownEntityException e) {
				// space doesn't exist or is not accessible.
				// add proper exception handling
				e.printStackTrace();
			} catch (InvalidDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public List<Procedure> getAllProcedureResults() {
		ArrayList<Procedure> procedures = new ArrayList<Procedure>();
		if (mIsOnline) {
			List<DataObject> data = null;
			try {
				data = mDataHandler.queryDataObjectsBySpace(mPrivateSpace.getId(), Collections.<SerializableDataObjectFilter> emptySet());
			} catch (Exception e) {
				data = null;
			}
			if (data != null && data.size() > 0) {
				for (DataObject dataObject : data) {
					Procedure procedure = unmarshalProcedureResult(dataObject);
					procedures.add(procedure);
				}
			}
		}
		return procedures;
	}
	
	
	
	
	
	
	
	private static final String TEMPLATE_ID = "templateId";
	private static final String ID = "id";
	private static final String USER_ID = "userId";
	private static final String NOTES = "notes";
	private static final String STATUS = "status";
	private static final String DURATION = "duration";
	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";
	private static final String ERRORS = "errors";
	private static final String SELF_ASSESSMENT = "selfAssessment";
	
	protected DataObject marshalProcedureResult(Procedure procedure) {
		DataObjectBuilder procedureObjectBuilder = new DataObjectBuilder("procedure", "mirror:application:trainerapp");
		procedureObjectBuilder.addElement(ID, procedure.getId(), false);
		procedureObjectBuilder.addElement(TEMPLATE_ID, procedure.getTemplateId(), false);
		procedureObjectBuilder.addElement(USER_ID, procedure.getUserId(), false);
		procedureObjectBuilder.addElement(NOTES, procedure.getNotes(), false);
		for (Step step : procedure.getSteps()) {
			DataObjectBuilder stepObjectBuilder = new DataObjectBuilder("step", "mirror:application:trainerapp");
			stepObjectBuilder.addElement(ID, step.getId(), false);
			stepObjectBuilder.addElement(TEMPLATE_ID, step.getTemplateId(), false);
			stepObjectBuilder.addElement(STATUS, Integer.toString(step.getStatus()), false);
			stepObjectBuilder.addElement(DURATION, Long.toString(step.getDuration()), false);
			stepObjectBuilder.addElement(START_TIME, Long.toString(step.getStartTime()), false);
			stepObjectBuilder.addElement(END_TIME, Long.toString(step.getEndTime()), false);
			stepObjectBuilder.addElement(ERRORS, Integer.toString(step.getErrors()), false);
			stepObjectBuilder.addElement(SELF_ASSESSMENT, Float.toString(step.getSelfAssessment()), false);			
			procedureObjectBuilder.addElement(stepObjectBuilder.getRootElement());
		}
		return procedureObjectBuilder.build();
	}
	
	protected Procedure unmarshalProcedureResult(DataObject dataObject) {
		Element rootElement = dataObject.getElement();
		String id = rootElement.getChildText(ID, rootElement.getNamespace());
		String templateId = rootElement.getChildText(TEMPLATE_ID, rootElement.getNamespace());
		String userId = rootElement.getChildText(USER_ID, rootElement.getNamespace());
		String notes = rootElement.getChildText(NOTES, rootElement.getNamespace());
		Procedure procedure = new Procedure(templateId, null, null, "", id, userId, notes);
		
		ArrayList<Step> steps = new ArrayList<Step>();
		List<Element> elements = rootElement.getChildren("step", rootElement.getNamespace());
		for (Element element : elements) {
			id = element.getChildText(ID, element.getNamespace());
			templateId = element.getChildText(TEMPLATE_ID, element.getNamespace());
			int status = Integer.parseInt(element.getChildText(STATUS, element.getNamespace()));
			long duration = Long.parseLong(element.getChildText(DURATION, element.getNamespace()));
			long startTime = Long.parseLong(element.getChildText(START_TIME, element.getNamespace()));
			long endTime = Long.parseLong(element.getChildText(END_TIME, element.getNamespace()));
			int errors = Integer.parseInt(element.getChildText(ERRORS, element.getNamespace()));
			float selfAssessment = Float.parseFloat(element.getChildText(SELF_ASSESSMENT, element.getNamespace()));
			Step step = new Step(templateId, "", "", "", 0, id, status, duration, startTime, endTime, errors, selfAssessment);
			steps.add(step);
		}
		procedure.setSteps(steps);
		return procedure;
	}
}
