package com.comp30022.helium.strawberry.components.server;

import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by noxm on 17/09/17.
 */

public class PeachServerInterface implements Publisher<Boolean> {
    private static final String TAG = "PeachServerInterface";
    private static final long EXPIRE_TIME = 1800000L; // 30mins
    private static PeachServerInterface instance = null;
    private static String userId = "";

    private List<Subscriber<Boolean>> subs = new ArrayList<>();
    private Long initTime = 0L;

    public static PeachServerInterface getInstance() throws NotInstantiatedException, InstanceExpiredException {
        if (instance == null)
            throw new NotInstantiatedException();
        if (instance.expired())
            throw new InstanceExpiredException();

        return instance;
    }

    public static void init(String facebookToken, Subscriber<Boolean> toNotify) {
        if (instance == null || instance.expired() || userId.length() == 0) {
            instance = new PeachServerInterface(facebookToken, toNotify);
            instance.initTime = System.currentTimeMillis();
        }
        else toNotify.update(true);
    }

    private boolean expired() {
        return System.currentTimeMillis() - initTime > EXPIRE_TIME;
    }

    private PeachServerInterface(String token, Subscriber<Boolean> toNotify) {
        StrawberryApplication.getInstance().getRequestQueue().getCache().clear();

        Log.i(TAG, "Initializing with token " + token);

        // construct header
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        // register notifiers
        if (toNotify != null)
            registerSubscriber(toNotify);

        PeachRestInterface.post("/authorize", tokenMap, new StrawberryListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resJson = new JSONObject(response);
                    Log.i(TAG, "user id is " + resJson.get("message"));
                    userId = (String) resJson.get("message");
                    notifyAllSubscribers(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    notifyAllSubscribers(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    try {
                        String msg = (error.getMessage() == null) ? error.networkResponse.statusCode + " Error" : error.getMessage();
                        String data = new String(error.networkResponse.data);
                        Log.e(TAG, msg);
                        Log.e(TAG, data);
                        notifyAllSubscribers(false);
                    } catch (Exception e) {
                        Log.e(TAG, "Error in volley");
                    }
                }
            }
        }));
    }

    private void notifyAllSubscribers(boolean b) {
        for (Subscriber<Boolean> sub : subs) {
            sub.update(b);
        }
    }

    /**
     * Rest call to update users current location
     *
     * @param location
     */
    public void updateCurrentLocation(Location location) {
        if (userId != null && userId.length() > 0) {
            Map<String, String> form = new HashMap<>();
            form.put("longitude", String.valueOf(location.getLongitude()));
            form.put("latitude", String.valueOf(location.getLatitude()));
            PeachRestInterface.post("/user/" + userId + "/location", form, new StrawberryListener());
        }
    }

    public void addFriendFbId(String id) {
        if (userId != null && userId.length() > 0) {
            Map<String, String> form = new HashMap<>();
            form.put("fbId", id);
            PeachRestInterface.post("/user/" + userId + "/friend", form, new StrawberryListener());
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

    public static User currentUser() {
        return new User(userId);
    }

    public void getUserLocation(User friend, StrawberryListener strawberryListener) {
        if (userId != null && userId.length() > 0) {
            PeachRestInterface.get("/user/" + friend.getId() + "/location", strawberryListener);
        }
    }

    public void getFriends(StrawberryListener strawberryListener) {
        if (userId != null && userId.length() > 0) {
            PeachRestInterface.get("/user/" + userId + "/friend", strawberryListener);
        }
    }

    public void getUser(String id, StrawberryListener strawberryListener) {
        if (userId != null && userId.length() > 0) {
            PeachRestInterface.get("/user/" + id, strawberryListener);
        }
    }

    public void getChatLog(User friend, Long start, StrawberryListener strawberryListener) {
        PeachRestInterface.get("/chat?start=" + start.toString() + "&from=" + friend.getId(), strawberryListener);
    }

    public void postChat(String message, String to, StrawberryListener strawberryListener) {
        Map<String, String> form = new HashMap<>();
        form.put("to", to);
        form.put("message", message);
        PeachRestInterface.post("/chat", form, strawberryListener);
    }
}
