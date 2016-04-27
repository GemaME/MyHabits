package es.nekosoft.myhabits.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.util.Date;

import es.nekosoft.myhabits.dao.LogMHDAO;
import es.nekosoft.myhabits.model.LogMH;
import es.nekosoft.myhabits.utils.Constants;


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

        //Create log entry
        String coordStr = "Lat: " +loc.getLatitude()+ " - Long: " + loc.getLongitude();
        insertInfoLog(coordStr);

        //Send info to MainActivity
        sendInfo(loc);
    }

    private void insertInfoLog(String msj){

        //Insert info
        LogMH obj = new LogMH(LogMH.TYPE_LOCATION, msj, new Date());
        LogMHDAO dao = new LogMHDAO(getBaseContext());
        dao.insert(obj);
    }

    private void sendInfo(Location loc){

        Log.d("DEBUG", "Sending info location to main");
        Intent intent = new Intent(Constants.REC_RESPONSE);
        intent.putExtra(Constants.REC_TYPE, Constants.REC_TYPE_LOC);
        intent.putExtra(Constants.REC_LAT, (float) loc.getLatitude());
        intent.putExtra(Constants.REC_LONG, (float) loc.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}


















