package com.comp30022.helium.strawberry.ar;

import android.location.Location;

import com.comp30022.helium.strawberry.entities.Coordinate;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.services.LocationService;

import eu.kudan.kudan.ARNode;

public class ARArrowManager implements Subscriber<Location> {

    private User friend;
    private Coordinate arrowVector;
    private ARNode modelNode;
    private LocationService locationService;

    public ARArrowManager(User friend, ARNode modelNode, LocationService locationService) {
        this.friend = friend;
        this.locationService = locationService;
        this.modelNode = modelNode;
        this.arrowVector = new Coordinate(0, 1);
    }

    public void init() {
        // the arrow points forwards (with no notion of direction "yet")
        this.modelNode.rotateByDegrees(90, 1, 0, 0);
        this.modelNode.rotateByDegrees(-90, 0, 0, 1);
    }

    @Override
    public void update(Location location) {
        // get dot product of this.lastSelfLocation and targetLocation
        Location myLocation = locationService.getDeviceLocation();
        Location friendLocation = locationService.getUserLocation(friend);

        Coordinate unitVector = getDirectionalVector(myLocation, friendLocation).normalize();
        double angleToRotate = Math.acos(arrowVector.normalize().dot(unitVector));

        // does angleToRotate require a negative negation?
        // rotate on the Z-axis
        modelNode.rotateByDegrees((float)angleToRotate, 0, 0, 1);
    }

    private Coordinate getDirectionalVector(Location myLocation,
                                          Location friendLocation) {
        Coordinate a = new Coordinate(myLocation);
        Coordinate b = new Coordinate(friendLocation);
        return b.subtract(a);
    }
}
