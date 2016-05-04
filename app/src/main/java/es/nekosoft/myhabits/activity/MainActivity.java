package es.nekosoft.myhabits.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import es.nekosoft.myhabits.activity.googleApis.ActivityApi;
import es.nekosoft.myhabits.activity.googleApis.GeofencingApi;
import es.nekosoft.myhabits.activity.googleApis.LocationApi;
import es.nekosoft.myhabits.asynctask.AcuWeatherAsyncTask;
import es.nekosoft.myhabits.asynctask.UbiAsyncTask;
import es.nekosoft.myhabits.receiver.MainReciever;
import es.nekosoft.myhabits.utils.ConstGeofences;
import es.nekosoft.myhabits.utils.Constants;
import es.nekosoft.myhabits.R;
import es.nekosoft.myhabits.utils.Sensible;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    //---- Atributes ----//

    GoogleApiClient mGoogleApiClient;
    LocationApi locationApi;
    GeofencingApi geofencingApi;
    ActivityApi activityApi;

    public String lastWeather = null;
    public double lastTemp = -1;
    float lastLatitude = -1;
    float lastLongitude = -1;
    int lastActivity = -1;
    String lastPlace = null;

    int progUbi;
    int progWeather;

    SeekBar sbTimeWeatherUpdate;
    SeekBar sbUbiUpdate;
    TextView sbTvTimeWeatherUpdate;
    TextView sbTvUbiUpdate;
    TextView tvWeather;
    TextView tvTemperature;
    RadioGroup radioModeLocation;
    List<CheckBox> checkList;
    MainActivityEvent maEvent;

    boolean swWeather = true;
    boolean swLocation = true;
    boolean swActivity = true;
    boolean swPlaces = true;


    //---- Activity LifeCycle ---//

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setups
        getViews();
        getChecks();
        setupToolbar();
        setupSeekBarS();

        //Create GoogleApi client
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

        //Create intances to manage the Google API services
        locationApi = new LocationApi(this, mGoogleApiClient);
        geofencingApi = new GeofencingApi(this, mGoogleApiClient);
        activityApi = new ActivityApi(this, mGoogleApiClient);

        //Setup cards functionality
        if(swWeather){

            float[] location = locationApi.getLocation();
            lastLatitude = location[0];
            lastLongitude = location[1];

            if(lastLatitude != 0) reqOpenW();
            handlerOpenW.post(runnableOpenW);
        }
        if(swLocation)locationApi.setupLocation(Constants.LOC_MODE);
        if(swActivity) activityApi.setupGoogleActivities();
        if(swPlaces)geofencingApi.addGeofences(geofencingApi.getGeofenceList(ConstGeofences.geoList));

        //Init ubidots sending timer
        handlerUbidots.post(runnableUbidots);
    }

    @Override
    public void onConnectionSuspended(int i) {

        //Disable checkbuttons
        disableChecks();
        //Inform to user about the problem
        Toast.makeText(getBaseContext(), R.string.err_google_api_suspended, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        //Disable checkbuttons
        disableChecks();
        //Inform to user about the problem
        Toast.makeText(getBaseContext(), getString(R.string.err_google_api_connect), Toast.LENGTH_LONG).show();
    }


    //---- Setup UI components ----//

    private void getViews() {

        //This class respond to different views events
        maEvent = new MainActivityEvent(this);

        sbTimeWeatherUpdate = (SeekBar) findViewById(R.id.sb_time_weather_update);
        sbTvTimeWeatherUpdate = (TextView) findViewById(R.id.sb_tv_time_weather_update);
        sbUbiUpdate = (SeekBar) findViewById(R.id.sb_ubi_update);
        sbTvUbiUpdate = (TextView) findViewById(R.id.sb_tv_ubi_update);

        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvTemperature = (TextView) findViewById(R.id.tv_temperature);

        radioModeLocation = (RadioGroup) findViewById(R.id.radio_mode_location);
        radioModeLocation.check(Constants.LOC_ID_VIEW_MODE);
        radioModeLocation.setOnCheckedChangeListener(maEvent);
    }

    private void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void getChecks() {

        //Get checkbuttons
        CardView weather = (CardView) findViewById(R.id.card_time_weather);
        CardView location = (CardView) findViewById(R.id.card_location);
        CardView trasport = (CardView) findViewById(R.id.card_vehicle);
        CardView places = (CardView) findViewById(R.id.card_places);

        checkList = new ArrayList<CheckBox>();
        getChecksByParent((ViewGroup) weather.getChildAt(0));
        getChecksByParent((ViewGroup) location.getChildAt(0));
        getChecksByParent((ViewGroup) trasport.getChildAt(0));
        getChecksByParent((ViewGroup) places.getChildAt(0));

        //Who is responding to the events?
        for (int i = 0; i < checkList.size(); i++)
            checkList.get(i).setOnCheckedChangeListener(maEvent);
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

    //These are generic methods to manage sekbars related to time controller
    private void setupSeekBarS(){

        int progUbiSb = setupSeekBar(sbUbiUpdate, Constants.SB_MAX_UBI, Constants.SB_MIN_UBI, Constants.SB_INI_UBI);
        int proWthSb = setupSeekBar(sbTimeWeatherUpdate, Constants.SB_MAX_WTH, Constants.SB_MIN_WTH, Constants.SB_INI_WTH);

        progUbi = showProgreesSb(sbTvUbiUpdate, progUbiSb, Constants.SB_MAX_UBI, Constants.SB_MIN_UBI);
        progWeather = showProgreesSb(sbTvTimeWeatherUpdate, proWthSb, Constants.SB_MAX_WTH, Constants.SB_MIN_WTH);
    }

    public int setupSeekBar(SeekBar sb, int sbMax, int sbMin, int sbIni) {

        //Set progress
        int progSB = ((sbIni - sbMin) * 100 / (sbMax - sbMin));
        sb.setProgress(progSB);

        //Set event
        sb.setOnSeekBarChangeListener(maEvent);

        return progSB;
    }

    public int showProgreesSb(TextView tvSb, int progress, int sbMax, int sbMin){

        //Msg info
        int prog = (progress * (sbMax - sbMin) / 100) + sbMin;
        String str = prog + "";
        tvSb.setText(String.format(Constants.SB_SCD, str));

        return prog;
    }


    //---- Ubidots timer ----//

    Handler handlerUbidots = new Handler();
    Runnable runnableUbidots = new Runnable() {
        @Override
        public void run() {

            //Send info to ubidots
            if(lastWeather != null || lastTemp != -1 || lastLatitude != -1 || lastLongitude != -1 ||
                    lastActivity != -1 || lastPlace != null){

                UbiAsyncTask ubi = new UbiAsyncTask(lastWeather, lastTemp, lastLatitude, lastLongitude, lastActivity, lastPlace);
                String url = String.format(Sensible.URL_UBI, Sensible.KEY_UBI);
                ubi.execute(url);
            }

            //Set next call
            handlerUbidots.postDelayed(runnableUbidots, progUbi * 1000);
        }
    };


    //---- OpenWeather timer ----//

    private void reqOpenW() {

        AcuWeatherAsyncTask acuW = new AcuWeatherAsyncTask(MainActivity.this, tvWeather, tvTemperature);
        String url = String.format(Sensible.URL_OPEN_WTH, lastLatitude, lastLongitude, Sensible.KEY_OPEN_WTH);
        acuW.execute(url);
    }

    Handler handlerOpenW = new Handler();
    Runnable runnableOpenW= new Runnable() {
        @Override
        public void run() {

            //Before getting info, we have to check the switch
            if(swWeather) {
                reqOpenW();
                handlerOpenW.postDelayed(runnableOpenW, progWeather * 1000);
            }
        }
    };


    //---- Receiver from Google API Services ----//

    //Our BroadcastReciever gets all Google API services responds
    private void prepareBroadcast() {

        MainReciever receiver = new MainReciever(this);
        IntentFilter intentFilter = new IntentFilter(Constants.REC_RESPONSE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
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

        switch (id){

            case R.id.action_log:
                startActivity(new Intent(this, LogActivity.class));
                break;
            case R.id.action_weather:
                openBrowser(Sensible.URL_TABLE_WTH);
                break;
            case R.id.action_place:
                openBrowser(Sensible.URL_TABLE_PLA);
                break;
            case R.id.action_map:
                openBrowser(Sensible.URL_LOC);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openBrowser(String url){

        Intent i = new Intent(this, WebActivity.class);
        i.putExtra(Constants.BROW_URL, url);
        startActivity(i);
    }


    //---- Getter & Setters ----//

    public String getLastWeather() {
        return lastWeather;
    }

    public void setLastWeather(String lastWeather) {
        this.lastWeather = lastWeather;
    }

    public double getLastTemp() {
        return lastTemp;
    }

    public void setLastTemp(double lastTemp) {
        this.lastTemp = lastTemp;
    }

    public float getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(float lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public float getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(float lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public int getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(int lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getLastPlace() {
        return lastPlace;
    }

    public void setLastPlace(String lastPlace) {
        this.lastPlace = lastPlace;
    }

}
