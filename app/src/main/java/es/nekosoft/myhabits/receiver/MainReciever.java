package es.nekosoft.myhabits.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.nekosoft.myhabits.activity.MainActivity;
import es.nekosoft.myhabits.utils.Constants;


public class MainReciever extends BroadcastReceiver {


    //---- Atributes ----//

    private String TAG = "MainReciever";
    MainActivity act;


    //---- Constructor ----//

    public MainReciever(MainActivity act){

        this.act = act;
    }


    //---- Intent reception ----//

    @Override
    public void onReceive(Context context, Intent intent) {

        int type = intent.getIntExtra(Constants.REC_TYPE, 0);

        //We recieve info about location
        if (type == Constants.REC_TYPE_LOC) {

            act.setLastLatitude(intent.getFloatExtra(Constants.REC_LAT, 0));
            act.setLastLongitude(intent.getFloatExtra(Constants.REC_LONG, 0));
            Log.d("DEBUG", "Update location info in MainActivity: " +act.getLastLatitude() + " - " +act.getLastLongitude());
        }

        //We recieve info about the activity
        else if (type == Constants.REC_TYPE_ACT) {

            act.setLastActivity(intent.getIntExtra(Constants.REC_ACTIVITY, 0));
            int percent = intent.getIntExtra(Constants.REC_PERCENT, 0);
            Log.d("DEBUG", "Update activity info in MainActivity: " +act.getLastActivity());
        }

        //We recieve info about the geofencing
        else if (type == Constants.REC_TYPE_GEO) {

            act.setLastPlace(intent.getStringExtra(Constants.GEO_ID));
            Log.d("DEBUG", "Update location info in MainActivity: " +act.getLastPlace());
        }
    }

}
