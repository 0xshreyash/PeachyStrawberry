package com.comp30022.helium.strawberry.components.location;

import android.location.Location;
import android.util.Log;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService implements Publisher<LocationEvent>, LocationListener {
    private static final String TAG = "PeachLocationService";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static LocationService instance;
    private static boolean setupCalled = false;
    public static final int INTERVAL_SECS = 5;
    public static final int FASTEST_INTERVAL_SECS = 1;
    public static final long QUERY_TIME_SECS = 3;
    public static final long BG_QUERY_TIME_SECS = 15;
    private Set<User> trackingUsers;

    private Timer timer;

    private HashMap<User, Location> locationCache;

    private List<Subscriber<LocationEvent>> subscribers; // all subscribers here

    public static LocationService getInstance() {
        if (instance == null || !setupCalled)
            return null;
        return instance;
    }

    /**
     * Setup current object as the singleton object
     *
     * @param mGoogleApiClient
     */
    public void setup(GoogleApiClient mGoogleApiClient) {
        if (setupCalled) {
            return;
        }

        setupCalled = true;
        /* handle initialization here */
        this.mGoogleApiClient = mGoogleApiClient;
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(INTERVAL_SECS * 1000)
                .setFastestInterval(FASTEST_INTERVAL_SECS * 1000);
        this.mGoogleApiClient.connect();

        instance = this;

        locationCache = new HashMap<>();
        subscribers = new ArrayList<>();
        trackingUsers = new LinkedHashSet<>();
        timer = new Timer();

        // track all friends
        for(User friend: StrawberryApplication.getCachedFriends()) {
            trackingUsers.add(friend);
        }

        timer.scheduleAtFixedRate(getLocationQueryTimerTask(), 0, QUERY_TIME_SECS * 1000);
    }

    public Location getDeviceLocation() {
        // this method should return this device's current location
        return new Location(mLastLocation);
    }

    public LocationRequest getRequest() {
        return mLocationRequest;
    }

    public void onResume() {
        mGoogleApiClient.connect();
        timer = new Timer();
        timer.scheduleAtFixedRate(getLocationQueryTimerTask(), 0, QUERY_TIME_SECS * 1000);
    }

    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        timer.cancel();
        timer = null;
    }

    public void setNewLocation(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        setNewLocation(location);
        LocationEvent locationEvent = new LocationEvent(this, PeachServerInterface.currentUser(), location);
        try {
            PeachServerInterface.getInstance().updateCurrentLocation(location);
        } catch (NotInstantiatedException | InstanceExpiredException e) {
            e.printStackTrace();
        }
        notifyAllSubscribers(locationEvent);
    }

    public Location getUserLocation(User user) {
        // this method should translate User (java Type) into information
        if (locationCache.containsKey(user)) {
            return locationCache.get(user);

        } else {
            Log.e(TAG, "user and location not found, returning UH");
            Location location = new Location("LocationService.user." + user.getUsername());
            location.setLongitude(144.960961);
            location.setLatitude(-37.796927);

            return location;
        }
    }

    private TimerTask getLocationQueryTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Getting users location");

                for (final User friend : trackingUsers) {
                    Log.d(TAG, "Getting users location, query: " + friend);

                    try {
                        PeachServerInterface.getInstance().getUserLocation(friend, new StrawberryListener(new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Getting users location, got:" + response);

                                try {
                                    JSONArray locArr = new JSONArray(response);
                                    if (locArr.length() != 0) {
                                        JSONObject latestLoc = (JSONObject) locArr.get(0);
                                        Log.d(TAG, "Getting users location, latest:" + latestLoc);

                                        Double longitude = (Double) latestLoc.get("longitude");
                                        Double latitude = (Double) latestLoc.get("latitude");
                                        Location newLocation = new Location(this.getClass().getSimpleName());

                                        newLocation.setLatitude(latitude);
                                        newLocation.setLongitude(longitude);

                                        updateLocationCache(friend, newLocation);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, null));
                    } catch (NotInstantiatedException | InstanceExpiredException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void updateLocationCache(User user, Location location) {
        if (locationCache.containsKey(user)) {
            Location lastLoc = locationCache.get(user);
            // TODO: update if altitude added
            if (lastLoc.getLatitude() == location.getLatitude() && lastLoc.getLongitude() == location.getLongitude())
                return;
        }

        locationCache.put(user, location);
        notifyAllSubscribers(new LocationEvent(this, user, location));
    }

    public void addTracker(User friend) {
        this.trackingUsers.add(friend);
    }

    public void removeTracker(User friend) {
        this.trackingUsers.remove(friend);
    }

    public void registerSubscriber(Subscriber<LocationEvent> sub) {
        // this should add the subscriber into its list
        subscribers.add(sub);
    }

    public void deregisterSubscriber(Subscriber<LocationEvent> sub) {
        // this should deregister the subscriber from the list
        subscribers.remove(sub);
    }

    private void notifyAllSubscribers(LocationEvent location) {
        // this method will call the update() method on all subscribers that are
        // registered.
        for (Subscriber<LocationEvent> sub : subscribers) {
            sub.update(location);
        }
    }
}
