package com.comp30022.helium.strawberry.components.location;

import android.location.Location;

import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LocationService implements Publisher<LocationEvent>, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static LocationService instance;
    private static boolean setupCalled = false;
    private static final int INTERVAL_SECS = 5;
    private static final int FASTEST_INTERVAL_SECS = 1;
    private Set<User> trackingUsers;

    private List<Subscriber<LocationEvent>> subscribers; // all subscribers here

    public static LocationService getInstance() {
        if (instance == null || !setupCalled)
            return null;
        return instance;
    }

    /**
     * Setup current object as the singleton object
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

        subscribers = new ArrayList<>();
        trackingUsers = new LinkedHashSet<>();
    }

    public Location getDeviceLocation() {
        // this method should return this device's current location
        return mLastLocation;
    }

    public LocationRequest getRequest() {
        return mLocationRequest;
    }

    public void onResume() {
        mGoogleApiClient.connect();
    }

    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void setNewLocation(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        setNewLocation(location);
        // TODO: Remember to UPDATE database.
        LocationEvent locationEvent = new LocationEvent(location, null);
        notifyAllSubscribers(locationEvent);
    }


    public Location getUserLocation(User user) {
        // this method should translate User (java Type) into information
        Location location = new Location("LocationService.user." + user.getUsername());

        // TODO
        // this method should translate Friend (java Type) into information
        // that the Query language can use
        // to uniquely find the user in the database, then we can return
        // the last known location of this user
        // from the database. (REST calls)
        location.setLongitude(144.960961);
        location.setLatitude(-37.796927);
        return location;
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
