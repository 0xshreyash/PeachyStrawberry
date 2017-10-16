package com.comp30022.helium.strawberry.components.ar;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import java.util.List;


public class ARActivity extends AppCompatActivity implements SensorEventListener,
        Subscriber<LocationEvent> {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final int MAX_DISP_MARKER = 5;
    private ARCameraSurface cameraSurface;
    private ARBanner arBanner;
    private FrameLayout container;
    private SensorManager sensorManager;
    private ARRenderer arRenderer;
    private float[] sensorVectorRotationMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] rotatedMatrix = new float[16];
    private LocationService locationService = LocationService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        this.cameraSurface = new ARCameraSurface(this);
        // container that contains the camera preview
        this.container = (FrameLayout) findViewById(R.id.camera_frame_layout);
        // the text box to display information
        this.arBanner = new ARBanner((TextView) findViewById(R.id.info_HUD));

        // get sensor manager
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        // init a new ARRenderer for AR display overlay
        this.arRenderer = new ARRenderer(this, (ConstraintLayout) findViewById(R.id.ar_home),
                (Vibrator)getSystemService(Context.VIBRATOR_SERVICE), this.arBanner);
        // add currently selected user to track
        trackSelectedUser();

        // bind camera to container
        this.container.addView(this.cameraSurface);
        // bind ARRenderer to container
        this.container.addView(this.arRenderer);

        // keep the screen from dimming!
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void update(LocationEvent updatedLocationEvent) {
        this.arRenderer.updateLocation(updatedLocationEvent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.accuracy) {
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                this.arBanner.badSensorDisplay();
                break;
            default:
                this.arBanner.revokeBadSensorDisplay();
                break;
        }

        // only care if this sensor event is from our rotation vector
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(this.sensorVectorRotationMatrix,
                    sensorEvent.values);

            if (this.cameraSurface != null) {
                // get our camera's projection matrix
                this.projectionMatrix = this.cameraSurface.getProjectionMatrix();
            }

            // and multiply it with the rotation matrix
            Matrix.multiplyMM(this.rotatedMatrix, 0, this.projectionMatrix, 0,
                    this.sensorVectorRotationMatrix, 0);

            // tell arRenderer this change so we can re-convert the coordinates
            this.arRenderer.updateProjectionMatrix(this.rotatedMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // nothing to do here
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenToSensors();
        this.locationService.registerSubscriber(this);
        this.locationService.requestMaintainLocationUpdateInterval(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
        this.locationService.deregisterSubscriber(this);
        this.locationService.requestMaintainLocationUpdateInterval(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.sensorManager.unregisterListener(this);
        this.locationService.requestMaintainLocationUpdateInterval(false);
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.locationService.deregisterSubscriber(this);
        this.sensorManager.unregisterListener(this);
    }

    private void listenToSensors() {
        this.sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    private void trackSelectedUser() {
        String selectedUser = StrawberryApplication.
                getString(StrawberryApplication.SELECTED_USER_TAG);
        if (selectedUser == null) {
            Log.i(TAG, "No targeted user selected globally");
            this.arBanner.noTappedUserDisplay();
            trackAllTopFriends(MAX_DISP_MARKER, User.ProfilePictureType.NORMAL);
        } else {
            User targetUser = User.getUser(selectedUser);
            if (targetUser.equals(PeachServerInterface.currentUser())) {
                Log.i(TAG, "No targeted user selected globally");
                this.arBanner.noTappedUserDisplay();
                trackAllTopFriends(MAX_DISP_MARKER, User.ProfilePictureType.NORMAL);
            } else {
                this.arRenderer.addTracker(new ARTrackerBeacon(targetUser, true,
                        User.ProfilePictureType.LARGE));
                // track this user!
                this.locationService.addTracker(targetUser);
                Log.i(TAG, "Tracking: " + selectedUser);
                trackAllTopFriends(MAX_DISP_MARKER, User.ProfilePictureType.NORMAL);
            }
        }
    }


    private void trackAllTopFriends(int top, User.ProfilePictureType ppsize) {
        List<User> friends = StrawberryApplication.getCachedFriends();
        int counter = 0;
        for (User u : friends) {
            this.arRenderer.addTracker(new ARTrackerBeacon(u, false,
                    ppsize));
            this.locationService.addTracker(u);
            Log.i(TAG, "Tracking: " + u);
            counter++;
            if (counter >= top) break;
        }
    }

}
