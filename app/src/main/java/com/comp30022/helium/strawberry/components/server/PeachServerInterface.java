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
import com.comp30022.helium.strawberry.patterns.Event;
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

public class PeachServerInterface implements Publisher<Event> {
    private static final String TAG = "PeachServerInterface";
    private static final long EXPIRE_TIME = 18000000L; // 300mins

    private static PeachServerInterface instance = null;
    private static String userId = "";

    private List<Subscriber<Event>> subs = new ArrayList<>();
    private static Long initTime = 0L;
    private static Long initResponseTime = null;

    public static PeachServerInterface getInstance() throws NotInstantiatedException, InstanceExpiredException {
        if (instance == null)
            throw new NotInstantiatedException();
        if (instance.expired())
            throw new InstanceExpiredException();

        return instance;
    }

    public static void init(String facebookToken, Subscriber<Event> toNotify) {
        if (instance == null || instance.expired() || userId.length() == 0) {
            initTime = System.currentTimeMillis();
            instance = new PeachServerInterface(facebookToken, toNotify);
        }
        else toNotify.update(new InterfaceReadyEvent(instance, "", true));
    }

    private boolean expired() {
        return System.currentTimeMillis() - initTime > EXPIRE_TIME;
    }

    private PeachServerInterface(String token, Subscriber<Event> toNotify) {
        StrawberryApplication.getInstance().getRequestQueue().getCache().clear();

        Log.i(TAG, "Initializing with token " + token);

        // construct header
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        // register notifiers
        if (toNotify != null)
            registerSubscriber(toNotify);

        PeachRestInterface.post("/authorize", tokenMap, new StrawberryListener("authorize", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resJson = new JSONObject(response);

                    userId = resJson.getString("message");
                    initResponseTime = resJson.getLong("timestamp");

                    Log.i(TAG, "user id is " + userId +
                            ", inittime=" + initTime +
                            ", initres=" + initResponseTime);

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
                    } catch (Exception e) {
                        Log.e(TAG, "Error in volley");
                    }
                }

                notifyAllSubscribers(false);
            }
        }));
    }

    private void notifyAllSubscribers(boolean b) {
        for (Subscriber<Event> sub : subs) {
            sub.update(new InterfaceReadyEvent(this, "", b));
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

            Log.d(TAG, "updateCurrentLocation Long: " + String.valueOf(location.getLongitude()) + ", Lat:" + String.valueOf(location.getLatitude()));
            PeachRestInterface.post("/user/" + userId + "/location", form, new StrawberryListener("updateCurrentLocation"));
        }
    }

    public void addFriendFbId(String id) {
        if (userId != null && userId.length() > 0) {
            Map<String, String> form = new HashMap<>();
            form.put("fbId", id);
            PeachRestInterface.post("/user/" + userId + "/friend", form, new StrawberryListener("addFriendFbId"));
        }
    }

    @Override
    public void registerSubscriber(Subscriber<Event> sub) {
        subs.add(sub);
    }

    @Override
    public void deregisterSubscriber(Subscriber<Event> sub) {
        subs.remove(sub);
    }

    public static User currentUser() {
        return User.getUser(userId);
    }

    public void getUserLocation(User friend, StrawberryListener strawberryListener) {
        if (userId != null && userId.length() > 0) {
            strawberryListener.setOrigin("getUserLocation");

            PeachRestInterface.get("/user/" + friend.getId() + "/location", strawberryListener);
        }
    }

    public void getFriends(StrawberryListener strawberryListener) {
        if (userId != null && userId.length() > 0) {
            strawberryListener.setOrigin("getFriends");

            PeachRestInterface.get("/user/" + userId + "/friend", strawberryListener);
        }
    }

    public void getUser(String id, StrawberryListener strawberryListener) {
        if (userId != null && userId.length() > 0) {
            strawberryListener.setOrigin("getUser");

            PeachRestInterface.get("/user/" + id, strawberryListener);
        }
    }

    public void getChatLog(User friend, Long start, StrawberryListener strawberryListener) {
        strawberryListener.setOrigin("getChatLog");

        PeachRestInterface.get("/chat?start=" + start.toString() + "&from=" + friend.getId(), strawberryListener);
    }

    public void postChat(String message, String to, StrawberryListener strawberryListener) {
        strawberryListener.setOrigin("postChat");

        Map<String, String> form = new HashMap<>();
        form.put("to", to);
        form.put("message", message);
        form.put("timestamp", String.valueOf(System.currentTimeMillis()));
        Log.d(TAG, "postChat: " + form);
        PeachRestInterface.post("/chat", form, strawberryListener);
    }

    public void getRecentChatLog(User from, StrawberryListener listener) {
        listener.setOrigin("getRecentChatLog");

        PeachRestInterface.get("/chat/recent?from=" + from.getId(), listener);
    }

    public static class InterfaceReadyEvent implements Event<PeachServerInterface, String, Boolean> {

        private final PeachServerInterface s;
        private final String k;
        private final Boolean v;

        public InterfaceReadyEvent(PeachServerInterface s, String k, Boolean v) {
            this.s = s;
            this.k = k;
            this.v = v;
        }

        @Override
        public PeachServerInterface getSource() {
            return s;
        }

        @Override
        public String getKey() {
            return k;
        }

        @Override
        public Boolean getValue() {
            return v;
        }
    }

    public static Long getInitTime() {
        return initTime;
    }

    public static Long getInitResponseTime() {
        return initResponseTime;
    }

    public static Long getTimeDiff() {
        return initResponseTime - initTime;
    }
}
