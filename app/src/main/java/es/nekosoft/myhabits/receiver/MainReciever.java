package es.nekosoft.myhabits.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MainReciever extends BroadcastReceiver {


    //---- Atributes ----//

    private String TAG = "MainReciever";
    Activity activity;


    //---- Constructor ----//

    public MainReciever(Activity activity){

        this.activity = activity;
    }

    public MainReciever() {

    }


    //---- Intent reception ----//

    @Override
    public void onReceive(Context context, Intent intent) {

        //Mostrar información del intent
        /*String action = intent.getAction();

        Toast.makeText(context, msjToast, Toast.LENGTH_LONG).show();*/
    }

}
