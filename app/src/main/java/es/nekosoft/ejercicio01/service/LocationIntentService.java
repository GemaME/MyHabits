package es.nekosoft.ejercicio01.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationResult;

import java.util.Date;
import java.util.List;

import es.nekosoft.ejercicio01.dao.LogMHDAO;
import es.nekosoft.ejercicio01.model.LogMH;
import es.nekosoft.ejercicio01.utils.Constants;


public class LocationIntentService extends IntentService {


    //---- Constructor ----//

    public LocationIntentService() {

        super(Constants.LOC_ACTION);
    }

    public LocationIntentService(String name) {

        super(name);
    }


    //---- Methods ---//

    protected void onHandleIntent(Intent intent) {

        LocationResult result = LocationResult.extractResult(intent);
        if(result == null)
            return;

        Location loc = result.getLastLocation();
        String coordStr = "Lat: " +loc.getLatitude()+ " - Long: " + loc.getLongitude();
        insertInfoLog(coordStr);
    }

    private void insertInfoLog(String msj){

        //Insert info
        LogMH obj = new LogMH(LogMH.TYPE_LOCATION, msj, new Date());
        LogMHDAO dao = new LogMHDAO(getBaseContext());
        dao.insert(obj);
    }

}


















