package es.nekosoft.ejercicio01.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Date;
import java.util.List;

import es.nekosoft.ejercicio01.dao.LogMHDAO;
import es.nekosoft.ejercicio01.model.LogMH;
import es.nekosoft.ejercicio01.utils.ConstGeofences;
import es.nekosoft.ejercicio01.utils.Constants;


public class GeofenceIntentService extends IntentService {


    private String TAG = "Geofences";
    protected static final String NAME_SERVICE = "GeofenceTransitionsIS";


    //---- Constructor ----//

    public GeofenceIntentService() {

        super(NAME_SERVICE);
    }

    public GeofenceIntentService(String name) {

        super(name);
    }


    //---- Methods ---//

    protected void onHandleIntent(Intent intent) {

        //Intent transformation
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        //Are there any errors?
        if (geofencingEvent.hasError()) {

            String errorMessage = getErrorString(geofencingEvent.getErrorCode());
            sendIntentToMain(Constants.GEO_TYPE_ERROR, errorMessage, null);
            return;
        }

        //What kind of transition do we have?
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            //Get geofences detected
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Log.d(TAG, "Se han detectado " +triggeringGeofences.size()+ " cercados");

            //Ver si está mi cercado
            String transition = null;
            String id = "";

            for (Geofence geofence : triggeringGeofences) {

                //Transition type
                transition = Constants.GEO_TRANS_UNKOWN;
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    transition = Constants.GEO_TRANS_ENTER;

                //Geofence Id
                id = geofence.getRequestId();

                //Save info
                insertInfoLog(id);
                //sendIntentToMain(Constants.GEO_TYPE, transition, id);
            }
        }
    }

    private String getErrorString(int errorCode) {

        String msj = Constants.GEO_ERROR_UNKNOWN;

        switch (errorCode){

            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                msj = Constants.GEO_ERROR_01;
                break;

            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                msj = Constants.GEO_ERROR_02;
                break;

            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                msj = Constants.GEO_ERROR_03;
                break;
        }

        return msj;
    }

    private void sendIntentToMain(String type, String transition, String id){

        Intent intent = new Intent(Constants.GEO_ACTION)
                .putExtra(Constants.GEO_TYPE, type)
                .putExtra(Constants.GEO_TYPE_TRANS, transition)
                .putExtra(Constants.GEO_ID, id);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void insertInfoLog(String idArea){

        //Insert info
        LogMH obj = new LogMH(LogMH.TYPE_PLACE, idArea, new Date());
        LogMHDAO dao = new LogMHDAO(getBaseContext());
        dao.insert(obj);
    }

}


















