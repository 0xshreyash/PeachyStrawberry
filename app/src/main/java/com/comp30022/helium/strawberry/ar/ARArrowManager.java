package com.comp30022.helium.strawberry.ar;

import com.comp30022.helium.strawberry.entities.Coordinate;
import com.comp30022.helium.strawberry.entities.Friend;
import com.comp30022.helium.strawberry.services.MockLocationServices;
import com.jme3.math.Vector2f;

import eu.kudan.kudan.ARNode;

public class ARArrowManager {

    private Friend self;
    private Friend friend;
    private Coordinate lastSelfLocation;
    private Coordinate lastFriendLocation;
    private Vector2f arrowVector;
    private ARNode modelNode;

    public ARArrowManager(Friend self, Friend friend, ARNode modelNode) {
        this.self = self;
        this.friend = friend;
        this.lastSelfLocation = getLatestCoordinateOf(self);
        this.lastFriendLocation = getLatestCoordinateOf(friend);
        this.modelNode = modelNode;
        this.arrowVector = new Vector2f(0, 1);
    }

    public void init() {
        // the arrow points forwards (with no notion of direction "yet")
        this.modelNode.rotateByDegrees(90, 1, 0, 0);
        this.modelNode.rotateByDegrees(-90, 0, 0, 1);
    }

    /**
     * sits within a loop - updates current location and friend's location periodically
     */
    public void update() {
        if (this.lastFriendLocation != null && this.lastSelfLocation != null) {
            this.lastSelfLocation   = getLatestCoordinateOf(this.self);
            this.lastFriendLocation = getLatestCoordinateOf(this.friend);

            // get dot product of this.lastSelfLocation and targetLocation
            Vector2f unitVector = getDirectionalVector(true);
            double angleToRotate = Math.acos(arrowVector.normalize().dot(unitVector));

            // does angleToRotate require a negative negation?
            // rotate on the Z-axis
            modelNode.rotateByDegrees((float)angleToRotate, 0, 0, 1);
        }
    }

    private Coordinate getLatestCoordinateOf(Friend f) {
        return MockLocationServices.getCoordinate(f);
    }

    /**
     * Returns a vector from self's location pointing to friend's location
     * i.e. Friend.vector() - Self.vector() => directional vector from self to friend
     * No vectors will be modified (i.e. vector.*Local() is not used to preserve states)
     *
     * @param normalize True if the returned vector should be normalized, false otherwise
     * @return directional vector (normalized or not depends on param)
     */
    private Vector2f getDirectionalVector(boolean normalize) {
        Vector2f a = this.lastSelfLocation.getVector();
        Vector2f b = this.lastFriendLocation.getVector();
        if (normalize) {
            return b.subtract(a).normalize();
        }
        return b.subtract(a);
    }
}
