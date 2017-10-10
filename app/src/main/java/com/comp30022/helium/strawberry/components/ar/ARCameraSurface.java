package com.comp30022.helium.strawberry.components.ar;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.comp30022.helium.strawberry.components.ar.helper.CameraHelper;

import java.io.IOException;


public class ARCameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = ARCameraSurface.class.getSimpleName();
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private int width, height;
    private float[] projectionMatrix = new float[16];
    private Activity context;

    public ARCameraSurface(Context context) {
        super(context);
        this.context = (Activity) context;
        this.camera = CameraHelper.getCamera();

        // add callback to know when this surface is created/destroyed
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // surface has been created, tell the camera to render preview
        if (this.camera == null) {
            this.camera = CameraHelper.getCamera();
        }
        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "ERROR: Can't set camera preview -- " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (this.camera != null) {
            this.width = width;
            this.height = height;

            setDisplayOrientation();

            // recalculate projection matrix since the width and height changed
            calculateCameraProjectionMatrix();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (this.camera != null) {
            this.camera.setPreviewCallback(null);
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
        }
    }

    public float[] getProjectionMatrix() {
        return this.projectionMatrix;
    }

    private void calculateCameraProjectionMatrix() {
        float ratio = (float) this.width / this.height;
        Matrix.frustumM(this.projectionMatrix, 0, -ratio, ratio, -1, 1, .5f, 2000);
    }

    /**
     * ADAPTED FROM: https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
     *
     * This code sets the orientation of the camera to the original orientation of the activity
     */
    private void setDisplayOrientation() {
        int id = findActiveCamera();
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        int rotation = this.context.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        this.camera.setDisplayOrientation(result);
    }


    /**
     * returns the id of the active camera (the back camera)
     * @return
     */
    private int findActiveCamera() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return -1;
    }

}
