package com.comp30022.helium.strawberry;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.comp30022.helium.strawberry.components.server.rest.components.PeachCookieStore;
import com.comp30022.helium.strawberry.entities.User;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StrawberryApplication extends Application {

    private RequestQueue requestQueue;
    private static StrawberryApplication myApplication;
    public static final String MY_PREFS = "my-prefs";

    public static final String MAC_TAG = "mac";
    public static final String GET_TAG = "getRequest";
    public static final String POST_TAG = "postRequest";
    public static final String PUT_TAG = "putRequest";
    public static final String DELETE_TAG = "deleteRequest";

    public static final String SELECTED_USER_TAG = "selectedUser";

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);

        // default values edit
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(MAC_TAG, findMacAddress());
        //editor.putString(SELECTED_USER_TAG, "59cf7b1e2f63f07468f2c77a");
        editor.apply();

        CookieStore cookieStore = new PeachCookieStore();
        CookieManager manager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }

    // Don't need to ensure that myApplication is not null because it
    // will have to be created, and then onCreate will happen.
    public static synchronized StrawberryApplication getInstance() {
        return myApplication;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    /**
     * Adding something to the request queue should allow us to make the requests
     * in order.
     *
     * @param tag     Either of GET, POST, PUT, DELETE.
     * @param request The request as a Request object.
     */
    public void addToRequestQueue(String tag, Request request) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /**
     * Cancel all requests with a certain tag, i.e. GET, POST, PUT, DELETE.
     *
     * @param tag Can be any of GET, POST, PUT, DELETE.
     */
    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public static String getMacAddress() {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String mac = pref.getString(MAC_TAG, "UNKNOWN");
        
        return mac;
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
        WifiManager manager = (WifiManager) getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        return address;
    }

    public static void setString(String name, String val) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, val);
        editor.apply();
    }

    public static void setStringSet(String name, Set<String> val) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(name, val);
        editor.apply();
    }

    public static String getString(String token) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        return pref.getString(token, null);
    }

    public static Set<String> getStringSet(String name) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        return pref.getStringSet(name, null);
    }

    public static List<User> getCachedFriends() {
        Set<String> friendSet = getStringSet("friends");
        if(friendSet == null)
            return Collections.emptyList();

        List<User> list = new ArrayList<>();

        for(String friend: friendSet) {
            list.add(User.toObject(friend));
        }

        return list;
    }

    public static void remove(String name) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.remove(name);
        editor.apply();
    }

    public static void setBoolean(String toggleFollowValKey, boolean checked) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(toggleFollowValKey, checked);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        SharedPreferences pref = getInstance().getApplicationContext().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }
}
