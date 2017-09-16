package com.comp30022.helium.strawberry.services;

import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by shreyashpatodia on 16/09/17.
 */

public class RestInterface {

    private static RestInterface myInterface;
    private String macAddress;
    private String accessToken;
    public static final String INTERFACE_NAME = "wlan0";
    public static final String MAC_TAG = "mac";

    private RestInterface() {
        this.macAddress = getMacAddress();
        Log.e("Mac address", this.macAddress);
        this.accessToken = null;
    }

    public void init(String accessToken) {
        this.accessToken = accessToken;
    }

    private static String getMacAddress() {
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

    public static RestInterface getInstance() {
        if(myInterface == null)
            myInterface = new RestInterface();
        return myInterface;
    }

    public String get(String endPointURL) {

        try {
            HttpGetRequest getRequest = new HttpGetRequest();

            String result = getRequest.execute(endPointURL, this.macAddress).get();

        }
        catch(InterruptedException e) {
            Log.e("REST interface error", "The background thread was interrupted");
            e.printStackTrace();
            return null;
        }
        catch(ExecutionException e) {
            Log.e("REST interface error", "There was a concurrent execution exception");
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
