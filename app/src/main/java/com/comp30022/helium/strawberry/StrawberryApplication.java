package com.comp30022.helium.strawberry;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.comp30022.helium.strawberry.services.RequestWrapper;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class StrawberryApplication extends Application {

    private RequestQueue requestQueue;
    private static StrawberryApplication myApplication;
    public static final String MY_PREFS = "my-prefs";
    private static final String INTERFACE_NAME = "wlan0";
    private static final String MAC_TAG = "mac";
    public static final String GET_TAG = "getRequest";
    public static final String POST_TAG = "postRequest";
    public static final String PUT_TAG = "putRequest";
    public static final String DELETE_TAG = "deleteRequest";

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(MAC_TAG, findMacAddress());

        // Required initialization logic here!
    }

    // Don't need to ensure that myApplication is not null because it
    // will have to be created, and then onCreate will happen.
    public static synchronized StrawberryApplication getInstance() {
        return myApplication;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue==null)
            requestQueue= Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    /**
     * Adding something to the request queue should allow us to make the requests
     * in order.
     * @param tag Either of GET, POST, PUT, DELETE.
     * @param request The request as a Request object.
     */
    public void addToRequestQueue(String tag, Request request) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /**
     * Cancel all requests with a certain tag, i.e. GET, POST, PUT, DELETE.
     * @param tag Can be any of GET, POST, PUT, DELETE.
     */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public static String getMacAddress() {
        SharedPreferences preferences = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        return preferences.getString(MAC_TAG, "");
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private static String findMacAddress() {
        try {
            // get all the interfaces
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            //find network interface wlan0
            for (NetworkInterface networkInterface : all) {
                if (!networkInterface.getName().equalsIgnoreCase(INTERFACE_NAME))
                    continue;
                //get the hardware address (MAC) of the interface
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //gets the last byte of b
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
