package com.comp30022.helium.strawberry.components.ar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.ar.helper.CanvasDrawerLogic;
import com.comp30022.helium.strawberry.components.ar.helper.CoordinateConverter;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.helpers.ColourScheme;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ARRenderer extends View implements View.OnTouchListener {
    // max number of profile picture waits to do
    private static final int MAX_BOTTLE_NECK = 50;
    private static final int VIBRATE_MS = 100;
    // maximum threshold for a finger touch event (200 screen units ~ roughly the large
    // profile pic size)
    private static final int NEAREST_DISTANCE_THRESHOLD = 200;

    private float[] projectionMatrix;
    private static final String TAG = ARRenderer.class.getSimpleName();
    private Set<ARTrackerBeacon> trackers;
    private Set<ARTrackerBeacon> copyOftrackers;
    private Location currentLocation;
    private Vibrator vibrator;
    // index values for camera coordinates float[]{x,y,z,w}
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int W = 3;

    private ARActivity arActivity;
    private CanvasDrawerLogic canvasDrawer;

    private ProgressBar progressBar;
    private TextView loadingText;
    private boolean loading;
    private int profilePictureBottleNeckCount;


    public enum Direction {
        UP, DOWN, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BTM_LEFT, BTM_RIGHT
    }


    public ARRenderer(Context context, ConstraintLayout container, Vibrator vibrator) {
        super(context);
        // this is dangerous, but we're sure that only ARActivity is using this ARRenderer for now
        this.arActivity = (ARActivity) context;
        this.trackers = new HashSet<>();
        this.copyOftrackers = new HashSet<>();
        this.currentLocation = LocationService.getInstance().getDeviceLocation();
        this.progressBar = (ProgressBar) container.findViewById(R.id.arwait);
        this.loadingText = (TextView) container.findViewById(R.id.ar_load_msg);
        this.canvasDrawer = new CanvasDrawerLogic();
        this.vibrator = vibrator;
        setOnTouchListener(this);
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
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();
        handleOnTouch(x, y);
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//        }
        return false;
    }

    private void handleOnTouch(int x, int y) {
        ARTrackerBeacon nearestBeacon = null;
        ARTrackerBeacon currentActiveNode = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (ARTrackerBeacon beacon : trackers) {
            double dist = beacon.distanceTo(x, y);
            if (dist < nearestDistance) {
                nearestBeacon = beacon;
                nearestDistance = dist;
            }
            if (beacon.isActive()) {
                currentActiveNode = beacon;
            }
        }
        if (nearestBeacon == null || nearestDistance > NEAREST_DISTANCE_THRESHOLD) return;

        // if we touch the current user, remove ALL except current user
        if (nearestBeacon.equals(currentActiveNode)) {
            Log.i(TAG, "You touched the current user");

            // if current trackers has more values, we back it up first, then we wipe them
            // i.e. only focus on currently tapped user
            if (trackers.size() > 1) {
                Log.w(TAG, "Focusing on target tapped only");
                this.vibrator.vibrate(VIBRATE_MS);
                copyOftrackers.addAll(trackers);
                trackers.clear();
            } else {
                // else, current tracker is cleared, refill it
                // i.e. we return all the wiped users
                Log.w(TAG, "Readding all saved targets");
                trackers.addAll(copyOftrackers);
                copyOftrackers.clear();
            }
            // either way, we're focusing on this current node
            trackers.add(currentActiveNode);
        // else, we set the new dude as the active beacon we're tracking
        } else {
            Log.i(TAG, "You touched " + nearestBeacon.getUserName());
            nearestBeacon.setActive(true);
            if (currentActiveNode != null) {
                currentActiveNode.setActive(false);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (loadIfInsufficientData()) return;

        // for each tracker beacon, we draw them on screen if they're in positive Z axis (i.e.
        // in front of us), else we render a guide pointing towards the target on the edges of the
        // screen.
        for (ARTrackerBeacon target : trackers) {
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

            target.setXY(screenCoordinates[X], screenCoordinates[Y]);

            if (target.isActive()) {
                // this happens if you're exactly at the target's location because the difference
                // between you and target's ENU coordinate is 0
                if (Float.isNaN(target.getX()) || Float.isNaN(target.getY())) {
                    this.arActivity.displayInfoHUD("You have arrived at " + target.getUserName()
                            + "'s location");
                } else {
                    writeDistanceTo(target);
                }
            }

            // if the point is in front of us ==> i.e. we should render it!
            if (cameraCoordinates[Z] > 0) {
                if (target.isActive()) {
                    // draw it in LARGE size
                    canvasDrawer.drawProfilePicture(canvas, target);
                    // Draw name above profile / circle
                    canvasDrawer.drawName(canvas, target.getUserName(),
                            target.getX(), target.getY());
                    // if the x and y will not be seen in screen, render the guide instead!
                    canvasDrawer.deduceGuide(canvas, target);
                } else {
                    // BEGIN TRANSACTION
                    // draw it in NORMAL size
                    User.ProfilePictureType oldSize = target.getSize();
                    target.setSize(User.ProfilePictureType.NORMAL);
                    canvasDrawer.drawProfilePicture(canvas, target);
                    target.setSize(oldSize);
                    // END TRANSACTION
                    // don't bother drawing name on someone UNSELECTED
                }
            } else {
                // Draw Guide artefact in general direction
                if (target.isActive()) {
                    if (target.getX() > 0) {
                        canvasDrawer.drawGuide(Direction.LEFT, canvas, target);
                    } else {
                        canvasDrawer.drawGuide(Direction.RIGHT, canvas, target);
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private boolean loadIfInsufficientData() {
        boolean load = false;
        for (ARTrackerBeacon tracker : trackers) {
            if (!tracker.finishLoading()) {
                load = true;
                this.profilePictureBottleNeckCount++;
                break;
            }
        }

        // don't spend forever waiting for that one profile picture, just render a dot.
        if (this.profilePictureBottleNeckCount >= MAX_BOTTLE_NECK) {
            load = false;
        }

        // if we have insufficient data, show the loading screen
        if (load || currentLocation == null || this.projectionMatrix == null) {
            // if we aren't already showing the loading screen, show it
            if (!this.loading) {
                this.arActivity.displayInfoHUD("Loading...");
                this.progressBar.setVisibility(View.VISIBLE);
                this.loadingText.setText("Gathering virtual strawberries...");
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
}