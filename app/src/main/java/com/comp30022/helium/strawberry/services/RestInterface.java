package com.comp30022.helium.strawberry.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.comp30022.helium.strawberry.StrawberryApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.comp30022.helium.strawberry.StrawberryApplication.GET_TAG;
import static com.comp30022.helium.strawberry.StrawberryApplication.POST_TAG;
import static com.comp30022.helium.strawberry.StrawberryApplication.PUT_TAG;
import static com.comp30022.helium.strawberry.StrawberryApplication.DELETE_TAG;



public class RestInterface {

    private static RestInterface myInterface;
    public static final String INTERFACE_NAME = "wlan0";
    public static final String MAC_TAG = "mac";

    private RestInterface() {

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



    public String oldGet(String endPointURL) {

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            String macAddress = StrawberryApplication.getMacAddress();
            String result = getRequest.execute(endPointURL, macAddress).get();

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
    public void get(String endPointURL) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET,
                endPointURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GET Success", response.toString());
                        //Success Callback
                        // Do what you need to here.
                        // Maybe to make it flexible we pass of references to classes.

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        //Failure Callback

                    }
                });

        StrawberryApplication.getInstance().addToRequestQueue(GET_TAG, jsonArrReq);
    }

    public void get(String endPointURL, Response.Listener<JSONArray> listener) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.GET,
                endPointURL, null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        //Failure Callback

                    }
                }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                    return params;
                    }
                };

        StrawberryApplication.getInstance().addToRequestQueue(GET_TAG, jsonArrReq);
    }

    public void get(Response.Listener<JSONObject> listener, String endPointURL)  {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                endPointURL, null,
                listener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<>();
                        params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                        return params;
                    }
                };

        StrawberryApplication.getInstance().addToRequestQueue(GET_TAG, jsonObjReq);
    }


    public void post(String endPointURL, JSONArray postparams) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.POST,
                endPointURL, postparams,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("POST Success", response.toString());
                        //Success Callback

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<>();
                        params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                        return params;
                    }
                };

        // Adding the request to the queue along with a unique string tag
        StrawberryApplication.getInstance().addToRequestQueue(POST_TAG, jsonArrReq);
    }

    public void post(String endPointURL, JSONObject postparams) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                endPointURL, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("POST Success", response.toString());
                        //Success Callback

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<>();
                        params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                        return params;
                    }
                };

        // Adding the request to the queue along with a unique string tag
        StrawberryApplication.getInstance().addToRequestQueue(POST_TAG, jsonObjReq);
    }

    public void put(String endPointURL, JSONObject putparams) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                endPointURL, putparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PUT Success", response.toString());
                        //Success Callback

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                return params;
            }
        };

        // Adding the request to the queue along with a unique string tag
        StrawberryApplication.getInstance().addToRequestQueue(PUT_TAG, jsonObjReq);

    }

    public void put(String endPointURL, JSONArray putparams) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.PUT,
                endPointURL, putparams,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("PUT Success", response.toString());
                        //Success Callback

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                return params;
            }
        };

        // Adding the request to the queue along with a unique string tag
        StrawberryApplication.getInstance().addToRequestQueue(PUT_TAG, jsonArrReq);
    }

    /**
     * The delete has been written on the assuption that it works the same way as
     * POST and PUT, please modify if it doesn't work correctly.
     * @param endPointURL
     * @param deleteparams
     */
    public void delete(String endPointURL, JSONObject deleteparams) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                endPointURL, deleteparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("DELETE Success", response.toString());
                        //Success Callback

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                return params;
            }
        };

        // Adding the request to the queue along with a unique string tag
        StrawberryApplication.getInstance().addToRequestQueue(DELETE_TAG, jsonObjReq);

    }

    public void delete(String endPointURL, JSONArray deleteparams) {
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(Request.Method.DELETE,
                endPointURL, deleteparams,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("DELETE Success", response.toString());
                        //Success Callback

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Failure Callback

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put(MAC_TAG, StrawberryApplication.getMacAddress());

                return params;
            }
        };

        // Adding the request to the queue along with a unique string tag
        StrawberryApplication.getInstance().addToRequestQueue(DELETE_TAG, jsonArrReq);
    }

}
