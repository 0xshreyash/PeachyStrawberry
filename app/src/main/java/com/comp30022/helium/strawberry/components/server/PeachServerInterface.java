package com.comp30022.helium.strawberry.components.server;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by noxm on 17/09/17.
 */

public class PeachServerInterface implements Publisher<Boolean>{
    private static final String TAG = "PeachServerInterface";
    private static final String SUCCESS_MESSAGE = "Successfully authorized";
    private static final long EXPIRE_TIME = 1800000L; // 30mins
    private static PeachServerInterface instance = null;
    private List<Subscriber<Boolean>> subs = new ArrayList<>();
    private Long initTime = 0L;

    public static PeachServerInterface getInstance() throws NotInstantiatedException, InstanceExpiredException {
        if(instance == null)
            throw new NotInstantiatedException();
        if(instance.expired())
            throw new InstanceExpiredException();

        return instance;
    }

    public static void init(String facebookToken, Subscriber<Boolean> toNotify) {
        if(instance == null || instance.expired())
            instance = new PeachServerInterface(facebookToken, toNotify);
        else toNotify.update(true);
    }

    private boolean expired() {
        return System.currentTimeMillis() - initTime > EXPIRE_TIME;
    }

    private PeachServerInterface(String token, Subscriber<Boolean> toNotify) {
        Log.i(TAG, "Initializing with token " + token);

        // construct header
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        // register notifiers
        if(toNotify != null)
            registerSubscriber(toNotify);

        PeachRestInterface.post("/authorize", tokenMap, new StrawberryListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resJson = new JSONObject(response);
                    String msg = (String) resJson.get("message");
                    if(msg.equals(SUCCESS_MESSAGE)) {
                        notifyAllSubscribers(true);
                        initTime = System.currentTimeMillis();
                    } else {
                        notifyAllSubscribers(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyAllSubscribers(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String msg = (error.getMessage() == null) ? error.networkResponse.statusCode + " Error" : error.getMessage();
                String data = new String(error.networkResponse.data);
                Log.e(TAG, msg);
                Log.e(TAG, data);
                notifyAllSubscribers(false);
            }
        }));
    }

    private void notifyAllSubscribers(boolean b) {
        for(Subscriber<Boolean> sub: subs) {
            sub.update(b);
        }
    }

    @Override
    public void registerSubscriber(Subscriber<Boolean> sub) {
        subs.add(sub);
    }

    @Override
    public void deregisterSubscriber(Subscriber<Boolean> sub) {
        subs.remove(sub);
    }
}
