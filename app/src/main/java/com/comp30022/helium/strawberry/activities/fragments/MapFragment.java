package com.comp30022.helium.strawberry.activities.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationServiceFragment;
import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;
import com.comp30022.helium.strawberry.helpers.BitmapHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends LocationServiceFragment implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "StrawberryMapFragment";
    private static final String TOGGLE_FOLLOW_VAL_KEY = "toggleFollowVal";
    private String prevRefresh = "";
    private StrawberryMap map;
    private SupportMapFragment mMapView;

    private Button drive;
    private Button walk;
    private Button bicycle;
    private Button transit;
    private Button lastChanged;

    private TextView arrival_time;
    private TextView arrival_distance;

    private Switch toggleFollow;

    private ArrayList<Location> locations;
    private boolean firstMove = true;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // find views
        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        arrival_time = (TextView) view.findViewById(R.id.arrival_time);
        arrival_distance = (TextView) view.findViewById(R.id.arrival_distance);

        drive = (Button) view.findViewById(R.id.drive);
        drive.setOnClickListener(this);

        walk = (Button) view.findViewById(R.id.walk);
        walk.setOnClickListener(this);

        bicycle = (Button) view.findViewById(R.id.bicycle);
        bicycle.setOnClickListener(this);

        transit = (Button) view.findViewById(R.id.transit);
        transit.setOnClickListener(this);

        toggleFollow = (Switch) view.findViewById(R.id.toggle_follow_switch);
        toggleFollow.setChecked(StrawberryApplication.getBoolean(TOGGLE_FOLLOW_VAL_KEY));

        //TODO load this instead of hard-coded
        lastChanged = transit;

        lastChanged.setBackgroundResource(R.drawable.map_mode_selected);
        lastChanged.setTextColor(Color.WHITE);

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
        map = new StrawberryMap(googleMap, this);
        locations = new ArrayList<>();

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_peach));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        for (User friend : StrawberryApplication.getCachedFriends()) {
            Location friendLoc = locationService.getUserLocation(friend);
            locations.add(friendLoc);

            map.updateMarker(friend.getId(), friend.getUsername(), friendLoc);
            try {
                StrawberryCallback<Bitmap> callback = new StrawberryCallback<Bitmap>() {
                    @Override
                    public void run(Bitmap bitmap) {
                        map.updateMarkerImage((String) attribute, BitmapHelper.makeCircular(bitmap));
                        Log.d(TAG, "Finished downloading icon for " + attribute);
                    }
                };
                callback.attribute = friend.getId();

                friend.getFbPicture(User.ProfilePictureType.SQUARE, callback);
            } catch (FacebookIdNotSetException | MalformedURLException e) {
                e.printStackTrace();
            }
        }

        refreshPath();
    }

    private void refreshPath() {
        String selectedId = StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG);

        if (selectedId != null) {
            Log.d(TAG, "Tracking " + selectedId);
            if(!selectedId.equals(prevRefresh))
                map.deleteAllPaths();
            prevRefresh = selectedId;

            if(!map.updatePath(PeachServerInterface.currentUser().getId(), selectedId)) {
                map.moveCamera(locations, 200);
                Log.e(TAG, "Failed to refresh path");
            }

            Log.i(TAG, "Successfully to refresh path");
        }
    }

    @Override
    public void update(LocationEvent updatedLocation) {
        if (map == null) {
            Log.e(TAG, "Map has not been initialized yet, ditching new location update");
            return;
        }

        User user = updatedLocation.getKey();
        Location currentLocation = updatedLocation.getValue();
        map.updateMarker(user.getId(), user.getUsername(), currentLocation);

        String selectedId = StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG);

        if(selectedId.equals(user.getId()) || user.getId().equals(PeachServerInterface.currentUser().getId()))
            refreshPath();

        if(toggleFollow.isChecked() || firstMove) {
            map.moveCamera(currentLocation, 16);
            firstMove = false;
        }
    }

    @Override
    protected void onResumeAction() {
        mMapView.onResume();
    }

    @Override
    protected void onPauseAction() {
        mMapView.onPause();

        Log.d(TAG, "Saving toggle value");
        StrawberryApplication.setBoolean(TOGGLE_FOLLOW_VAL_KEY, toggleFollow.isChecked());
    }

    // Change the travel mode.
    @Override
    public void onClick(View view) {
        lastChanged.setBackgroundResource(R.drawable.map_mode_default);
        lastChanged.setTextColor(Color.BLACK);

        switch (view.getId()) {
            // Change the background of the clicked button, and change back the previously changed one
            case R.id.drive:
                lastChanged = drive;
                map.setMode("driving");
                break;

            case R.id.walk:
                lastChanged = walk;
                map.setMode("walking");
                break;

            case R.id.bicycle:
                lastChanged = bicycle;
                map.setMode("bicycling");
                break;

            case R.id.transit:
                lastChanged = transit;
                map.setMode("transit");
                break;

            default:
                Log.e("onClick", "Cannot find any button");
                return;
        }

        lastChanged.setBackgroundResource(R.drawable.map_mode_selected);
        lastChanged.setTextColor(Color.WHITE);

        map.updatePath(PeachServerInterface.currentUser().getId(), StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG));
    }

    // Update the arrival time and distance which will be shown in the textview.
    public void changeText(String name, String value) {
        switch (name) {
            case "distance":
                arrival_distance.setText(value);
                break;

            case "duration":
                arrival_time.setText(value);
                break;

            default:
                Log.d("changeText", "The text is changed successfully");
        }
    }
}
