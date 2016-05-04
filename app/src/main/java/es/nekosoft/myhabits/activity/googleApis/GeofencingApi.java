package es.nekosoft.myhabits.activity.googleApis;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import es.nekosoft.myhabits.model.GeofencesDTO;
import es.nekosoft.myhabits.service.GeofenceIntentService;
import es.nekosoft.myhabits.utils.Constants;


public class GeofencingApi implements ResultCallback<Status> {


    Activity activity;
    GoogleApiClient mGoogleApiClient;

    public GeofencingApi(Activity activity, GoogleApiClient mGoogleApiClient) {

        this.activity = activity;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public void addGeofences(List<Geofence> list) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(list),
                getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    public void removeGeofences(List<String> ids) {

        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                ids
        ).setResultCallback(this);
    }

    public GeofencingRequest getGeofencingRequest(List<Geofence> list) {

        //Setup monitor for geofencings
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(list);
        return builder.build();
    }

    public List<Geofence> getGeofenceList(List<GeofencesDTO> geoDTOlist){

        //Create list geofencings
        List<Geofence> geoListlist = new ArrayList<Geofence>();
        for (GeofencesDTO ele : geoDTOlist) {

            geoListlist.add(new Geofence.Builder()

                    .setRequestId(ele.getId())
                    .setCircularRegion(
                            ele.getLatitude(),
                            ele.getLongitude(),
                            ele.getRadius()
                    )
                    .setExpirationDuration(Constants.GEO_EXP_MS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }

        return geoListlist;
    }

    public PendingIntent getGeofencePendingIntent() {

        //Create PendingIntent
        Intent intent = new Intent(activity, GeofenceIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {

        Log.d("Status msg", status.getStatusMessage() + "");
        Log.d("Status Succes", status.isSuccess() + "");
        Log.d("Status Interrupted", status.isInterrupted() + "");
        Log.d("Status Cancell", status.isCanceled() + "");
    }
}
