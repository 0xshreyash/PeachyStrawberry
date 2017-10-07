package com.comp30022.helium.strawberry.components.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.helpers.ColourScheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ARRenderer extends View {
    private float[] projectionMatrix;
    private static final String TAG = ARRenderer.class.getSimpleName();
    private List<ARTrackerBeacon> trackers;
    private Location currentLocation;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int W = 3;
    private static final int NAME_HEIGHT_OFFSET = 80;
    private static final int NAME_WIDTH_OFFSET = 30;
    private static final int DEFAULT_CIRCLE_RADIUS = 30;
    private static final float OFFSET = .5f;
    private static final int GUIDE_OFFSET = 100;      // offset for the guide artefact
    private ARActivity arActivity;
    private Paint defaultPaintCircle;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BTM_LEFT, BTM_RIGHT
    }


    public ARRenderer(Context context) {
        super(context);
        // this is dangerous, but we're sure that only ARActivity is using this ARRenderer for now
        this.arActivity = (ARActivity) context;

        // default dot if user's profile picture isn't available
        this.defaultPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.defaultPaintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        this.defaultPaintCircle.setColor(ColourScheme.PRIMARY_DARK);
        this.defaultPaintCircle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        this.defaultPaintCircle.setTextSize(50);
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
            Log.e(TAG, "You updated location to" + locationEvent.getValue());
            this.currentLocation = new Location(locationEvent.getValue());
        } else {
            // find the tracking point and update that point's location
            User updatedUserLocation = locationEvent.getKey();
            // loop through all our tracking targets and find the user that corresponds to this
            // update. Once found, update the beacon's location and break off this loop
            for (ARTrackerBeacon trackerBeacon : trackers) {
                if (trackerBeacon.getUser().equals(updatedUserLocation)) {
                    trackerBeacon.updateLocation(locationEvent.getValue());
                    Log.e(TAG, "Friend updated location to" + locationEvent.getValue());
                    break;
                }
            }
        }

        // re-draw the points
        this.invalidate();
    }

    public ARActivity getArActivity() {
        return this.arActivity;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (currentLocation == null || this.projectionMatrix == null) {
            return;
        }


        for (int i = 0; i != trackers.size(); ++i) {
            ARTrackerBeacon target = trackers.get(i);
            float[] ENUCoordinates = getENU(target.getLocation());
            // convert from : ENU -> Camera
            float[] cameraCoordinates = convertToCameraSpace(ENUCoordinates);

            float[] screenCoordinates = convertToScreenSpace(
                    cameraCoordinates,
                    canvas.getWidth(),
                    canvas.getHeight());
            float x = screenCoordinates[X];
            float y = screenCoordinates[Y];

            if (Float.isNaN(x) || Float.isNaN(y)) {
                this.arActivity.displayInfoHUD("You have arrived at " + target.getUserName()
                        + "'s location");
            } else {
                this.arActivity.displayInfoHUD("You're " +
                        target.getLocation().distanceTo(currentLocation)
                        + "m away from " + target.getUserName());
            }

            // if the point is infront of us ==> i.e. we should render it!
            if (cameraCoordinates[Z] > 0) {
                Bitmap profilePicture = target.getProfilePicture(this);
                if (profilePicture != null) {
                    canvas.drawBitmap(profilePicture, x, y, this.defaultPaintCircle);
                } else {
                    // no profile picture available for this user (yet)
                    canvas.drawCircle(x, y, DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                }

                ////////////////////////////////////////
                // Draw name above profile / circle
                ////////////////////////////////////////
                canvas.drawText(target.getUserName(),
                        x - (NAME_WIDTH_OFFSET * target.getUserName().length() / 2),
                        y - NAME_HEIGHT_OFFSET, this.defaultPaintCircle);

                ////////////////////////////////////////////////
                // Draw Guide artefact in general direction
                ////////////////////////////////////////////////
                if (x < 0 && y < 0) {
                    drawGuide(Direction.TOP_LEFT, canvas, x, y);
                } else if (x > canvas.getWidth() && y < 0) {
                    drawGuide(Direction.TOP_RIGHT, canvas, x, y);
                } else if (x < 0 && y > canvas.getHeight()) {
                    drawGuide(Direction.BTM_LEFT, canvas, x, y);
                } else if (x > canvas.getWidth() && y > canvas.getHeight()) {
                    drawGuide(Direction.BTM_RIGHT, canvas, x, y);
                } else if (x < 0) {
                    drawGuide(Direction.LEFT, canvas, x, y);
                } else if (x > canvas.getWidth()) {
                    drawGuide(Direction.RIGHT, canvas, x, y);
                } else if (y < 0) {
                    drawGuide(Direction.UP, canvas, x, y);
                } else if (y > canvas.getHeight()) {
                    drawGuide(Direction.DOWN, canvas, x, y);
                }

            } else {

                ////////////////////////////////////////////////
                // Draw Guide artefact in general direction
                ////////////////////////////////////////////////
                if (x > 0) {
                    drawGuide(Direction.LEFT, canvas, x, y);
                } else {
                    drawGuide(Direction.RIGHT, canvas, x, y);
                }
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
        float x = (OFFSET + cameraSpace[X] / cameraSpace[W]) * width;
        float y = (OFFSET - cameraSpace[Y] / cameraSpace[W]) * height;
        return new float[]{x, y};
    }

    private void drawGuide(Direction direction, Canvas canvas, float x, float y) {
        if (x < 0 || x > canvas.getWidth()) {
            x = x < 0 ? GUIDE_OFFSET : canvas.getWidth() - GUIDE_OFFSET;
        }
        if (y < 0 || y > canvas.getHeight()) {
            y  = y < 0 ? GUIDE_OFFSET : canvas.getHeight() - GUIDE_OFFSET;
        }
        switch (direction) {
            case UP:
                canvas.drawCircle(x, GUIDE_OFFSET, DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                break;
            case DOWN:
                canvas.drawCircle(x, canvas.getHeight() - GUIDE_OFFSET, DEFAULT_CIRCLE_RADIUS,
                        this.defaultPaintCircle);
                break;
            case LEFT:
                canvas.drawCircle(GUIDE_OFFSET, y, DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                break;
            case RIGHT:
                canvas.drawCircle(canvas.getWidth() - GUIDE_OFFSET, y,
                        DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                break;
            case TOP_LEFT:
                canvas.drawCircle(GUIDE_OFFSET, GUIDE_OFFSET, DEFAULT_CIRCLE_RADIUS,
                        this.defaultPaintCircle);
                break;
            case TOP_RIGHT:
                canvas.drawCircle(canvas.getWidth() - GUIDE_OFFSET,
                        GUIDE_OFFSET, DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                break;
            case BTM_LEFT:
            canvas.drawCircle(GUIDE_OFFSET,
                    canvas.getHeight() - GUIDE_OFFSET,
                    DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                break;
            case BTM_RIGHT:
                canvas.drawCircle(canvas.getWidth() - GUIDE_OFFSET,
                        canvas.getHeight() - GUIDE_OFFSET,
                        DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
                break;
        }
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
