package es.nekosoft.ejercicio01.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.nekosoft.ejercicio01.dao.LogMHDAO;
import es.nekosoft.ejercicio01.model.LogMH;
import es.nekosoft.ejercicio01.utils.Constants;


public class ActivityIntentService extends IntentService {

    public ActivityIntentService() {

        super(Constants.ACT_ACTION);
    }

    public ActivityIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        //Get possible activities
        List<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        // Log each activity.
        for (DetectedActivity da: detectedActivities) {

            //Insert info: Activities + % posibility
            String msj = getBaseContext().getString(Constants.getActivityString(da.getType())) + " " + da.getConfidence() + "%";
            LogMH obj = new LogMH(LogMH.TYPE_TRANSPORT, msj, new Date());
            LogMHDAO dao = new LogMHDAO(getBaseContext());
            dao.insert(obj);
        }

    }
}
