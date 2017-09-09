package com.comp30022.helium.strawberry;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.comp30022.helium.strawberry.entities.Friend;
import com.comp30022.helium.strawberry.services.Coordinate;
import com.comp30022.helium.strawberry.services.MockLocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by noxm on 19/08/17.
 */

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = MapViewActivity.class.getSimpleName();
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "abc";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap;
    private TextView info;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double lat, lon;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        info = (TextView) findViewById(R.id.info);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            info.setText(R.string.google_api);
        }
        createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MockLocationServices mock = null;
        mock.getInstance();
        Friend friend = null;
//         Add a marker in Sydney and move the camera
        LatLng uh = new LatLng(mock.getCoordinate(friend).getY(), mock.getCoordinate(friend).getX());
        mMap.addMarker(new MarkerOptions().position(uh).title("Marker in Union House"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uh));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.animateCamera(zoom);

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    public void setNewLocation(Location location) {
        info.setText(R.string.connection_success);
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d(TAG, "location: " + lat + " " + lon);
        LatLng curr = new LatLng(lat, lon);
//        lat = Math.min(mock.getCoordinate(friend).getY(), currLocation.getLatitude()) -
//                        Math.abs(mock.getCoordinate(friend).getY() - currLocation.getLatitude())/2;
//        lon = Math.min(mock.getCoordinate(friend).getX(), currLocation.getLongitude()) +
//                Math.abs(mock.getCoordinate(friend).getX() - currLocation.getLongitude())/2;
//        LatLng mid = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(curr).title("Marker in current location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
//
//        mMap.animateCamera(zoom);
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            setNewLocation(mLastLocation);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            info.setText(R.string.location_null);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        info.setText(R.string.connection_suspended);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        info.setText(R.string.connection_failed);
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Intent intent = new Intent(this, ErrorActivity.class);
                intent.putExtra("error", e.getMessage());
                startActivity(intent);
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setNewLocation(location);
    }
}
