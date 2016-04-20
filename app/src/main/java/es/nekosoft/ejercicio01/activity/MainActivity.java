package es.nekosoft.ejercicio01.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.nekosoft.ejercicio01.dao.LogMHDAO;
import es.nekosoft.ejercicio01.model.LogMH;
import es.nekosoft.ejercicio01.service.ActivityIntentService;
import es.nekosoft.ejercicio01.service.LocationIntentService;
import es.nekosoft.ejercicio01.utils.ConstGeofences;
import es.nekosoft.ejercicio01.utils.Constants;
import es.nekosoft.ejercicio01.service.GeofenceIntentService;
import es.nekosoft.ejercicio01.model.GeofencesDTO;
import es.nekosoft.ejercicio01.receiver.MainReciever;
import es.nekosoft.ejercicio01.R;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    GoogleApiClient mGoogleApiClient;

    SeekBar sbTime;
    String sTimeMsg = "Every %s seconds";
    TextView tvCReloadTime;
    List<CheckBox> checkList;

    double sMaxPer = 100;
    double sMaxTime = 60;
    double sInitTime = 30;
    int progAct;

    Location lastLocation;


    //---- Activity Life Cycle ---//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getChecks();
        setupToolbar();

        setupCardReload();
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

        //Setups Google apis
        setupLocation();
        setupGeofencing();
        setupGoogleActivities();

        //Init Handler timer
        handler.post(runnableCode);
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

    private void disableChecks() {

        for (int i = 0; i < checkList.size(); i++) {

            checkList.get(i).setEnabled(false);
        }
    }

    private void getChecks() {

        CardView location = (CardView) findViewById(R.id.card_location);
        CardView trasport = (CardView) findViewById(R.id.card_vehicle);
        CardView places = (CardView) findViewById(R.id.card_places);

        checkList = new ArrayList<CheckBox>();
        getChecksByParent((ViewGroup) location.getChildAt(0));
        getChecksByParent((ViewGroup) trasport.getChildAt(0));
        getChecksByParent((ViewGroup) places.getChildAt(0));
    }

    private void getChecksByParent(ViewGroup parent) {

        View child = null;
        for (int i = 0; i < parent.getChildCount(); i++) {

            child = parent.getChildAt(i);
            if (child instanceof CheckBox)
                checkList.add((CheckBox) child);
        }
    }


    //---- Setup Componentes ----//

    private void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupCardWeather() {

    }

    private void setupCardReload() {

        //Get views
        sbTime = (SeekBar) findViewById(R.id.selector_time_updating);
        tvCReloadTime = (TextView) findViewById(R.id.time_updating);

        //Set progress
        progAct = (int) (sInitTime * sMaxTime / sMaxPer);
        sbTime.setProgress((int) progAct);
        String msg = String.format(sTimeMsg, "" + progAct);
        tvCReloadTime.setText(msg);

        //Seekbar event
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progAct = (int) ((progress + 1) * sMaxTime / sMaxPer);
                String msg = String.format(sTimeMsg, "" + progAct);
                tvCReloadTime.setText(msg);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //Handler for the timer
    Handler handler = new Handler();
    Runnable runnableCode = new Runnable() {
        @Override
        public void run() {

            /*LogMH obj = new LogMH(LogMH.TYPE_UPDATE, getString(R.string.msj_reload), new Date());
            LogMHDAO dao = new LogMHDAO(getBaseContext());
            dao.insert(obj);*/

            //Set next call
            handler.postDelayed(runnableCode, progAct * 1000);
        }
    };


    //---- Location ----//

    private void setupLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = createLocationRequest(Constants.LOC_INTERVAL, Constants.LOC_FAST_INTERVAL, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, getLocationPendingIntent()).setResultCallback(this);
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

        //Comprobar si se han concedido los permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        {

            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {

                this.lastLocation = lastLocation;
            }
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

    private void prepareBroadcast(){

        IntentFilter mStatusIntentFilter = new IntentFilter(Constants.GEO_ACTION);
        MainReciever mMainreciever = new MainReciever(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMainreciever,
                mStatusIntentFilter);
    }


    //---- Google activities ----//

    private void setupGoogleActivities(){

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.ACT_DETECT_INTERV_MS,
                getGoogleActivitiesPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getGoogleActivitiesPendingIntent(){

        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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


}
