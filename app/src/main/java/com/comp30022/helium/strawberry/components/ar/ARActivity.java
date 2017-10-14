package com.comp30022.helium.strawberry.components.ar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.helpers.ColourScheme;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import java.util.List;


public class ARActivity extends AppCompatActivity implements SensorEventListener,
        Subscriber<LocationEvent> {
    private static final String TAG = ARActivity.class.getSimpleName();
    private static final int MAX_DISP_MARKER = 5;
    private ARCameraSurface cameraSurface;
    private TextView infoHUD;
    private FrameLayout container;
    private SensorManager sensorManager;
    private ARRenderer arRenderer;
    private float[] sensorVectorRotationMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] rotatedMatrix = new float[16];
    private LocationService locationService = LocationService.getInstance();
    private boolean displayOverride;
    private boolean hadBadSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        this.cameraSurface = new ARCameraSurface(this);
        // container that contains the camera preview
        this.container = (FrameLayout) findViewById(R.id.camera_frame_layout);
        // the text box to display information (if needed)
        this.infoHUD = (TextView) findViewById(R.id.info_HUD);

        // get sensor manager
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        // init a new ARRenderer for AR display overlay
        this.arRenderer = new ARRenderer(this, (ConstraintLayout) findViewById(R.id.ar_home));
        // add currently selected user to track
        trackSelectedUser();

        // bind camera to container
        this.container.addView(this.cameraSurface);
        // bind ARRenderer to container
        this.container.addView(this.arRenderer);

        // registers to location service so that we will receive updates
        locationService.registerSubscriber(this);

        // keep the screen from dimming!
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // make pretty text colours
        setupInfoHUD();

    }

    /**
     * If anyone wants to display text in infoHUD, call this method
     *
     * @param text
     */
    public void displayInfoHUD(String text) {
        // if ARActivity didn't request for a display override, let others write what they want.
        if (!this.displayOverride) {
            this.infoHUD.setText(text);
        }
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
                this.infoHUD.setText(" Sensor low accuracy, follow these steps:\n" +
                        "  1. Tilt your phone forward and back\n" +
                        "  2. Move it side to side\n"+
                        "  3. Tilt left and right\n");
                this.infoHUD.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
                this.displayOverride = true;
                this.hadBadSensor = true;
                break;
            default:
                // good accuracy.
                if (this.hadBadSensor)
                    this.displayOverride = false;
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
        this.locationService.deregisterSubscriber(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.sensorManager.unregisterListener(this);
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

    private void setupInfoHUD() {
        this.infoHUD.setTextColor(ColourScheme.SECONDARY_DARK);
    }

    private void trackSelectedUser() {
        String selectedUser = StrawberryApplication.
                getString(StrawberryApplication.SELECTED_USER_TAG);
        if (selectedUser == null) {
            Log.i(TAG, "No targeted user selected globally");
            this.infoHUD.setText("Not tracking anyone. Select a user from the main menu!");
            this.displayOverride = true;
            trackAllTopFriends(MAX_DISP_MARKER);
        } else {
            User targetUser = User.getUser(selectedUser);
            this.arRenderer.addTracker(new ARTrackerBeacon(targetUser, true,
                    User.ProfilePictureType.LARGE));
            // track this user!
            this.locationService.addTracker(targetUser);
            Log.i(TAG, "Tracking: " + selectedUser);
        }
    }

    private void trackAllTopFriends(int top) {
        List<User> friends = StrawberryApplication.getCachedFriends();
        int counter = 0;
        for (User u : friends) {
            this.arRenderer.addTracker(new ARTrackerBeacon(u, false,
                    User.ProfilePictureType.NORMAL));
            this.locationService.addTracker(u);
            Log.i(TAG, "Tracking: " + u);
            counter++;
            if (counter >= top) break;
        }
    }

}
