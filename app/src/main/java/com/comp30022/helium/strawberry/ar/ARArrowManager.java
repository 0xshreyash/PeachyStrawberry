package com.comp30022.helium.strawberry.ar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.comp30022.helium.strawberry.entities.Coordinate;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.services.LocationService;
import com.jme3.math.Quaternion;

import java.util.Arrays;

import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARNode;

public class ARArrowManager implements Subscriber<Location> {
    private static final String TAG = ARArrowManager.class.getSimpleName();
    private User friend;
    private Coordinate arrowVector;
    private ARModelNode modelNode;
    private LocationService locationService;
    private ARCameraViewActivity m;
    private SensorManager sensorManager;
    private double resetAngleTheta;
    private float[] mAccel;
    private float[] mMag;
    private Quaternion orignalOrientation;
    private Quaternion parentOrignalOrientation;
    private int debug;

    public ARArrowManager(ARCameraViewActivity m, SensorManager sensorManager, User friend,
                          ARModelNode modelNode, LocationService locationService) {
        //TODO: remove this is not needed after debugging
        this.m = m;
        this.sensorManager = sensorManager;
        this.mAccel = new float[3];
        this.mMag = new float[3];
        this.friend = friend;
        this.locationService = locationService;
        this.modelNode = modelNode;
        this.arrowVector = new Coordinate(0, 1);
        this.locationService.registerSubscriber(this);
    }

    public void init() {
        // the arrow points forwards (with no notion of direction "yet")
        Log.e(TAG, "PRE ORIENTATION: " + this.modelNode.getOrientation().toString());
        Log.e(TAG, "PRE WORLD ORI" + this.modelNode.getWorldOrientation().toString());
        this.modelNode.rotateByDegrees(90, 1, 0, 0);
        this.modelNode.rotateByDegrees(-90, 0, 0, 1);
        Log.e(TAG, "POST ORIENTATION: " + this.modelNode.getOrientation().toString());
        Log.e(TAG, "POST WORLD ORI" + this.modelNode.getWorldOrientation().toString());
        this.resetAngleTheta = 0;
        this.orignalOrientation = new Quaternion(this.modelNode.getWorldOrientation());
        this.parentOrignalOrientation = new Quaternion(this.modelNode.getParent().getWorldOrientation());
    }


    @Override
    public void update(Location location) {
        // get dot product of this.lastSelfLocation and targetLocation
        Location myLocation = locationService.getDeviceLocation();
        Location friendLocation = locationService.getUserLocation(friend);

        float bearing = myLocation.bearingTo(friendLocation);
        double angleToNorth = angleToTrueNorth();

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
        if (angleToNorth > 0) {
            bearing = -bearing;
        }
        // reset orientation to forward
        if (debug % 2 != 0) {
            m.debugMessage("Back to 0");
            reset();
        } else {
            // re-rotate to point to north
            double rotBy = (-angleToNorth);
            m.debugMessage("Need to rotate by " + rotBy);
            modelNode.rotateByDegrees((float)rotBy, 0, 0, 1);
        }
debug++;
    }

    private void reset() {
        this.modelNode.setOrientation(this.orignalOrientation);
    }

    private double angleToTrueNorth() {
        if (mAccel != null && mMag != null) {
            float[] rotationMatrix = new float[9];
            boolean notFreeFalling =
                    SensorManager.getRotationMatrix(rotationMatrix, null, mAccel, mMag);
            // freeFalling means the rotation matrix isnt' correct! (shouldn't ever happen)
            if (notFreeFalling) {
                float[] res = new float[3];
                SensorManager.getOrientation(rotationMatrix, res);
                double deg = Math.toDegrees(res[0]);
//                m.debugMessage("Deg to north: " + deg);
                return deg;
            } else {
                Log.w(TAG, "Device free falling for some reason");
//                m.debugMessage("Device free falling for some reason");
            }
        } else {
            m.debugMessage("mAccel or mMag is null");
        }
        return 0;
    }

    public void sensorChanged(SensorEvent e) {
        switch (e.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccel = Arrays.copyOf(e.values, e.values.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMag = Arrays.copyOf(e.values, e.values.length);
                break;
        }
    }

}
