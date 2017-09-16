package com.comp30022.helium.strawberry;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;


import com.comp30022.helium.strawberry.entities.Friend;
import com.comp30022.helium.strawberry.patterns.Publisher;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.services.LocationService;
import com.comp30022.helium.strawberry.services.MockLocationServices;
import com.comp30022.helium.strawberry.services.NotInstantiatedException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


import static com.comp30022.helium.strawberry.R.id.info;

/**
 * Created by noxm on 19/08/17.
 */

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, Subscriber<Location> {
    private static final String TAG = MapViewActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationService mLocationService;
    private ArrayList<Marker> markerList;
    private Marker lastMarker = null;
    private Boolean initCamera = true;


    // TODO: REMOVE
    private int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            mLocationService = LocationService.getInstance();
        } catch (NotInstantiatedException e) {
            Log.d(TAG, e.toString());
        }
        mLocationService.registerSubscriber(this);
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
        markerList = new ArrayList<Marker>();
        LatLng uh = new LatLng(mock.getCoordinate(friend).getY(), mock.getCoordinate(friend).getX());
        Marker dest = mMap.addMarker(new MarkerOptions().position(uh).title("Marker in Union House"));
        markerList.add(dest);
        Location currentLocation = mLocationService.getDeviceLocation();
        newMarkers(currentLocation);


//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void newMarkers(Location location) {
        double lat, lon;
        lat = location.getLatitude();
        lon = location.getLongitude();

        LatLng curr = new LatLng(lat, lon);

        if (lastMarker == null) {
            Marker user = mMap.addMarker(new MarkerOptions().position(curr).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_circle)));
            markerList.add(user);
            lastMarker = user;
        } else {
            lastMarker.setPosition(curr);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        if(initCamera) {
            mMap.animateCamera(cu);
            initCamera = false;
        }

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

    @Override
    public void update(Location info) {
        newMarkers(info);
    }
}
