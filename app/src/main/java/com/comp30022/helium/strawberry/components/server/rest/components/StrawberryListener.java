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
        if(success == null)
            return new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                }
            };

        return success;
    }

    public Response.ErrorListener getErrorListener() {
        if(error==null)
            return new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "Unknown error";
                    if(error.getMessage() != null)
                        errorMessage = error.getMessage();

                    Log.e(TAG, errorMessage);
                    error.printStackTrace();
                }
            };

        return error;
    }
}
