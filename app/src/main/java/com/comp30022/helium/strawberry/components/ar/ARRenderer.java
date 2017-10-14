package com.comp30022.helium.strawberry.components.ar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.ar.helper.CoordinateConverter;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationService;
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
    private User.ProfilePictureType profilePictureType = User.ProfilePictureType.LARGE;
    private boolean drawName = true;
    // index values for camera coordinates float[]{x,y,z,w}
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int W = 3;

    // offset for the username height
    private static final int NAME_HEIGHT_OFFSET = 70;
    // offset for the username width
    private static final int NAME_WIDTH_OFFSET = 7;
    // When the user has no profile picture/callback hasn't returned, we render a temporary
    // circle with this radius as replacement for the profile picture
    private static final int DEFAULT_CIRCLE_RADIUS = 30;
    // offset for the guide artefact
    private static final int GUIDE_OFFSET = 31;
    // when drawing the guide, offset the image away from the arrow
    private static final int IMAGE_OFFSET = 100;

    /* The following offsets are for visual treats, they make the cropped profile picture align well
            with the guide arrow. The values are determined by eye. */
    private static final int LEFT_IMAGE_OFFSET = 60;
    private static final int RIGHT_IMAGE_OFFSET = 30;
    private static final int BTM_LEFT_IMAGE_OFFSET = RIGHT_IMAGE_OFFSET;
    private static final int BTM_IMAGE_OFFSET = 50;
    private static final int TOP_LEFT_IMAGE_OFFSET = 60;
    private static final int BTM_RIGHT_IMAGE_OFFSET = 30;

    private ARActivity arActivity;
    private Paint namePaint;
    private Paint arrowPaint;
    private Paint profilePicturePaint;

    private ProgressBar progressBar;
    private TextView loadingText;
    private boolean loading;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BTM_LEFT, BTM_RIGHT
    }


    public ARRenderer(Context context, ConstraintLayout container) {
        super(context);
        // this is dangerous, but we're sure that only ARActivity is using this ARRenderer for now
        this.arActivity = (ARActivity) context;
        this.trackers = new ArrayList<>();
        this.currentLocation = LocationService.getInstance().getDeviceLocation();
        this.progressBar = (ProgressBar) container.findViewById(R.id.arwait);
        this.loadingText = (TextView) container.findViewById(R.id.ar_load_msg);
        setupArrowPaint();
        setupNamePaint();
        setupProfilePicturePaint();
    }

    public void addTracker(ARTrackerBeacon tracker) {
        // add to the list of tracking points we want to track and render on screen
        this.trackers.add(tracker);
    }

    public void updateProjectionMatrix(float[] newProjectionMatrix) {
        this.projectionMatrix = Arrays.copyOf(newProjectionMatrix, newProjectionMatrix.length);
        // re-draw the points
        this.invalidate();
    }

    public void updateLocation(LocationEvent locationEvent) {
        if (locationEvent.getKey().equals(PeachServerInterface.currentUser())) {
            // it's this device's location's update
            Log.i(TAG, "You updated location to" + locationEvent.getValue());
            this.currentLocation = new Location(locationEvent.getValue());
        } else {
            // find the tracking point and update that point's location
            User updatedUserLocation = locationEvent.getKey();
            // loop through all our tracking targets and find the user that corresponds to this
            // update. Once found, update the beacon's location and break off this loop
            for (ARTrackerBeacon trackerBeacon : trackers) {
                if (trackerBeacon.getUser().equals(updatedUserLocation)) {
                    trackerBeacon.updateLocation(locationEvent.getValue());
                    Log.i(TAG, "Friend updated location to" + locationEvent.getValue());
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
        if (loadIfInsufficientData()) return;

        // for each tracker beacon, we draw them on screen if they're in positive Z axis (i.e.
        // in front of us), else we render a guide pointing towards the target on the edges of the
        // screen.
        for (int i = 0; i != trackers.size(); ++i) {
            ARTrackerBeacon target = trackers.get(i);
            // convert from : GPS -> ENU
            float[] ENUCoordinates = CoordinateConverter.getENU(target.getLocation(),
                    this.currentLocation);
            // convert from : ENU -> Camera
            float[] cameraCoordinates = CoordinateConverter.convertToCameraSpace(ENUCoordinates,
                    this.projectionMatrix);

            float[] screenCoordinates = CoordinateConverter.convertToScreenSpace(
                    cameraCoordinates,
                    canvas.getWidth(),
                    canvas.getHeight());
            float x = screenCoordinates[X];
            float y = screenCoordinates[Y];

            // this happens if you're exactly at the target's location because the difference
            // between you and target's ENU coordinate is 0
            if (Float.isNaN(x) || Float.isNaN(y)) {
                this.arActivity.displayInfoHUD("You have arrived at " + target.getUserName()
                        + "'s location");
            } else {
                writeDistanceTo(target);
            }

            // if the point is in front of us ==> i.e. we should render it!
            if (cameraCoordinates[Z] > 0) {
                Bitmap profilePicture = target.getProfilePicture(this, this.profilePictureType);
                if (profilePicture != null) {
                    canvas.drawBitmap(profilePicture, x, y, this.profilePicturePaint);
                } else {
                    // no profile picture available for this user (yet)
                    canvas.drawCircle(x, y, DEFAULT_CIRCLE_RADIUS, this.namePaint);
                }

                ////////////////////////////////////////
                // Draw name above profile / circle
                ////////////////////////////////////////
                if (this.drawName)
                    canvas.drawText(target.getUserName(),
                            x - (NAME_WIDTH_OFFSET * target.getUserName().length() / 2),
                            y - NAME_HEIGHT_OFFSET, this.namePaint);

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

    @SuppressLint("SetTextI18n")
    private boolean loadIfInsufficientData() {
        // if we have insufficient data, show the loading screen
        if (currentLocation == null || this.projectionMatrix == null) {
            // if we aren't already showing the loading screen, show it
            if (!this.loading) {
                this.arActivity.displayInfoHUD("Loading...");
                this.progressBar.setVisibility(View.VISIBLE);
                this.loadingText.setText("Gathering the sweetest strawberries...");
                this.loadingText.setTextColor(ColourScheme.PRIMARY_DARK);
                this.loadingText.bringToFront();
                this.progressBar.bringToFront();
                this.loading = true;
            }
            // otherwise, the spinner and text should already be there
            return true;
        }
        // if was loading but now we have enough data, just make the loading screen disappear
        if (this.loading) {
            this.loading = false;
            this.progressBar.setVisibility(View.INVISIBLE);
            this.loadingText.setText("");
        }
        // it's NOT true that we don't have enough data.
        return false;
    }

    private void writeDistanceTo(ARTrackerBeacon target) {
        double distanceTo = target.getLocation().distanceTo(currentLocation);
        String unit = "m";
        // if we're > 1km, convert m to km
        if (distanceTo > 1000) {
            distanceTo /= 1000;
            unit = "km";
        }
        @SuppressLint("DefaultLocale")
        String formatted = String.format("%.2f%s away from %s", distanceTo,
                unit, target.getUserName());
        this.arActivity.displayInfoHUD(formatted);
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
        final String arrow = "<";
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
                yOffset = -(IMAGE_OFFSET + BTM_IMAGE_OFFSET);
                xOffset = -BTM_IMAGE_OFFSET;
                break;
            case LEFT:
                dx = GUIDE_OFFSET;
                dy = y;
                rotation = 0;
                xOffset = IMAGE_OFFSET;
                yOffset = -LEFT_IMAGE_OFFSET;
                break;
            case RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = y;
                rotation = 180;
                xOffset = -(IMAGE_OFFSET + RIGHT_IMAGE_OFFSET);
                break;
            case TOP_LEFT:
                dx = GUIDE_OFFSET;
                dy = GUIDE_OFFSET;
                rotation = -45;
                xOffset = IMAGE_OFFSET - TOP_LEFT_IMAGE_OFFSET / 2;
                yOffset = IMAGE_OFFSET - TOP_LEFT_IMAGE_OFFSET;
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
                xOffset = IMAGE_OFFSET - BTM_LEFT_IMAGE_OFFSET * 2;
                yOffset = -IMAGE_OFFSET - BTM_LEFT_IMAGE_OFFSET;
                break;
            case BTM_RIGHT:
                dx = canvas.getWidth() - GUIDE_OFFSET;
                dy = canvas.getHeight() - GUIDE_OFFSET;
                rotation = 135;
                xOffset = -(IMAGE_OFFSET + BTM_RIGHT_IMAGE_OFFSET);
                yOffset = -IMAGE_OFFSET + BTM_RIGHT_IMAGE_OFFSET;
                break;
        }
        canvas.save();
        // negative rotation because android uses positive clockwise system
        canvas.rotate(-rotation, dx, dy);
        canvas.drawText(arrow, dx, dy, this.arrowPaint);
        canvas.restore();

        Bitmap profilePicture = target.getProfilePicture(this, User.ProfilePictureType.SMALL);
        if (profilePicture != null) {
            canvas.drawBitmap(profilePicture, dx + xOffset, dy + yOffset, this.profilePicturePaint);
        } else {
            // no profile picture available for this user (yet) - draw a dot
            canvas.drawCircle(dx + xOffset, dy + yOffset, DEFAULT_CIRCLE_RADIUS, this.namePaint);
        }
    }

    public void setProfilePictureSize(User.ProfilePictureType size) {
            this.profilePictureType = size;
    }

    public void setDisplayName(boolean bool) {
        this.drawName = bool;
    }

    /**
     * Username's paint style
     */
    private void setupNamePaint() {
        this.namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.namePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.namePaint.setColor(ColourScheme.PRIMARY_DARK);
        this.namePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        this.namePaint.setTextSize(60);
    }

    /**
     * Guide arrow's paint style
     */
    private void setupArrowPaint() {
        // set default arrow paint
        this.arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.arrowPaint.setStyle(Paint.Style.FILL);
        this.arrowPaint.setColor(ColourScheme.PRIMARY_DARK);
        this.arrowPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        this.arrowPaint.setTextSize(120);
    }

    private void setupProfilePicturePaint() {
        this.profilePicturePaint = new Paint();
    }
}
