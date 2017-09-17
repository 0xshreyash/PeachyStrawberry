package com.comp30022.helium.strawberry.components.server.rest.components;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.comp30022.helium.strawberry.StrawberryApplication;

import java.util.HashMap;
import java.util.Map;

import static com.comp30022.helium.strawberry.StrawberryApplication.MAC_TAG;

/**
 * Created by noxm on 17/09/17.
 */

public class StrawberryRequest extends StringRequest {
    private Map<String, String> params = null;

    public StrawberryRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public StrawberryRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public StrawberryRequest(int method, String url, Map<String, String> params, Response.Listener<String> successListener, Response.ErrorListener errorListener) {
        super(method, url, successListener, errorListener);
        this.params = params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = new HashMap<>();
        header.put(MAC_TAG, StrawberryApplication.getMacAddress());

        return header;
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }
}
