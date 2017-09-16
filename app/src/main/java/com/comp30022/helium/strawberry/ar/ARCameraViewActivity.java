package com.comp30022.helium.strawberry.ar;

import android.os.Bundle;
import android.util.Log;


import com.comp30022.helium.strawberry.services.LocationService;

import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class ARCameraViewActivity extends ARActivity {
    private static final String KUDAN_AR_TAG = ARCameraViewActivity.class.getSimpleName();
    private ARArrowManager arrowManager;
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = LocationService.getInstance();
        if (!KudanSetup.setupKudan()) {
            Log.d(KUDAN_AR_TAG, "Failed to verify API key!");
        }
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

        this.arrowManager = new ARArrowManager(null, arrowModelNode, locationService);
        // this will point the arrow "forwards"
        this.arrowManager.init();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.locationService.onResume();
    }

    @Override
    public void onPause() {
        super.onResume();
        this.locationService.onPause();
    }

}
