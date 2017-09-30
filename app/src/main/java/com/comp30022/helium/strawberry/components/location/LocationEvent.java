package com.comp30022.helium.strawberry.components.location;


import android.location.Location;

import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Event;

/**
 * Wrapper class for location. LocationService.java uses this to inform all subscribers
 * the change of user's location. If the user is this device itself, the user field is
 * left as null.
 */
public class LocationEvent implements Event<LocationService, User, Location>{

    private final LocationService source;
    private final User key;
    private final Location value;

    LocationEvent(LocationService source, User user, Location location) {
        this.source = source;
        this.key = user;
        this.value = location;
    }

    @Override
    public LocationService getSource() {
        return source;
    }

    @Override
    public User getKey() {
        return key;
    }

    @Override
    public Location getValue() {
        return value;
    }
}
