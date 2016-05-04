package es.nekosoft.myhabits.activity.googleApis;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import es.nekosoft.myhabits.service.ActivityIntentService;
import es.nekosoft.myhabits.utils.Constants;


public class ActivityApi implements ResultCallback<Status> {

    Activity activity;
    GoogleApiClient mGoogleApiClient;

    public ActivityApi(Activity activity, GoogleApiClient mGoogleApiClient) {

        this.activity = activity;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    public void setupGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.ACT_DETECT_INTERV_MS,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    public void removeGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    public PendingIntent getGoogleActivitiesPendingIntent(){

        Intent intent = new Intent(activity, ActivityIntentService.class);
        return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
