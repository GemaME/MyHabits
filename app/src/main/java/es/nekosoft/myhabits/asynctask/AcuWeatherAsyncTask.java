package es.nekosoft.myhabits.asynctask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import es.nekosoft.myhabits.activity.MainActivity;


public class AcuWeatherAsyncTask extends AsyncTask<String, String, String> {

    Activity act;
    TextView tvWeather;
    TextView tvTemperature;

    public AcuWeatherAsyncTask(Activity act, TextView tvWeather, TextView tvTemperature){

        this.act = act;
        this.tvWeather = tvWeather;
        this.tvTemperature = tvTemperature;
        Log.d("AccuW", "creo info");
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
            conn.setRequestMethod("GET");
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

    @Override
    protected void onPostExecute(String bodyResponse) {
        super.onPostExecute(bodyResponse);

        MainActivity main = (MainActivity) act;
        JSONObject objJson;
        JSONArray arrJson;

        try {
            //Get weather
            objJson = new JSONObject(bodyResponse);
            arrJson = objJson.getJSONArray("weather");
            objJson = arrJson.getJSONObject(0);
            main.lastWeather = objJson.getString("main");

            //Get temperature
            objJson = new JSONObject(bodyResponse);
            objJson = objJson.getJSONObject("main");
            DecimalFormat df = new DecimalFormat("#.##");
            main.lastTemp = Double.parseDouble(df.format(objJson.getDouble("temp") - 273.15));

            //Show info in the activity
            tvWeather.setText(main.lastWeather);
            tvTemperature.setText(main.lastTemp + "ºC");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
