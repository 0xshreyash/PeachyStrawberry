package com.comp30022.helium.strawberry.components.server.rest.components;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by noxm on 17/09/17.
 */

public class StrawberryListener {
    private static final String TAG = StrawberryListener.class.getSimpleName();

    private Response.Listener<String> success = null;
    private Response.ErrorListener error = null;

    public StrawberryListener() {

    }

    public StrawberryListener(Response.Listener<String> success, Response.ErrorListener error) {
        this.success = success;
        this.error = error;
    }

    public Response.Listener<String> getSuccessListener() {
        if (success == null)
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
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
                            Log.d(TAG, 1 + " ERROR: " + msg + "\n" + data);
                        } catch (Exception e) {
                            Log.e(TAG, "Volley error");
                        }
                    } else {
                        Log.e(TAG, "Unknown error");
                    }
                }
            };

        return error;
    }
}
