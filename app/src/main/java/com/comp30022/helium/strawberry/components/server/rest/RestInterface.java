package com.comp30022.helium.strawberry.components.server.rest;

import com.android.volley.Request;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryRequest;

import java.util.Map;

import static com.comp30022.helium.strawberry.StrawberryApplication.GET_TAG;
import static com.comp30022.helium.strawberry.StrawberryApplication.POST_TAG;
import static com.comp30022.helium.strawberry.StrawberryApplication.PUT_TAG;
import static com.comp30022.helium.strawberry.StrawberryApplication.DELETE_TAG;

public class RestInterface {
    public static void get(String endPointURL, StrawberryListener strawberryListener) {
        StrawberryRequest stringRequest = new StrawberryRequest(Request.Method.GET, endPointURL,
                strawberryListener.getSuccessListener(), strawberryListener.getErrorListener());

        StrawberryApplication.getInstance().addToRequestQueue(GET_TAG, stringRequest);
    }

    public static void post(String endPointURL, Map<String, String> params, StrawberryListener strawberryListener) {
        StrawberryRequest stringRequest = new StrawberryRequest(Request.Method.POST, endPointURL, params,
                strawberryListener.getSuccessListener(), strawberryListener.getErrorListener());

        StrawberryApplication.getInstance().addToRequestQueue(POST_TAG, stringRequest);
    }

    public static void put(String endPointURL, Map<String, String> params, StrawberryListener strawberryListener) {
        StrawberryRequest stringRequest = new StrawberryRequest(Request.Method.PUT, endPointURL, params,
                strawberryListener.getSuccessListener(), strawberryListener.getErrorListener());

        StrawberryApplication.getInstance().addToRequestQueue(PUT_TAG, stringRequest);
    }

    public static void delete(String endPointURL, Map<String, String> params, StrawberryListener strawberryListener) {
        StrawberryRequest stringRequest = new StrawberryRequest(Request.Method.DELETE, endPointURL, params,
                strawberryListener.getSuccessListener(), strawberryListener.getErrorListener());

        StrawberryApplication.getInstance().addToRequestQueue(DELETE_TAG, stringRequest);
    }
}
