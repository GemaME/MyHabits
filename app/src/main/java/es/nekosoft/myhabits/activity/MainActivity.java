package es.nekosoft.myhabits.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import es.nekosoft.myhabits.asynctask.AcuWeatherAsyncTask;
import es.nekosoft.myhabits.asynctask.UbiAsyncTask;
import es.nekosoft.myhabits.service.ActivityIntentService;
import es.nekosoft.myhabits.service.LocationIntentService;
import es.nekosoft.myhabits.utils.ConstGeofences;
import es.nekosoft.myhabits.utils.Constants;
import es.nekosoft.myhabits.service.GeofenceIntentService;
import es.nekosoft.myhabits.model.GeofencesDTO;
import es.nekosoft.myhabits.receiver.MainReciever;
import es.nekosoft.myhabits.R;
import es.nekosoft.myhabits.utils.Sensible;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>,
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    GoogleApiClient mGoogleApiClient;
    List<CheckBox> checkList;

    int progUbi;
    int progWeather;

    public String lastWeather = "";
    public double lastTemp = 0;
    float lastLatitude = 0;
    float lastLongitude = 0;
    int lastActivity = 0;
    String lastPlace = "";

    SeekBar sbTimeWeatherUpdate;
    TextView sbTvTimeWeatherUpdate;
    SeekBar sbUbiUpdate;
    TextView sbTvUbiUpdate;

    TextView tvWeather;
    TextView tvTemperature;


    //---- Activity Life Cycle ---//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setups
        getViews();
        getChecks();
        setupToolbar();
        setupSeekBarS();

        //Google APIs
        setupGoogleApi();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    //---- Google API ----//

    private void setupGoogleApi() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //Prepare recieve msgs from services
        prepareBroadcast();

        //Setups Google apis
        setupLocation();
        setupGeofencing();
        setupGoogleActivities();

        //Show initial weather
        getLocation();
        if(lastLatitude != 0)reqAcuW();

        //Init timers
        handlerAcuW.post(runnableAcuW);
        handlerUbidots.post(runnableUbidots);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        //Disable checkbuttons
        disableChecks();
        //Inform to user about the problem
        Toast.makeText(getBaseContext(), getString(R.string.err_google_api_connect), Toast.LENGTH_LONG).show();
    }


    //---- Setup Componentes ----//

    private void getViews() {

        sbTimeWeatherUpdate = (SeekBar) findViewById(R.id.sb_time_weather_update);
        sbTvTimeWeatherUpdate = (TextView) findViewById(R.id.sb_tv_time_weather_update);
        sbUbiUpdate = (SeekBar) findViewById(R.id.sb_ubi_update);
        sbTvUbiUpdate = (TextView) findViewById(R.id.sb_tv_ubi_update);

        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);
    }

    private void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupSeekBarS(){

        int progUbiSb = setupSeekBar(sbUbiUpdate, Constants.SB_MAX_WTH, Constants.SB_MIN_WTH, Constants.SB_INI_WTH);
        int proWthSb = setupSeekBar(sbTimeWeatherUpdate, Constants.SB_MAX_UBI, Constants.SB_MIN_UBI, Constants.SB_INI_UBI);

        progUbi = showProgreesSb(sbTvUbiUpdate, progUbiSb, Constants.SB_MAX_UBI, Constants.SB_MIN_UBI);
        progWeather = showProgreesSb(sbTvTimeWeatherUpdate, proWthSb, Constants.SB_MAX_WTH, Constants.SB_MIN_WTH);
    }

    private int setupSeekBar(SeekBar sb, int sbMax, int sbMin, int sbIni) {

        //Set progress
        int progSB = sbIni * 100 / (sbMax - sbMin);
        sb.setProgress(progSB);

        //Set event
        sb.setOnSeekBarChangeListener(this);

        return progSB;
    }

    private int showProgreesSb(TextView tvSb, int progress, int sbMax, int sbMin){

        //Msg info
        int progText = progress * (sbMax - sbMin) / 100;
        String str = (progText + sbMin) + "";
        tvSb.setText(String.format(Constants.SB_SCD, str));

        return progText;
    }


    //---- Ubidots ----//

    Handler handlerUbidots = new Handler();
    Runnable runnableUbidots = new Runnable() {
        @Override
        public void run() {

            //Get info
            UbiAsyncTask ubi = new UbiAsyncTask(lastWeather, lastTemp, lastLatitude, lastLongitude, lastActivity, lastPlace);
            String url = String.format(Sensible.URL_UBI, Sensible.KEY_UBI);
            ubi.execute(url);

            //Set next call
            handlerUbidots.postDelayed(runnableUbidots, progUbi * 1000);
        }
    };


    //---- AcuWeather ----//

    private void reqAcuW() {

        AcuWeatherAsyncTask acuW = new AcuWeatherAsyncTask(MainActivity.this, tvWeather, tvTemperature);
        String url = String.format(Sensible.URL_OPEN_WTH, lastLatitude, lastLongitude, Sensible.KEY_OPEN_WTH);
        acuW.execute(url);
    }

    Handler handlerAcuW = new Handler();
    Runnable runnableAcuW = new Runnable() {
        @Override
        public void run() {

            //Get AcuWeather info
            if (lastLatitude != 0) reqAcuW();
            //Set next call
            handlerAcuW.postDelayed(runnableAcuW, progWeather * 1000);
        }
    };


    //---- CheckBoxes ----//

    private void getChecks() {

        //Get checkbuttons
        CardView location = (CardView) findViewById(R.id.card_location);
        CardView trasport = (CardView) findViewById(R.id.card_vehicle);
        CardView places = (CardView) findViewById(R.id.card_places);

        checkList = new ArrayList<CheckBox>();
        getChecksByParent((ViewGroup) location.getChildAt(0));
        getChecksByParent((ViewGroup) trasport.getChildAt(0));
        getChecksByParent((ViewGroup) places.getChildAt(0));

        //Stablish event
        for (int i = 0; i < checkList.size(); i++)
            checkList.get(i).setOnCheckedChangeListener(this);
    }

    private void disableChecks() {

        for (int i = 0; i < checkList.size(); i++) {

            checkList.get(i).setEnabled(false);
        }
    }

    private void getChecksByParent(ViewGroup parent) {

        View child = null;
        for (int i = 0; i < parent.getChildCount(); i++) {

            child = parent.getChildAt(i);
            if (child instanceof CheckBox)
                checkList.add((CheckBox) child);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


    //---- Location ----//

    private void setupLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = createLocationRequest(Constants.LOC_INTERVAL, Constants.LOC_FAST_INTERVAL, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getLocationPendingIntent()).setResultCallback(this);
    }

    private void removeLocation() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationPendingIntent()).setResultCallback(this);
    }

    private LocationRequest createLocationRequest(int interval, int fastest, int priority) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastest);
        mLocationRequest.setPriority(priority);

        return mLocationRequest;
    }

    private PendingIntent getLocationPendingIntent() {

        Intent intent = new Intent(this, LocationIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {

            lastLatitude = (float) lastLocation.getLatitude();
            lastLongitude = (float) lastLocation.getLongitude();
        }
    }


    //---- Geofencing ----//

    private void setupGeofencing() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                getGeofencingRequest(),
                getGeofencePendingIntent()
        ).setResultCallback(this);

    }

    private void removeGeofencing(List<String> ids) {

        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                ids
        ).setResultCallback(this);

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    private GeofencingRequest getGeofencingRequest() {

        //Geofence list
        List<Geofence> geofenceList = geofenceList();

        //Setup monitor for geofencings
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private List<Geofence> geofenceList(){

        List<Geofence> list = new ArrayList<Geofence>();

        for (Map.Entry<String, GeofencesDTO> ele : ConstGeofences.mapGeoDTO.entrySet()) {

            list.add(new Geofence.Builder()

                    .setRequestId(ele.getValue().getId())
                    .setCircularRegion(
                            ele.getValue().getLatitude(),
                            ele.getValue().getLongitude(),
                            ele.getValue().getRadius()
                    )
                    .setExpirationDuration(Constants.GEO_EXP_MS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build());
        }

        return list;
    }

    private PendingIntent getGeofencePendingIntent() {

        //Create PendingIntent
        Intent intent = new Intent(this, GeofenceIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    //---- Google activities ----//

    private void setupGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.ACT_DETECT_INTERV_MS,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    private void removeGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGoogleActivitiesPendingIntent(){

        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    //---- Receiver ----//

    private void prepareBroadcast() {

        ActivityReceiver receiver = new ActivityReceiver();
        IntentFilter intentFilter = new IntentFilter(Constants.REC_RESPONSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    private class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int type = intent.getIntExtra(Constants.REC_TYPE, 0);

            //We recieve info about location
            if (type == Constants.REC_TYPE_LOC) {

                lastLatitude = intent.getFloatExtra(Constants.REC_LAT, 0);
                lastLongitude = intent.getFloatExtra(Constants.REC_LONG, 0);
                Log.d("DEBUG", "Update location info in MainActivity: " +lastLatitude + " - " +lastLongitude);
            }

            //We recieve info about the activity
            else if (type == Constants.REC_TYPE_ACT) {

                lastActivity = intent.getIntExtra(Constants.REC_ACTIVITY, 0);
                int percent = intent.getIntExtra(Constants.REC_PERCENT, 0);
                Log.d("DEBUG", "Update activity info in MainActivity: " +lastActivity);
            }

            //We recieve info about the geofencing
            else if (type == Constants.REC_TYPE_GEO) {

                lastPlace = intent.getStringExtra(Constants.GEO_ID);
                Log.d("DEBUG", "Update location info in MainActivity: " +lastPlace);
            }
        }
    }


    //---- Menu ---//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_log){
            startActivity(new Intent(this, LogActivity.class));
        }
        else if(id == R.id.action_internet){
            startActivity(new Intent(this, WebActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    //---- SeekBar ----//

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int id = seekBar.getId();

        if(id == R.id.sb_time_weather_update){

            progWeather = showProgreesSb(sbTvTimeWeatherUpdate, progress, Constants.SB_MAX_WTH, Constants.SB_MIN_WTH);
        }
        else if(id == R.id.sb_ubi_update){

            progUbi = showProgreesSb(sbTvUbiUpdate, progress, Constants.SB_MAX_UBI, Constants.SB_MIN_UBI);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
