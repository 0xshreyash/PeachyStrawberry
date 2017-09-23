package com.comp30022.helium.strawberry.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.ar.ARCameraViewActivity;
import com.comp30022.helium.strawberry.components.location.LocationService;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Main acts as googleApiClient, for now
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

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
        Intent intent = new Intent(this, MapFragmentTestActivity.class);
        //TODO: pass friend tracking here
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
}
