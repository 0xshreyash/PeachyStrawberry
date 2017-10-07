package com.comp30022.helium.strawberry.activities.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationServiceFragment;
import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;
import com.comp30022.helium.strawberry.helpers.BitmapHelper;
import com.comp30022.helium.strawberry.helpers.LocationHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.net.MalformedURLException;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends LocationServiceFragment implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = "StrawberryMapFragment";
    private static final String TOGGLE_FOLLOW_VAL_KEY = "toggleFollowVal";
    private static final float INIT_ZOOM = 16;
    private static final float MIN_MOVE_DIST = 20; // meters
    private String prevRefresh = "";
    private String prevTransport = "";
    private StrawberryMap map;
    private SupportMapFragment mMapView;

    private ImageButton drive, walk, bicycle, transit, lastChanged;

    private TextView arrivalTime;
    private TextView arrivalDistance;

    private Switch toggleFollow;

    private boolean firstMove = true;
    private Location lastPathUpdateLocationUser = null;
    private Location lastPathUpdateLocationFriend = null;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // find views
        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        arrivalTime = (TextView) view.findViewById(R.id.arrival_time);
        arrivalDistance = (TextView) view.findViewById(R.id.arrival_distance);

        drive = (ImageButton) view.findViewById(R.id.drive);
        drive.setOnClickListener(this);

        walk = (ImageButton) view.findViewById(R.id.walk);
        walk.setOnClickListener(this);

        bicycle = (ImageButton) view.findViewById(R.id.bicycle);
        bicycle.setOnClickListener(this);

        transit = (ImageButton) view.findViewById(R.id.transit);
        transit.setOnClickListener(this);

        toggleFollow = (Switch) view.findViewById(R.id.toggle_follow_switch);
        toggleFollow.setChecked(StrawberryApplication.getBoolean(TOGGLE_FOLLOW_VAL_KEY));
        toggleFollow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(!toggleFollow.isChecked()) {
                        map.moveCamera(LocationService.getInstance().getDeviceLocation(), map.getCurrentZoom());
                    }
                }
                return false;
            }
        });

        String savedTransport = StrawberryApplication.getString(StrawberryApplication.SELECTED_TRANSPORT_TAG);

        if(savedTransport == null || savedTransport.equals("transit")) {
            lastChanged = transit;
        } else if(savedTransport.equals("bicycle")) {
            lastChanged = bicycle;
        } else if(savedTransport.equals("walk")) {
            lastChanged = walk;
        } else {
            lastChanged = drive;
        }
        lastChanged.setBackgroundResource(R.drawable.map_mode_selected);
        makeIconWhite(lastChanged);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
        return view;
    }

    private void makeIconWhite(ImageButton lastChanged) {
        switch (lastChanged.getId()) {
            // Change the background of the clicked button, and change back the previously changed one
            case R.id.drive:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_car_white_24dp));
                break;

            case R.id.walk:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_walk_white_24dp));
                break;

            case R.id.bicycle:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_white_24dp));
                break;

            case R.id.transit:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_tram_white_24dp));
                break;

            default:
                Log.e("onClick", "Cannot find any button");
        }
    }

    private void makeIconBlack(ImageButton lastChanged) {
        switch (lastChanged.getId()) {
            // Change the background of the clicked button, and change back the previously changed one
            case R.id.drive:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_car_black_24dp));
                break;

            case R.id.walk:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_walk_black_24dp));
                break;

            case R.id.bicycle:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_black_24dp));
                break;

            case R.id.transit:
                lastChanged.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_tram_black_24dp));
                break;

            default:
                Log.e("onClick", "Cannot find any button");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = new StrawberryMap(googleMap, this);

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

        // default remember last pos
        String lastLoc = StrawberryApplication.getString(LocationService.LAST_LOCATION);
        if(lastLoc != null) {
            map.setCameraLocation(LocationHelper.stringToLocation(lastLoc), map.getCurrentZoom());
        }

        for (User friend : StrawberryApplication.getCachedFriends()) {
            Location friendLoc = locationService.getUserLocation(friend);

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

    public void refreshPath() {
        String selectedId = StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG);
        String currTransport = StrawberryApplication.getString(StrawberryApplication.SELECTED_TRANSPORT_TAG);

        if (selectedId != null) {
            Log.d(TAG, "Tracking " + selectedId);
            if (!selectedId.equals(prevRefresh) || !currTransport.equals(prevTransport)) {
                map.deleteAllPaths();
                arrivalDistance.setText("Calculating..");
                arrivalTime.setText("Calculating..");
            }
            prevRefresh = selectedId;
            prevTransport = currTransport;

            if (!map.updatePath(PeachServerInterface.currentUser().getId(), selectedId)) {
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
        
        if (selectedId.equals(user.getId())) {
            // friend moved
            if(lastPathUpdateLocationFriend == null || lastPathUpdateLocationFriend.distanceTo(currentLocation) > MIN_MOVE_DIST) {
                refreshPath();
                lastPathUpdateLocationFriend = currentLocation;
            } else {
                Log.i(TAG, "Skipping unrequired path update: reason - friend didnt move enough");
            }
        }

        if (user.getId().equals(PeachServerInterface.currentUser().getId())) {
            // current user moved
            if(lastPathUpdateLocationUser == null || lastPathUpdateLocationUser.distanceTo(currentLocation) > MIN_MOVE_DIST) {
                refreshPath();
                lastPathUpdateLocationUser = currentLocation;
            } else {
                Log.i(TAG, "Skipping unrequired path update: reason - user didnt move enough");
            }
        }

        if(firstMove) {
            map.moveCamera(currentLocation, INIT_ZOOM);
            firstMove = false;
        } else if (toggleFollow.isChecked()) {
            map.moveCamera(currentLocation, map.getCurrentZoom());
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
        makeIconBlack(lastChanged);

        switch (view.getId()) {
            // Change the background of the clicked button, and change back the previously changed one
            case R.id.drive:
                lastChanged = drive;
                map.setMode("driving");
                StrawberryApplication.setString(StrawberryApplication.SELECTED_TRANSPORT_TAG, "drive");
                break;

            case R.id.walk:
                lastChanged = walk;
                map.setMode("walking");
                StrawberryApplication.setString(StrawberryApplication.SELECTED_TRANSPORT_TAG, "walk");
                break;

            case R.id.bicycle:
                lastChanged = bicycle;
                map.setMode("bicycling");
                StrawberryApplication.setString(StrawberryApplication.SELECTED_TRANSPORT_TAG, "bicycle");
                break;

            case R.id.transit:
                lastChanged = transit;
                map.setMode("transit");
                StrawberryApplication.setString(StrawberryApplication.SELECTED_TRANSPORT_TAG, "transit");
                break;

            default:
                Log.e("onClick", "Cannot find any button");
                return;
        }

        lastChanged.setBackgroundResource(R.drawable.map_mode_selected);
        makeIconWhite(lastChanged);

        map.updatePath(PeachServerInterface.currentUser().getId(), StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG));
    }

    // Update the arrival time and distance which will be shown in the textview.
    public void changeText(String name, String value) {
        switch (name) {
            case "distance":
                arrivalDistance.setText(value);
                break;

            case "duration":
                arrivalTime.setText(value);
                break;

            default:
                Log.d("changeText", "The text is changed successfully");
        }
    }
}
