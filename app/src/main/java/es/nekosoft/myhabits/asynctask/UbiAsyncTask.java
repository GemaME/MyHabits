package es.nekosoft.myhabits.asynctask;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import es.nekosoft.myhabits.utils.Constants;
import es.nekosoft.myhabits.utils.Sensible;


public class UbiAsyncTask extends AsyncTask<String, String, String> {

    String lastWeather = "";
    double lastTemp = 0;
    float lastLat = 0;
    float lastLng = 0;
    int lastActivity = 0;
    String lastPlace = "";


    public UbiAsyncTask(String lastWeather, double lastTemp, float lastLat, float lastLng,
                        int lastActivity, String lastPlace){

        this.lastWeather        = lastWeather;
        this.lastTemp           = lastTemp;
        this.lastLat            = lastLat;
        this.lastLng            = lastLng;
        this.lastActivity       = lastActivity;
        this.lastPlace          = lastPlace;
    }

    @Override
    protected String doInBackground(String... params) {

        URL url = null;
        InputStream is = null;
        int statusCode = 0;
        String bodyResponse = null;

        try {

            //Making request
            url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");

            OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
            wr.write(prepareBodyJson());
            wr.close();
            conn.connect();

            //The response
            statusCode = conn.getResponseCode();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            bodyResponse = br.readLine();
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.d("HTTP", "The response is: " + statusCode + " - " + bodyResponse);
        return bodyResponse;
    }

    private String prepareBodyJson(){

        String result = null;

        try {

            //Create body
            JSONArray finalBody = new JSONArray();

            //Var weather
            if(lastWeather != null){

                JSONObject weaJson = new JSONObject();
                weaJson.put("variable", Sensible.UBI_WTH);
                JSONObject weaCon = new JSONObject();
                weaCon.put("wth", lastWeather);
                weaCon.put("tmp", lastTemp);
                weaJson.put("context", weaCon);
                weaJson.put("value", lastTemp);
                finalBody.put(weaJson);
            }

            //Var location
            if(lastLat != -1) {

                JSONObject locJson = new JSONObject();
                locJson.put("variable", Sensible.UBI_LOC);
                JSONObject locCon = new JSONObject();
                locCon.put("lat", lastLat);
                locCon.put("lng", lastLng);
                locJson.put("context", locCon);
                locJson.put("value", lastLat + lastLng);
                finalBody.put(locJson);
            }

            //Var place
            if(lastActivity != -1 || lastPlace != null) {

                JSONObject plaJson = new JSONObject();
                plaJson.put("variable", Sensible.UBI_PLA);
                JSONObject plaCon = new JSONObject();
                if (lastActivity != -1)
                    plaCon.put("act", lastActivity);
                if (lastPlace != null)
                    plaCon.put("place", lastPlace);
                plaJson.put("context", plaCon);
                plaJson.put("value", 777);
                finalBody.put(plaJson);
            }

            //Final str
            result = finalBody.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("HTTP", "Sending: " + result);
        return result;
    }
}
