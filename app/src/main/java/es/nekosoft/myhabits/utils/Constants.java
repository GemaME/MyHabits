package es.nekosoft.myhabits.utils;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;

import es.nekosoft.myhabits.R;


public class Constants {

    //---- Web ----//

    public final static String BROW_URL = "url";


    //---- SeekBars ----//

    public final static int SB_MAX = 100;

    public final static int SB_MAX_UBI = 60;
    public final static int SB_MIN_UBI = 10;
    public final static int SB_INI_UBI = 10;

    public final static int SB_MAX_WTH = 1800;
    public final static int SB_MIN_WTH = 30;
    public final static int SB_INI_WTH = 30;

    public final static String SB_SCD = "Every %s seconds";
    public final static String SB_MIN = "Every %s minutes";


    //---- Receiver ----//

    public final static String REC_RESPONSE = "es.nekosoft.RESPONSE";
    public final static String REC_TYPE = "type";
    public final static int REC_TYPE_LOC = 1;
    public final static int REC_TYPE_ACT = 2;
    public final static int REC_TYPE_GEO = 3;
    public final static String REC_LAT = "latitude";
    public final static String REC_LONG = "longitude";
    public final static String REC_ACTIVITY = "activity";
    public final static String REC_PERCENT = "percentage";


    //---- Limit Log ----//

    public static String LOG_LIMIT_SHOWN = "100";


    //---- Location ----//

    public static String LOC_ACTION = "es.nekosoft.GEOFENCING";
    public static int LOC_MODE = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    public static int LOC_ID_VIEW_MODE = R.id.check_balance_location;
    public static int LOC_INTERVAL = 10000;
    public static int LOC_FAST_INTERVAL = 1000;


    //---- Geofencing ----//

    public static String GEO_ACTION = "es.nekosoft.GEOFENCING";
    public static int GEO_EXP_MS = 60000;

    public static String GEO_TYPE = "type";
    public static String GEO_ID = "if";

    public static String GEO_TYPE_TRANS = "transition";
    public static String GEO_TYPE_ERROR = "error";

    public static String GEO_ERROR_01 = "The service isn't available";
    public static String GEO_ERROR_02 = "There are more than 100 geofences !!!";
    public static String GEO_ERROR_03 = "What a bunch of PendingIntents !!!";
    public static String GEO_ERROR_UNKNOWN = "Error desconegut";

    public static String GEO_TRANS_ENTER = "enter";
    public static String GEO_TRANS_EXIT = "exit";
    public static String GEO_TRANS_UNKOWN = "uknown";


    //---- Google Activities ----//

    public static String ACT_ACTION = "es.nekosoft.ACTIVITY";
    public static int ACT_DETECT_INTERV_MS = 20000;

    public static int getActivityString(int detectedActivityType) {

        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return R.string.act_vehicle;
            case DetectedActivity.ON_BICYCLE:
                return R.string.act_bicycle;
            case DetectedActivity.ON_FOOT:
                return R.string.act_foot;
            case DetectedActivity.RUNNING:
                return R.string.act_running;
            case DetectedActivity.STILL:
                return R.string.act_still;
            case DetectedActivity.TILTING:
                return R.string.act_tilting;
            case DetectedActivity.UNKNOWN:
                return R.string.act_unkown;
            case DetectedActivity.WALKING:
                return R.string.act_walking;
            default:
                return R.string.act_undefined;
        }
    }

}
