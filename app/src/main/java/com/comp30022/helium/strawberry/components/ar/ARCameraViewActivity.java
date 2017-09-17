package com.comp30022.helium.strawberry.components.ar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import eu.kudan.kudan.ARActivity;

public class ARCameraViewActivity extends ARActivity {
    private static final String KUDAN_AR_TAG = "KUDAN AR::: ";
    private static final int CAMERA_PERM = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!KudanSetup.setupKudan()) {
            Log.d(KUDAN_AR_TAG, "Failed to verify API key!");
        }
        getCameraPermission();
    }

    @Override
    public void setup() {
        super.setup();
    }


    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    CAMERA_PERM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // successfully got camera permission from user!
                } else {
                    Toast.makeText(getApplicationContext(), "Camera permission is required for" +
                            " this augmented reality functionality.", Toast.LENGTH_LONG).show();
                    getCameraPermission();
                }
        }
    }


}
