package com.comp30022.helium.strawberry.components.ar;

import android.location.Location;

import com.comp30022.helium.strawberry.entities.User;

public class ARTrackerBeacon {
    private User user;
    private Location location;

    public ARTrackerBeacon(User user) {
        this.user = user;
    }

    public ARTrackerBeacon(ARTrackerBeacon trackerBeacon) {
        this.user = trackerBeacon.user;
        this.location = trackerBeacon.location;
    }

    public Location getLocation() {
        return new Location(location);
    }

    public void updateLocation(Location newLocation) {
        this.location = new Location(newLocation);
    }

    public String getSimpleName() {
        return user.getUsername();
    }

    public void getProfilePicture() {
//        return user.getFbPicture();
    }

    public User getUser() {
        return user;
    }
}
