package com.comp30022.helium.strawberry.ar;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class ARCameraViewActivity extends ARActivity {
    private static final String KUDAN_AR_TAG = "KUDAN AR::: ";
    private ARArrowManager arrowManager;
    private Handler handler;
    private Runnable arrowUpdater;
    private final int INTERVAL = 1000;  // refresh every one second


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KudanSetup.setupKudan()) {
            Log.d(KUDAN_AR_TAG, "Failed to verify API key!");
        }
        this.handler = new Handler();
        this.arrowUpdater = new Runnable() {
            @Override
            public void run() {
                arrowManager.update();
                ARCameraViewActivity.this.handler.postDelayed(arrowUpdater, INTERVAL);
            }
        };
        this.handler.postDelayed(arrowUpdater, INTERVAL);
    }

    @Override
    public void setup() {
        super.setup();

        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();

        ARModelImporter arModelImporter = new ARModelImporter();
        arModelImporter.loadFromAsset("narrow.armodel");
        ARModelNode modelNode = arModelImporter.getNode();

        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("target.png");

        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setAmbient(.8f, .8f, .8f);

        for (ARMeshNode m : modelNode.getMeshNodes()) {
            m.setMaterial(material);
        }
        modelNode.scaleByUniform(100f);

        this.getARView().getCameraViewPort().getCamera().addChild(modelNode);
        gyroPlaceManager.getWorld().addChild(modelNode);

        this.arrowManager = new ARArrowManager(null, null, modelNode);
        // this will point the arrow "forwards"
        this.arrowManager.init();
    }



}
