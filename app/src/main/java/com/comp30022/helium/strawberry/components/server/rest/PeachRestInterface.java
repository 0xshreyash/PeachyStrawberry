package com.comp30022.helium.strawberry.components.server.rest;

import android.util.Log;

import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;

import java.util.Map;

/**
 * This wrapper should be used to construct rest calls and
 * then user the rest interface to send the calls using
 * Volley.
 */
public class PeachRestInterface {
    private static final String TAG = PeachRestInterface.class.getSimpleName();

    public static final String PROTOCOL = "https";
    public static final String HOSTNAME = "gnomie.me";
    public static final String VERSION = "v1";
    public static final String SERVER_URI = PROTOCOL + "://" + HOSTNAME + "/";

    public static void get(String peachPath, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? SERVER_URI : SERVER_URI + VERSION + peachPath;
        Log.d(TAG, "GET " + url);
        RestInterface.get(url, strawberryListener);
    }

    public static void post(String peachPath, Map<String, String> params, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? SERVER_URI : SERVER_URI + VERSION + peachPath;
        Log.d(TAG, "POST " + url);
        RestInterface.post(url, params, strawberryListener);
    }

    public static void put(String peachPath, Map<String, String> params, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? SERVER_URI : SERVER_URI + VERSION + peachPath;
        Log.d(TAG, "PUT " + url);
        RestInterface.put(url, params, strawberryListener);
    }

    public static void delete(String peachPath, Map<String, String> params, StrawberryListener strawberryListener) {
        String url = (peachPath == null) ? SERVER_URI : SERVER_URI + VERSION + peachPath;
        Log.d(TAG, "DELETE " + url);
        RestInterface.delete(url, params, strawberryListener);
    }
}
