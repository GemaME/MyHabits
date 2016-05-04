package es.nekosoft.myhabits.activity;

import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.google.android.gms.location.LocationRequest;

import java.util.Date;

import es.nekosoft.myhabits.R;
import es.nekosoft.myhabits.dao.LogMHDAO;
import es.nekosoft.myhabits.model.LogMH;
import es.nekosoft.myhabits.utils.ConstGeofences;
import es.nekosoft.myhabits.utils.Constants;


public class MainActivityEvent implements CompoundButton.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {

    MainActivity mainActivity;

    public MainActivityEvent(MainActivity mainActivity){

        this.mainActivity = mainActivity;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int id = buttonView.getId();

        //Weather
        if(id == R.id.check_time_weather && isChecked){

            mainActivity.swWeather = true;
            mainActivity.handlerOpenW.post(mainActivity.runnableOpenW);
        }
        else if(id == R.id.check_time_weather && !isChecked){

            mainActivity.swWeather = false;
            mainActivity.lastTemp = -1;
            mainActivity.lastWeather = null;
        }

        //Location
        else if(id == R.id.check_location && isChecked){

            mainActivity.swLocation = true;
            mainActivity.locationApi.setupLocation(Constants.LOC_MODE);
        }
        else if(id == R.id.check_location && !isChecked){

            mainActivity.swLocation = false;
            mainActivity.lastLatitude = -1;
            mainActivity.lastLongitude = -1;
            mainActivity.locationApi.removeLocation();
        }

        //Activity
        else if(id == R.id.check_act && isChecked){

            mainActivity.swLocation = true;
            mainActivity.activityApi.setupGoogleActivities();
        }
        else if(id == R.id.check_act && !isChecked){

            mainActivity.swActivity = false;
            mainActivity.lastActivity = -1;
            mainActivity.activityApi.removeGoogleActivities();
        }

        //Places
        else if(id == R.id.check_places && isChecked){

            mainActivity.swPlaces = true;
            mainActivity.geofencingApi.addGeofences(mainActivity.geofencingApi.getGeofenceList(ConstGeofences.geoList));
        }
        else if(id == R.id.check_places && !isChecked){

            mainActivity.swPlaces = false;
            mainActivity.lastPlace = null;
            mainActivity.geofencingApi.removeGeofences(ConstGeofences.removeList);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int id = seekBar.getId();

        if(id == R.id.sb_time_weather_update){

            mainActivity.progWeather = mainActivity.showProgreesSb(mainActivity.sbTvTimeWeatherUpdate, progress, Constants.SB_MAX_WTH, Constants.SB_MIN_WTH);
        }
        else if(id == R.id.sb_ubi_update){

            mainActivity.progUbi = mainActivity.showProgreesSb(mainActivity.sbTvUbiUpdate, progress, Constants.SB_MAX_UBI, Constants.SB_MIN_UBI);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        //Stop location service
        mainActivity.locationApi.removeLocation();

        //Change accuracy and energy mode
        String msj = null;
        switch (checkedId){

            case R.id.check_balance_location:
                mainActivity.locationApi.setupLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                msj = mainActivity.getString(R.string.balance_prio_location);
                break;
            case R.id.check_accu_location:
                mainActivity.locationApi.setupLocation(LocationRequest.PRIORITY_HIGH_ACCURACY);
                msj = mainActivity.getString(R.string.accu_prio_location);
                break;
            case R.id.check_low_location:
                mainActivity.locationApi.setupLocation(LocationRequest.PRIORITY_LOW_POWER);
                msj = mainActivity.getString(R.string.low_prio_location);
                break;
            case R.id.check_nop_location:
                mainActivity.locationApi.setupLocation(LocationRequest.PRIORITY_NO_POWER);
                msj = mainActivity.getString(R.string.nop_prio_location);
                break;
        }

        //Inform in the log about the change
        LogMH obj = new LogMH(LogMH.TYPE_LOCATION, msj, new Date());
        LogMHDAO dao = new LogMHDAO(mainActivity);
        dao.insert(obj);
    }
}
