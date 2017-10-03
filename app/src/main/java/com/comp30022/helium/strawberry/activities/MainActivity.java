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
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Subscriber<Boolean> {
    private static final String TAG = "MainActivity";

    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;

    private boolean added = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if we actually have permission
        checkPermission();

        // wait for rest interface to authorize
        setContentView(R.layout.splash);
        String token = StrawberryApplication.getString("token");
        if (token != null) {
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
    }

    private void autoAddFriends() {
        added = true;
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject();
                            JSONArray friends = new JSONArray(jsonObject.get("data").toString());

                            for (int i = 0; i < friends.length(); i++) {
                                JSONObject friend = new JSONObject(friends.get(i).toString());
                                String id = friend.get("id").toString();
                                Log.d(TAG, "Friend " + id);
                                PeachServerInterface.getInstance().addFriendFbId(id);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
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

    public void goToFriendSelection(View view) {
        Intent intent = new Intent(this, FriendListTestActivity.class);
        startActivity(intent);
    }

    public void goToChat(View view) {
        Intent intent = new Intent(this, ChatTestActivity.class);
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
     *
     * @param restInit
     */
    @Override
    public void update(Boolean restInit) {
        if (!restInit) {
            Toast toast = Toast.makeText(this, "Failed to authorize with existing token.", Toast.LENGTH_SHORT);
            toast.show();
            backToStart();
        } else {
            // successful, gurantee that we have permissions
            mLocationService = new LocationService();
            mLocationService.setup(mGoogleApiClient);

            if (!added) {
                autoAddFriends();
            }
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

