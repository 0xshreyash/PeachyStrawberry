package com.comp30022.helium.strawberry.helpers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by noxm on 17/09/17.
 * TODO: change this such that it downloads URL in any format, not just String
 */
public class FetchUrl extends AsyncTask<String, Void, String> {
    private ParserTask<String, ?, ?> parserTask;

    public FetchUrl(ParserTask<String, ?, ?> parserTask) {
        this.parserTask = parserTask;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());

        } finally {
            assert iStream != null;

            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }


    /**
     * Here doInBackground task will be implemented in background
     *
     * @param url
     * @return
     */
    @Override
    protected String doInBackground(String... url) {
        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]); // fecth url from web service
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }

        return data;
    }

    /**
     * After downloaded action
     *
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // Invokes the thread for parsing the JSON data
        if (parserTask != null)
            parserTask.execute(result);
    }
}
