package com.comp30022.helium.strawberry.components.ar.helper;


import android.hardware.Camera;
import android.util.Log;

public class CameraHelper {

    private static final String TAG = CameraHelper.class.getSimpleName();
    public static Camera getCamera() {
        try {
            Camera camera = Camera.open();
            return camera;
        } catch (Exception e) {
            Log.e(TAG, "ERROR: Can't instantiate camera -- " + e.getMessage());
            return null;
        }
    }
}
