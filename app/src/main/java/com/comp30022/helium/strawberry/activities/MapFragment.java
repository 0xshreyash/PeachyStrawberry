package com.comp30022.helium.strawberry.activities;

import android.location.Location;
import android.os.Bundle;

import com.comp30022.helium.strawberry.entities.User;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.location.LocationServiceFragment;
import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends LocationServiceFragment implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = MapFragment.class.getSimpleName();
    private StrawberryMap map;
    private SupportMapFragment mMapView;
    private Button drive;
    private Button walk;
    private Button bicycle;
    private Button transit;
    private Button lastChanged = null;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        drive = (Button) view.findViewById(R.id.drive);
        drive.setOnClickListener(this);
        walk = (Button) view.findViewById(R.id.walk);
        walk.setOnClickListener(this);
        bicycle = (Button) view.findViewById(R.id.bicycle);
        bicycle.setOnClickListener(this);
        transit = (Button) view.findViewById(R.id.transit);
        transit.setOnClickListener(this);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        return view;
    }

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
    public void update(Location currentLocation) {
        if (map != null) {
            map.updateMarker("currentLocation", "You are here", currentLocation);
            map.updatePath("currentLocation", "friendLocation");
        } else {
            Log.e(TAG, "Map has not been initialized yet, ditching new location update");
        }
    }

    @Override
    protected void onResumeAction() {
        mMapView.onResume();
    }

    @Override
    protected void onPauseAction() {
        mMapView.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Change the background of the clicked button, and change back the previously changed one
            case R.id.drive:
                if(lastChanged != null){
                    lastChanged.setBackgroundResource(R.drawable.mode_stytle);
                }
                lastChanged = drive;
                map.setMode("driving");
                map.updatePath("currentLocation", "friendLocation");
                drive.setBackgroundResource(R.drawable.mode_colour);
                break;

            case R.id.walk:
                if(lastChanged != null){
                    lastChanged.setBackgroundResource(R.drawable.mode_stytle);
                }
                lastChanged = walk;
                map.setMode("walking");
                map.updatePath("currentLocation", "friendLocation");
                walk.setBackgroundResource(R.drawable.mode_colour);
                break;

            case R.id.bicycle:
                if(lastChanged != null){
                    lastChanged.setBackgroundResource(R.drawable.mode_stytle);
                }
                lastChanged = bicycle;
                map.setMode("bicycling");
                map.updatePath("currentLocation", "friendLocation");
                bicycle.setBackgroundResource(R.drawable.mode_colour);
                break;

            case R.id.transit:
                if(lastChanged != null){
                    lastChanged.setBackgroundResource(R.drawable.mode_stytle);
                }
                lastChanged = transit;
                map.setMode("transit");
                map.updatePath("currentLocation", "friendLocation");
                transit.setBackgroundResource(R.drawable.mode_colour);
                break;

            default:
                Log.d("onClick","Cannot find any button");
                break;
        }
    }
}
