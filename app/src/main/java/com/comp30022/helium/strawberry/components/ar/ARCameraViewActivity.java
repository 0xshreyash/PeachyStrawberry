package com.comp30022.helium.strawberry.components.ar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.entities.User;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class ARCameraViewActivity extends ARActivity implements SensorEventListener {
    private static final String KUDAN_AR_TAG = ARCameraViewActivity.class.getSimpleName();
    private ARArrowManager arrowManager;
    private LocationService locationService;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = LocationService.getInstance();
        if (!KudanSetup.setupKudan()) {
            Log.d(KUDAN_AR_TAG, "Failed to verify API key!");
        }
        this.sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void setup() {
        super.setup();

        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();

        ARModelImporter arModelImporter = new ARModelImporter();
        arModelImporter.loadFromAsset("narrow.armodel");
        ARModelNode arrowModelNode = arModelImporter.getNode();

        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("target.png");

        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setAmbient(.8f, .8f, .8f);

        for (ARMeshNode m : arrowModelNode.getMeshNodes()) {
            m.setMaterial(material);
        }
        arrowModelNode.scaleByUniform(100f);

        this.getARView().getCameraViewPort().getCamera().addChild(arrowModelNode);
        gyroPlaceManager.getWorld().addChild(arrowModelNode);

        this.arrowManager = new ARArrowManager(this, User.getUser("testId", "testuser"), arrowModelNode, locationService);
        // this will point the arrow "forwards"
        this.arrowManager.init();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationService.onResume();
        Sensor mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationService.onPause();
        sensorManager.unregisterListener(this);
    }

    // TODO: remove, this is for debugging only
    public void debugMessage(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (arrowManager != null) {
            arrowManager.sensorChanged(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
