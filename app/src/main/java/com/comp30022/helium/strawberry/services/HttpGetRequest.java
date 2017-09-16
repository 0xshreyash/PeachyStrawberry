package com.comp30022.helium.strawberry.services;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Old HttpConnection style requests sent using this. Volley get requests also
 * made, to check which one is faster.
 */
public class HttpGetRequest extends AsyncTask<String, Void, String> {

    private static final String REQUEST_METHOD = "GET";

    // TODO: Make these numbers sensible (/10?)
    public static final int READ_TIMEOUT = 150000;
    public static final int CONNECTION_TIMEOUT = 150000;

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = strings[0];
        String macAddress = strings[1];
        String result = null;
        try {
            URL myUrl = new URL(stringUrl);
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();
            connection.setRequestProperty(RestInterface.MAC_TAG, macAddress);
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            // Connect to the URL after setting everything
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            String inputLine;
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();

        }
        catch(MalformedURLException e) {
            // The error messages are not meant to be complete, just indicative.
            Log.e("REST interface error", "The URL provided was malformed");
            e.printStackTrace();
        }
        catch(IOException e) {
            // The error messages are not meant to be complete, just indicative.
            Log.d("REST interface error", "The connection could not be opened");
            e.printStackTrace();
        }

        return result;
    }
}

