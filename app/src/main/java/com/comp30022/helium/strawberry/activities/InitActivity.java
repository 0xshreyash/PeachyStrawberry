package com.comp30022.helium.strawberry.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.comp30022.helium.strawberry.R;

import java.util.HashMap;
import java.util.Map;

public class InitActivity extends AppCompatActivity {

    private static final int LOCATION_REQ = 1;
    private static final int CAMERA_REQ = 2;
    private static final String TAG = "PeachInit";
    private Map<Integer, Boolean> permissionMap;

    // this exists just to look pretty on launch
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionMap = new HashMap<>();
        permissionMap.put(LOCATION_REQ, false);
        permissionMap.put(CAMERA_REQ, false);

        setContentView(R.layout.activity_init);

        requestPermission();
    }

    public void checkAndContinue() {
        if (!permissionMap.values().contains(false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            Log.i(TAG, "Some permissions not granted. " + permissionMap.values());
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionMap.put(requestCode, true);
            checkAndContinue();
        } else {
            permissionMap.put(requestCode, false);
            requestPermission();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQ);

            Log.i(TAG, "Location requested");
            return;
        } else {
            permissionMap.put(LOCATION_REQ, true);
            Log.i(TAG, "Location already granted");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_REQ);
            Log.i(TAG, "Camera requested");
            return;
        } else {
            permissionMap.put(CAMERA_REQ, true);
            Log.i(TAG, "Camera already granted");
        }

        checkAndContinue();
    }
}
