package com.comp30022.helium.strawberry.components.ar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;

import java.util.ArrayList;

public class ARActivity extends AppCompatActivity implements SensorEventListener,
        Subscriber<LocationEvent> {
    private ARCameraSurface cameraSurface;
    private TextView infoHUD;
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
        // the text box to display information (if needed)
        this.infoHUD = (TextView) findViewById(R.id.info_HUD);

        // get sensor manager
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);

        // init a new ARRenderer for AR display overlay
        this.arRenderer = new ARRenderer(this);
        // add currently selected user to track
        ARTrackerBeacon target = new ARTrackerBeacon(new User(StrawberryApplication
                .getString(StrawberryApplication.SELECTED_USER_TAG)));
        this.arRenderer.addTracker(target);

        // bind camera to container
        this.container.addView(this.cameraSurface);
        // bind ARRenderer to container
        this.container.addView(this.arRenderer);

        // registers to location service so that we will receive updates
        locationService.registerSubscriber(this);

        // keep the screen from dimming!
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    /**
     * If anyone wants to display text in infoHUD, call this method
     *
     * @param text
     */
    public void displayInfoHUD(String text) {
        // TODO: better formatting and more options for text/text colour etc
        this.infoHUD.setText(text);
    }

    @Override
    public void update(LocationEvent updatedLocationEvent) {
        this.arRenderer.updateLocation(updatedLocationEvent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        this.locationService.deregisterSubscriber(this);
//        this.sensorManager.unregisterListener(this);
    }

    private void listenToSensors() {
        this.sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }
}
