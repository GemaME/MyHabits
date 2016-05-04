package es.nekosoft.myhabits.activity.googleApis;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import es.nekosoft.myhabits.service.LocationIntentService;
import es.nekosoft.myhabits.utils.Constants;


public class LocationApi implements ResultCallback<Status> {

    Activity activity;
    GoogleApiClient mGoogleApiClient;

    public LocationApi(Activity activity, GoogleApiClient mGoogleApiClient) {

        this.activity = activity;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public void setupLocation(int mode) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = createLocationRequest(Constants.LOC_INTERVAL, Constants.LOC_FAST_INTERVAL, mode);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getLocationPendingIntent()).setResultCallback(this);
    }

    public void removeLocation() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationPendingIntent()).setResultCallback(this);
    }

    public LocationRequest createLocationRequest(int interval, int fastest, int priority) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastest);
        mLocationRequest.setPriority(priority);

        return mLocationRequest;
    }

    PendingIntent penIntentLoc;

    public PendingIntent getLocationPendingIntent() {

        if(penIntentLoc == null){

            Intent intent = new Intent(activity, LocationIntentService.class);
            penIntentLoc = PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return penIntentLoc;
    }

    public float[] getLocation() {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {

            float[] result = new float[2];
            result[0] = (float) lastLocation.getLatitude();
            result[1] = (float) lastLocation.getLongitude();
            return result;
        }
        return null;
    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
