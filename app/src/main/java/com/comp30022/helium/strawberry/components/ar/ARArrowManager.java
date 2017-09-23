package com.comp30022.helium.strawberry.components.ar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;

import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.entities.Coordinate;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import java.util.Arrays;

import eu.kudan.kudan.ARModelNode;

public class ARArrowManager implements Subscriber<Location> {
    private static final String TAG = ARArrowManager.class.getSimpleName();
    private User friend;
    private ARModelNode modelNode;
    private LocationService locationService;
    private ARCameraViewActivity m;
    private Coordinate lastDirection;
    private Coordinate forward;
    private float[] rotationMatrix;
    private double lastAzimuth = 0;
    private boolean azimuthCalculated = false;
    private float[] mMag;
    private float[] mAccel;
    private float[] orientation;


    public ARArrowManager(ARCameraViewActivity m, User friend,
                          ARModelNode modelNode, LocationService locationService) {
        //TODO: remove this is not needed after debugging
        this.m = m;
        this.rotationMatrix = new float[9];
        this.mMag = new float[3];
        this.mAccel = new float[3];
        this.orientation = new float[3];
        this.friend = friend;
        this.locationService = locationService;
        this.modelNode = modelNode;
        this.locationService.registerSubscriber(this);
        this.lastDirection = new Coordinate(0, 1);
        this.forward = new Coordinate(0, 1);
    }

    public void init() {
        // the arrow points forwards (with no notion of direction "yet")
        this.modelNode.rotateByDegrees(90, 1, 0, 0);
        this.modelNode.rotateByDegrees(-90, 0, 0, 1);
        update(null);
    }


    @Override
    public void update(Location location) {
        // get dot product of this.lastSelfLocation and targetLocation
        Location myLocation = locationService.getDeviceLocation();
        Location friendLocation = locationService.getUserLocation(friend);
        pointToLocation(myLocation, friendLocation);
    }

    private void pointToLocation(Location self, Location target) {
        // bearing is clockwise!
        double rotBy = calculateRotation(-self.bearingTo(target));
        m.debugMessage("Azimuth " + this.lastAzimuth + " bearing: " + self.bearingTo(target));
        modelNode.rotateByDegrees((float)normalToKudanAngle(rotBy), 0, 0, 1);

        // record current directional vector
        this.lastDirection = this.lastDirection.rotateDegree(rotBy).normalize();
    }

    private double calculateRotation(double angleFromNorth) {
        // calculate target as if the arrow is pointing forward on the screen
        Coordinate target = new Coordinate(this.forward);
        double theta = this.lastAzimuth + angleFromNorth;
        target = target.rotateDegree(theta).normalize();

        // get the rotation angle from old vector to new vector
        double angle = Math.atan2(target.getY(), target.getX()) -
                Math.atan2(lastDirection.getY(), lastDirection.getX());
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return Math.toDegrees(angle);
    }

    public void sensorChanged(SensorEvent e) {
        if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mAccel = Arrays.copyOf(e.values, e.values.length);
        if (e.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mMag= Arrays.copyOf(e.values, e.values.length);
        if (mAccel != null && mMag != null) {
            boolean success = SensorManager.getRotationMatrix(this.rotationMatrix, null, mAccel, mMag);
            if (success) {
                SensorManager.getOrientation(this.rotationMatrix, orientation);
                double azimuth = Math.toDegrees(orientation[0]);
                if (this.azimuthCalculated) {
                    double deltaDegree = this.lastAzimuth - azimuth;
                    this.forward = this.forward.rotateDegree(deltaDegree).normalize();
                } else {
                    this.azimuthCalculated = true;
                }
                this.lastAzimuth = azimuth;
            }
        }
    }

    private double normalToKudanAngle(double ang) {
        // neg theta since Kudan's + is right side (normally it's left for pos)
        // negate angle to north
        // our Kudan Z-Axis is positive to the right, but this is flipped
        //       Z
        //       ^
        //    -  |  +
        //    ---|--> Y

        // i.e.
        // to left  -> -
        // to right -> +

        // in angleToNorth, the angleToNorth looks like
        //       S
        //       ^
        //    +  |  -
        //  E <--|--> W
        //    +  |  -
        //       N
        // i.e. TO LEFT  -> +
        //      TO RIGHT -> -   until north
        return -ang;
    }

}

