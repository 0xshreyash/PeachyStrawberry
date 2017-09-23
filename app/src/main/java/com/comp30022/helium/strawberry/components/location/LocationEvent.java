package com.comp30022.helium.strawberry.components.location;


import android.location.Location;

import com.comp30022.helium.strawberry.entities.User;

/**
 * Wrapper class for location. LocationService.java uses this to inform all subscribers
 * the change of user's location. If the user is this device itself, the user field is
 * left as null.
 */
public class LocationEvent {

    private Location location;
    private User user;

    LocationEvent(Location location, User user) {
        this.location = location;
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public User getUser() {
        return user;
    }

    public boolean thisDeviceLocationChanged() {
        return this.user == null;
    }
}
