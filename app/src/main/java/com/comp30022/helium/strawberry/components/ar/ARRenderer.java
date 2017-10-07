package com.comp30022.helium.strawberry.components.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ARRenderer extends View {
    private float[] projectionMatrix;
    private List<ARTrackerBeacon> trackers;
    private Location currentLocation;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int W = 3;

    public ARRenderer(Context context) {
        super(context);
    }

    public void addTrackers(List<ARTrackerBeacon> trackers) {
        // the list of tracking points we want to track and render on screen
        this.trackers = trackers;
    }

    public void addTracker(final ARTrackerBeacon tracker) {
        // add to the list of tracking points we want to track and render on screen
        if (this.trackers != null) {
            this.trackers.add(tracker);
        } else {
            this.trackers = new ArrayList<ARTrackerBeacon>() {{
                add(new ARTrackerBeacon(tracker));
            }};
        }
    }

    public void updateProjectionMatrix(float[] newProjectionMatrix) {
        this.projectionMatrix = Arrays.copyOf(newProjectionMatrix, newProjectionMatrix.length);
        // re-draw the points
        this.invalidate();
    }

    public void updateLocation(LocationEvent locationEvent) {
        if (locationEvent.getKey().equals(PeachServerInterface.currentUser())) {
            // it's this device's location's update
            this.currentLocation = locationEvent.getValue();
        } else {
            // find the tracking point and update that point's location
            User updatedUserLocation = locationEvent.getKey();
            // loop through all our tracking targets and find the user that corresponds to this
            // update. Once found, update the beacon's location and break off this loop
            for (ARTrackerBeacon trackerBeacon : trackers) {
                if (trackerBeacon.getUser().equals(updatedUserLocation)) {
                    trackerBeacon.updateLocation(locationEvent.getValue());
                    break;
                }
            }
        }

        // re-draw the points
        this.invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO: remove
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i != trackers.size(); ++i) {
            float[] ENUCoordinates = getENU(trackers.get(i).getLocation());
            // convert from : ENU -> Camera
            float[] cameraCoordinates = convertToCameraSpace(ENUCoordinates);

            // if the point is infront of us ==> i.e. we should render it!
            if (cameraCoordinates[Z] > 0) {
                float[] screenCoordinates = convertToScreenSpace(
                        cameraCoordinates,
                        canvas.getWidth(),
                        canvas.getHeight());
                canvas.drawCircle(screenCoordinates[X], screenCoordinates[Y], 30, paint);
            } else {
                // point is at somewhere we can't see it
            }
        }
    }

    private float[] getENU(Location targetLocation) {
        float[] deviceLocationECEF = Converter.GPS2ECEF(this.currentLocation);
        float[] targetECEF = Converter.GPS2ECEF(targetLocation);
        return Converter.ECEF2ENU(this.currentLocation,
                deviceLocationECEF,
                targetECEF);
    }

    private float[] convertToCameraSpace(float[] ENU) {
        float[] cameraSpace = new float[4];
        // multiply ENU (as a vector) with the projection matrix to get CameraSpace
        Matrix.multiplyMV(cameraSpace, 0, this.projectionMatrix, 0, ENU, 0);
        return cameraSpace;
    }

    private float[] convertToScreenSpace(float[] cameraSpace, int width, int height) {
        float x = (cameraSpace[X] / cameraSpace[W]) * width;
        float y = (cameraSpace[Y] / cameraSpace[W]) * height;
        return new float[]{x, y};
    }


    /**
     * This class is a convertion class for location -> ECEF (Earth centered earth focused)
     * -> ENU (east north up)
     * <p>
     * Adapted from
     * https://github.com/dat-ng/ar-location-based-android/blob/master/app/src/main/java/ng/dat/ar/helper/LocationHelper.java
     * <p>
     * and convertion formula obtained from:
     * http://digext6.defence.gov.au/dspace/bitstream/1947/3538/1/DSTO-TN-0432.pdf
     * <p>
     * <p>
     * The variables named here follows the variables named in the PDF file as the formula was
     * laid out
     **/
    private static class Converter {
        // WGS 84 semi-major axis constant in meters
        private final static double WGS84_A = 6378137.0;
        // square of WGS 84 eccentricity
        private final static double WGS84_E2 = 0.00669437999014;

        private static float[] GPS2ECEF(Location location) {
            double radiansLat = Math.toRadians(location.getLatitude());
            double radiansLong = Math.toRadians(location.getLongitude());

            float cosLat = (float) Math.cos(radiansLat);
            float sinLat = (float) Math.sin(radiansLat);
            float cosLong = (float) Math.cos(radiansLong);
            float sinLong = (float) Math.sin(radiansLong);

            float chi = (float) (WGS84_A / Math.sqrt(1.0 - WGS84_E2 * sinLat * sinLat));

            float x = (float) ((chi + location.getAltitude()) * cosLat * cosLong);
            float y = (float) ((chi + location.getAltitude()) * cosLat * sinLong);
            float z = (float) ((chi * (1.0 - WGS84_E2) + location.getAltitude()) * sinLat);

            return new float[]{x, y, z};

        }

        private static float[] ECEF2ENU(Location location, float[] ECEF, float[] targetECEF) {
            double radiansLat = Math.toRadians(location.getLatitude());
            double radiansLon = Math.toRadians(location.getLongitude());

            float cosLat = (float) Math.cos(radiansLat);
            float sinLat = (float) Math.sin(radiansLat);
            float cosLong = (float) Math.cos(radiansLon);
            float sinLong = (float) Math.sin(radiansLon);

            float dx = targetECEF[0] - ECEF[0];
            float dy = targetECEF[1] - ECEF[1];
            float dz = targetECEF[2] - ECEF[2];

            float east = -sinLong * dx + cosLong * dy;
            float north = -sinLat * cosLong * dx - sinLat * sinLong * dy + cosLat * dz;
            float up = cosLat * cosLong * dx + cosLat * sinLong * dy + sinLat * dz;

            return new float[]{east, north, up, 1};
        }
    }
}
