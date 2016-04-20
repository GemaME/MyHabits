package es.nekosoft.ejercicio01.service;

import android.os.AsyncTask;

import java.net.URL;


public class WeatherREST extends AsyncTask<URL, String, String> {

    protected String doInBackground(URL... urls) {

        int count = urls.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {
            //totalSize += Downloader.downloadFile(urls[i]);
            //publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {

        //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result) {

        //showDialog("Downloaded " + result + " bytes");
    }
}