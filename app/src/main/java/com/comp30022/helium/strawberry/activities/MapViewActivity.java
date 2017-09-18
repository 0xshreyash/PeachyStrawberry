package com.comp30022.helium.strawberry.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.comp30022.helium.strawberry.patterns.Subscriber;

import com.comp30022.helium.strawberry.entities.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.util.Log;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.location.LocationServiceActivity;
import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noxm on 19/08/17.
 */

public class MapViewActivity extends LocationServiceActivity implements OnMapReadyCallback {
    private static final String TAG = MapViewActivity.class.getSimpleName();
    private StrawberryMap map;

    @Override
    protected void onCreateAction(Bundle savedInstanceState) {
        // Google Map
        setContentView(R.layout.activity_map_view);
        // Obtain the SupportMapFragment and getString notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = new StrawberryMap(googleMap);

        // TODO: update with real friend object
        User friend = new User();

        // init locations
        Location friendLoc = locationService.getUserLocation(friend);
        map.updateMarker("friendLocation", "Friend Location", friendLoc);

        Location currentLocation = locationService.getDeviceLocation();
        map.updateMarker("currentLocation", "You are here", currentLocation);

        // move camera
        List<Location> locations = new ArrayList<>();
        locations.add(friendLoc);
        locations.add(currentLocation);
        map.moveCamera(locations, 200);
    }

    @Override
    protected void onResumeAction() {
        Log.d(TAG, "Resume");
    }

    @Override
    protected void onPauseAction() {
        Log.d(TAG, "Pause");
    }

    @Override
    /*
    TODO: What if the location is friend's location?
          Friend1, Friend2?
     */
    public void update(Location currentLocation) {
        if (map != null) {
            map.updateMarker("currentLocation", "You are here", currentLocation);
            map.updatePath("currentLocation", "friendLocation");
        } else {
            Log.e(TAG, "Map has not been initialized yet, ditching new location update");
        }
    }
}

