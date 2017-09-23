package com.comp30022.helium.strawberry.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.ar.ARCameraViewActivity;
import com.comp30022.helium.strawberry.components.location.LocationService;

import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Subscriber<Boolean> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if we actually have permission
        checkPermission();

        // wait for rest interface to authorize
        setContentView(R.layout.splash);
        String token = StrawberryApplication.getString("token");
        if(token != null) {
            PeachServerInterface.init(StrawberryApplication.getString("token"), this);
        } else {
            // need to get token again
            backToStart();
        }

        // Main acts as googleApiClient, for now
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationService = new LocationService();
        mLocationService.setup(mGoogleApiClient);
    }

    // if does not have permission, boot to start
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            backToStart();
        }
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
        backToStart();
    }

    /**
     * get result of rest interface initialization
     * @param restInit
     */
    @Override
    public void update(Boolean restInit) {
        if(!restInit) {
            Toast toast = Toast.makeText(this, "Failed to authorize with existing token.", Toast.LENGTH_SHORT);
            toast.show();
            backToStart();
        } else {
            // successful, gurantee that we have permissions
            setContentView(R.layout.activity_main);
        }
    }

    private void backToStart() {
        Intent intent = new Intent(getApplicationContext(), InitActivity.class);
        LoginManager.getInstance().logOut();
        startActivity(intent);
        this.finish();
    }
}

