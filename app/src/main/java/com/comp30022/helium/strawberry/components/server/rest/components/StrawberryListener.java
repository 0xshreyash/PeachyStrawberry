package com.comp30022.helium.strawberry.components.server.rest.components;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by noxm on 17/09/17.
 */

public class StrawberryListener {
    private static final String TAG = StrawberryListener.class.getSimpleName();
    private static final String UNKNOWN_ORIGIN = "UNKNOWN_ORIGIN";

    private Response.Listener<String> success = null;
    private Response.ErrorListener error = null;
    private String origin;

    public StrawberryListener() {
        this.origin = UNKNOWN_ORIGIN;
    }

    public StrawberryListener(String origin) {
        this.origin = origin;
    }

    public StrawberryListener(Response.Listener<String> success, Response.ErrorListener error) {
        this.origin = UNKNOWN_ORIGIN;
        this.success = success;
        this.error = error;
    }

    public StrawberryListener(String origin, Response.Listener<String> success, Response.ErrorListener error) {
        this.origin = origin;
        this.success = success;
        this.error = error;
    }

    public Response.Listener<String> getSuccessListener() {
        if (success == null)
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, origin + " " + response);
                }
            };

        return success;
    }

    public Response.ErrorListener getErrorListener() {
        if (error == null)
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error != null) {
                        try {
                            String msg = (error.getMessage() == null) ? error.networkResponse.statusCode + " Error" : error.getMessage();
                            String data = new String(error.networkResponse.data);

                            Log.e(TAG, msg);
                            Log.d(TAG, origin + " ERROR: " + msg + "\n" + data);

                        } catch (Exception e) {
                            Log.e(TAG, "Volley error: " + origin + " " + e.getMessage());
                        }
                    } else {
                        Log.e(TAG, "Unknown error: " + origin);
                    }
                }
            };

        return error;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
