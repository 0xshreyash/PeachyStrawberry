package com.comp30022.helium.strawberry.components.ar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.View;

import com.comp30022.helium.strawberry.components.ar.helper.CoordinateConverter;
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
    private static final int NAME_WIDTH_OFFSET = 15;
    private static final int DEFAULT_CIRCLE_RADIUS = 30;
    private static final float OFFSET = .5f;
    private static final int GUIDE_OFFSET = 31;      // offset for the guide artefact
    private static final int IMAGE_OFFSET = 100;
    private ARActivity arActivity;
    private Paint defaultPaintCircle;
    private Paint arrowPaint;

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
        this.defaultPaintCircle.setTextSize(60);

        // set default arrow paint
        this.arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.arrowPaint.setStyle(Paint.Style.FILL);
        this.arrowPaint.setColor(ColourScheme.PRIMARY_DARK);
        this.arrowPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        this.arrowPaint.setTextSize(160);
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


        // for each tracker beacon, we draw them on screen if they're in positive Z axis (i.e.
        // in front of us), else we render a guide pointing towards the target on the edges of the
        // screen.
        for (int i = 0; i != trackers.size(); ++i) {
            ARTrackerBeacon target = trackers.get(i);
            // convert from : GPS -> ENU
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
                @SuppressLint("DefaultLocale")
                String formatted = String.format("%.2fm away from %s",
                        target.getLocation().distanceTo(currentLocation), target.getUserName());
                this.arActivity.displayInfoHUD(formatted);
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

                /* ************************************************************************
                 * if the x and y will not be seen in screen, render the guide instead!
                 * ************************************************************************/

                ////////////////////////////////////////////////
                // Draw Guide artefact in general direction
                ////////////////////////////////////////////////
                if (x < 0 && y < 0) {
                    drawGuide(Direction.TOP_LEFT, canvas, target, x, y);
                } else if (x > canvas.getWidth() && y < 0) {
                    drawGuide(Direction.TOP_RIGHT, canvas, target, x, y);
                } else if (x < 0 && y > canvas.getHeight()) {
                    drawGuide(Direction.BTM_LEFT, canvas, target, x, y);
                } else if (x > canvas.getWidth() && y > canvas.getHeight()) {
                    drawGuide(Direction.BTM_RIGHT, canvas, target, x, y);
                } else if (x < 0) {
                    drawGuide(Direction.LEFT, canvas, target, x, y);
                } else if (x > canvas.getWidth()) {
                    drawGuide(Direction.RIGHT, canvas, target, x, y);
                } else if (y < 0) {
                    drawGuide(Direction.UP, canvas, target, x, y);
                } else if (y > canvas.getHeight()) {
                    drawGuide(Direction.DOWN, canvas, target, x, y);
                }

            } else {

                ////////////////////////////////////////////////
                // Draw Guide artefact in general direction
                ////////////////////////////////////////////////
                if (x > 0) {
                    drawGuide(Direction.LEFT, canvas, target, x, y);
                } else {
                    drawGuide(Direction.RIGHT, canvas, target, x, y);
                }
            }
        }
    }

    private float[] getENU(Location targetLocation) {
        float[] deviceLocationECEF = CoordinateConverter.GPS2ECEF(this.currentLocation);
        float[] targetECEF = CoordinateConverter.GPS2ECEF(targetLocation);
        return CoordinateConverter.ECEF2ENU(this.currentLocation,
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

    private void drawGuide(Direction direction, Canvas canvas, ARTrackerBeacon target, float x, float y) {
        if (x < 0 || x > canvas.getWidth()) {
            x = x < 0 ? GUIDE_OFFSET : canvas.getWidth() - GUIDE_OFFSET;
        }
        if (y < 0 || y > canvas.getHeight()) {
            y = y < 0 ? GUIDE_OFFSET : canvas.getHeight() - GUIDE_OFFSET;
        }
        float dx = 0;
        float dy = 0;
        float xOffset = 0;
        float yOffset = 0;
        String arrow = "<";
        float rotation = 0;
        switch (direction) {
            case UP:
                dx = x;
                dy = GUIDE_OFFSET;
                rotation = -90;
                yOffset = IMAGE_OFFSET;
                break;
            case DOWN:
                dx = x;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 90;
                yOffset = -IMAGE_OFFSET;
                break;
            case LEFT:
                dx = GUIDE_OFFSET;
                dy = y;
                rotation = 0;
                xOffset = IMAGE_OFFSET;
                break;
            case RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = y;
                rotation = 180;
                xOffset = -IMAGE_OFFSET;
                break;
            case TOP_LEFT:
                dx = GUIDE_OFFSET;
                dy = GUIDE_OFFSET;
                rotation = -45;
                xOffset = yOffset = IMAGE_OFFSET;
                break;
            case TOP_RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = GUIDE_OFFSET;
                rotation = -135;
                xOffset = -IMAGE_OFFSET;
                yOffset = IMAGE_OFFSET;
                break;
            case BTM_LEFT:
                dx = GUIDE_OFFSET;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 45;
                xOffset = IMAGE_OFFSET;
                yOffset = -IMAGE_OFFSET;
                break;
            case BTM_RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 135;
                xOffset = -IMAGE_OFFSET;
                yOffset = -IMAGE_OFFSET;
                break;
        }

//        String userName = target.getUserName();
//        canvas.drawText(userName,
//                dx - (NAME_WIDTH_OFFSET * userName.length()/2),
//                dy - NAME_HEIGHT_OFFSET,
//                this.defaultPaintCircle);

        canvas.save();
        // - rotation because android uses positive clockwise system
        canvas.rotate(-rotation, dx, dy);
        canvas.drawText(arrow, dx, dy, this.defaultPaintCircle);
        canvas.restore();

        Bitmap profilePicture = target.getProfilePicture(this);
        if (profilePicture != null) {
            canvas.drawBitmap(profilePicture, dx + xOffset, dy + yOffset, this.defaultPaintCircle);
        } else {
            // no profile picture available for this user (yet)
            canvas.drawCircle(dx + xOffset, dy + yOffset, DEFAULT_CIRCLE_RADIUS, this.defaultPaintCircle);
        }
    }

}
