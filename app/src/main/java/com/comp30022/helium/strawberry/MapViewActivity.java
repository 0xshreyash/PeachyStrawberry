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
import com.comp30022.helium.strawberry.services.LocationService;
import com.comp30022.helium.strawberry.services.MockLocationServices;
import com.comp30022.helium.strawberry.services.NotInstantiatedException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.comp30022.helium.strawberry.R.id.info;

/**
 * Created by noxm on 19/08/17.
 */

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MapViewActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationService mLocationService;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
//        info = (TextView) findViewById(R.id.info);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            mLocationService = LocationService.getInstance();
        } catch (NotInstantiatedException e) {
            Log.d(TAG, e.toString());
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
        Location currentLocation = mLocationService.getDeviceLocation();
        double lat, lon;
        lat = currentLocation.getLatitude();
        lon = currentLocation.getLongitude();
//         Add a marker in Sydney and move the camera
        LatLng uh = new LatLng(mock.getCoordinate(friend).getY(), mock.getCoordinate(friend).getX());
        mMap.addMarker(new MarkerOptions().position(uh).title("Marker in Union House"));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uh));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.animateCamera(zoom);

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    @Override
    protected void onResume() {
        super.onResume();
        mLocationService.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationService.onPause();
    }
}
