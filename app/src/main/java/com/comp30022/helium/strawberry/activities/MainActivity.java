package com.comp30022.helium.strawberry.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.ar.ARCameraViewActivity;
import com.comp30022.helium.strawberry.components.location.LocationService;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;
    private final static int LOCATION_REQ = 111;
    private final static int CAMERA_REQ = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // TODO: Permission grant failure : should not continue down onCreate
        //       This will cause crashes
        // Solution example:
        // create PermissionActivity that is the entrance, requesting permissions
        // then move to MainActivity once all the permissions are granted
        requestPermission();

        setContentView(R.layout.activity_main);

        // Main acts as googleApiClient, for now
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // TODO: setup in LocationServiceInitActivity,
        //       keep the googleAPIclient there
        mLocationService = new LocationService();
        mLocationService.setup(mGoogleApiClient);
    }


    public void goToAR(View view) {
        Intent intent = new Intent(this, ARCameraViewActivity.class);
        startActivity(intent);
    }

    public void goToChat(View view) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    public void goToMap(View view) {
        Intent intent = new Intent(this, MapViewActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
        intent.putExtra("EXTRA_MESSAGE", "some custom message");
        startActivity(intent);
    }

    /**
     * Google API client connected
     *
     * @param bundle
     * @throws SecurityException
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
        Log.i(TAG, "Connection created");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationService.getRequest(), mLocationService);
        if (mLastLocation != null) {
            mLocationService.setNewLocation(mLastLocation);
        }
    }

    /**
     * Google API client suspended
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection suspended with " + i);
    }

    /**
     * Google API client failed
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed with " + connectionResult.getErrorMessage());
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQ);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, CAMERA_REQ);
        }
    }
}
