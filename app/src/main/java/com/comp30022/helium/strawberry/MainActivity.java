package com.comp30022.helium.strawberry;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.comp30022.helium.strawberry.ar.ARCameraViewActivity;
import com.comp30022.helium.strawberry.services.LocationService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private TextView info;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;
    private final static int LOCATION_REQ = 111;
    private final static int CAMERA_REQ = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        info = (TextView) findViewById(R.id.info);

        loginButton = (LoginButton)findViewById(R.id.login_button);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        info.setText(
                                "User ID: "
                                        + loginResult.getAccessToken().getUserId()
                                        + "\n" +
                                        "Auth Token: "
                                        + loginResult.getAccessToken().getToken()
                        );
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        info.setText("Login attempt canceled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
//                        info.setText("Login attempt failed.");
                        info.setText(exception.toString());
                    }
                });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationService = new LocationService();
        mLocationService.setup(mGoogleApiClient);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
        info.setText(R.string.connection_success);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationService.getRequest(), mLocationService);
        if (mLastLocation != null) {
            mLocationService.setNewLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        info.setText(R.string.connection_suspended);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        info.setText(R.string.connection_failed);
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
