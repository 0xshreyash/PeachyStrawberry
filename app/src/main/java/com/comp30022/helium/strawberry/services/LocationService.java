package com.comp30022.helium.strawberry.services;

import android.location.Location;

import com.comp30022.helium.strawberry.entities.Friend;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import java.util.List;

public class LocationService implements Publisher<Location> {

    private List<Subscriber<Location>> subscribers; // all subscribers here

    public LocationService() { /* handle initialization here */ }
    public void setup() { /* handle setup here */ }

    public Location getDeviceLocation() {
        // this method should return this device's current location
        return null;
    }

    public Location getUserLocation(Friend user) {
        // this method should translate Friend (java Type) into information
        // that the Query language can use
        // to uniquely find the user in the database, then we can return
        // the last known location of this user
        // from the database. (REST calls)
        return null;
    }

    private void update() {
        // this method should update this device's location
        // periodically (every X seconds)
        // after updating the latest location, do:
        // 1) sent latest information (location) to the database (REST calls)
        // 2) call this.notifyAllObservers(Location location)
    }
    public void registerSubscriber(Subscriber<Location> sub) {
        // this should add the subscriber into its list
    }
    public void deregisterSubscriber(Subscriber<Location> sub) {
        // this should deregister the subscriber from the list
    }

    private void notifyAllSubscribers(Location location) {
        // this method will call the update() method on all subscribers that are
        // registered.
        for (Subscriber<Location> sub : subscribers) {
            sub.update(location);
        }
    }
}
