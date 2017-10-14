package com.comp30022.helium.strawberry.activities.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;

import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.location.LocationService;
import com.comp30022.helium.strawberry.components.map.StrawberryMapWrapperLayout;
import com.comp30022.helium.strawberry.components.map.helpers.AutocompleteAdapter;
import com.comp30022.helium.strawberry.components.map.helpers.AutocompleteView;
import com.comp30022.helium.strawberry.components.map.helpers.MenuItemTouchListener;
import com.comp30022.helium.strawberry.components.map.helpers.SearchSwipeListener;
import com.comp30022.helium.strawberry.components.map.helpers.TextChangeListener;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.components.location.LocationEvent;
import com.comp30022.helium.strawberry.components.location.LocationServiceFragment;
import com.comp30022.helium.strawberry.components.map.StrawberryMap;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;
import com.comp30022.helium.strawberry.helpers.BitmapHelper;
import com.comp30022.helium.strawberry.helpers.DisplayHelper;
import com.comp30022.helium.strawberry.helpers.LocationHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

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
    private View view;
    private ViewGroup clickMenu, searchBar;
    private TextView userName;
    private ImageButton chatButtom;
    private ImageButton arButton;
    private MenuItemTouchListener chatListener;
    private MenuItemTouchListener arListener;
    private StrawberryMapWrapperLayout mapLayout;

    private ImageButton drive, walk, bicycle, transit, lastChanged, searchButton;

    private TextView arrivalTime;
    private TextView arrivalDistance;

    private Switch toggleFollow;

    private boolean firstMove = true;
    private Location lastPathUpdateLocationUser = null;
    private Location lastPathUpdateLocationFriend = null;
    private int MARKER_HEIGHT = 5;
    private int OFFSET_FROM_MARKER = 20;

    private AutocompleteAdapter autoCompleteAdapter;
    private AutocompleteView searchBox;
    private User[] friendArray;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_map, container, false);
        //Log.i(TAG, this.view.findViewById(R.id.map_wrapper_layout).getId() + " the id of the map_wrapper is");
        friendArray = new User[StrawberryApplication.getCachedFriends().size()];
        friendArray = StrawberryApplication.getCachedFriends().toArray(friendArray);

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
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (!toggleFollow.isChecked()) {
                        map.moveCamera(LocationService.getInstance().getDeviceLocation(), map.getCurrentZoom());
                    }
                }
                return false;
            }
        });


        String savedTransport = StrawberryApplication.getString(StrawberryApplication.SELECTED_TRANSPORT_TAG);

        if (savedTransport == null || savedTransport.equals("transit")) {
            lastChanged = transit;
        } else if (savedTransport.equals("bicycle")) {
            lastChanged = bicycle;
        } else if (savedTransport.equals("walk")) {
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

        /**
         * Stuff for the custon onClick for markers
         */
        this.clickMenu = (ViewGroup) inflater.inflate(R.layout.marker_click_menu, null);
        this.userName = (TextView) clickMenu.findViewById(R.id.friend_name);
        this.arButton = (ImageButton) clickMenu.findViewById(R.id.button_ar);
        this.chatButtom = (ImageButton) clickMenu.findViewById(R.id.button_chat);

        this.chatListener = new MenuItemTouchListener(clickMenu, R.id.button_chat) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                if (!marker.getTitle().equals(PeachServerInterface.currentUser().getUsername())) {
                    //TODO: open chat
                    Toast.makeText(getContext(), marker.getTitle() + " chat was clicked", Toast.LENGTH_LONG).show();
                }
            }
        };

        this.chatButtom.setOnTouchListener(chatListener);
        this.arListener = new MenuItemTouchListener(clickMenu, R.id.button_ar) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                if (!marker.getTitle().equals(PeachServerInterface.currentUser().getUsername())) {
                    //TODO: AR
                    Toast.makeText(getContext(), marker.getTitle() + " ar was clicked", Toast.LENGTH_LONG).show();
                }
            }
        };

        this.arButton.setOnTouchListener(arListener);


        /**
         * Search stuff begins here
         */

        searchBar = (ViewGroup) view.findViewById(R.id.my_search_bar);
        searchButton = (ImageButton) view.findViewById(R.id.my_search_button);
        searchBox = (AutocompleteView) view.findViewById(R.id.my_search_box);

        searchBox.setSelectAllOnFocus(true);
        searchBox.setFocusableInTouchMode(true);

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    User curr = autoCompleteAdapter.getTopUser();
                    if(curr != null ) {
                        resetSearchBar();
                        showWindowForFriend(curr);
                    }
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBox.getVisibility() == View.VISIBLE) {
                    searchBox.setVisibility(View.INVISIBLE);
                    searchBox.setVisibility(View.GONE);
                    searchBox.setText("");

                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) searchBar.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.BELOW);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    //searchBox.setWidth(searchBox.getWidth() + DisplayHelper.dpToPixel(10, getContext()));
                } else if (searchBox.getVisibility() == View.GONE) {
                    searchBox.setVisibility(View.VISIBLE);
                    searchBox.requestFocus();

                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) searchBar.getLayoutParams();
                    layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    layoutParams.addRule(RelativeLayout.BELOW, R.id.transport_stats);
                }
            }
        });

        searchBox.addTextChangedListener(new TextChangeListener(this, getContext()));

        /*
        searchButton.setOnTouchListener(new SearchSwipeListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                //super.onSwipeRight();
                if(searchBox.getVisibility() == View.VISIBLE) {
                    searchBox.setVisibility(View.GONE);
                }

            }

            @Override
            public void onSwipeLeft() {
                if(searchBox.getVisibility() == View.GONE) {
                    searchBox.setVisibility(View.VISIBLE);
                }
            }
        });
        searchBox.setOnTouchListener(new SearchSwipeListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                if(searchBox.getVisibility() == View.GONE) {
                    searchBox.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSwipeRight() {
                if(searchBox.getVisibility() == View.VISIBLE) {
                    searchBox.setVisibility(View.GONE);
                }
            }
        });
        */
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
        if (lastLoc != null) {
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

        mapLayout = (StrawberryMapWrapperLayout) view.findViewById(R.id.map_wrapper_layout);
        Log.e(TAG, view.toString());
        Log.e(TAG, view.findViewById(R.id.map_wrapper_layout).getId() + " is also here");
        mapLayout.init(googleMap,
                DisplayHelper.dpToPixel(MARKER_HEIGHT + OFFSET_FROM_MARKER, getContext()));
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                userName.setText(marker.getTitle());
                chatListener.setMarker(marker);
                arListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapLayout.setMarkerWithInfoWindow(marker, clickMenu);
                return clickMenu;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                return null;
            }
        });

        this.autoCompleteAdapter = new AutocompleteAdapter(getContext(), R.layout.item_friend, friendArray, this);
        this.getAutocompleteView().setAdapter(autoCompleteAdapter);
    }

    public void refreshPath() {
        String selectedId = StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG);
        String currTransport = StrawberryApplication.getString(StrawberryApplication.SELECTED_TRANSPORT_TAG);

        if (selectedId != null) {
            Log.d(TAG, "Tracking " + selectedId);
            if ((prevRefresh != null && !selectedId.equals(prevRefresh) || (prevTransport != null && !currTransport.equals(prevTransport)))) {
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

        if (selectedId != null && selectedId.equals(user.getId())) {
            // friend moved
            if (lastPathUpdateLocationFriend == null || lastPathUpdateLocationFriend.distanceTo(currentLocation) > MIN_MOVE_DIST) {
                refreshPath();
                lastPathUpdateLocationFriend = currentLocation;
            } else {
                Log.i(TAG, "Skipping unrequired path update: reason - friend didnt move enough");
            }
        }

        if (user.getId().equals(PeachServerInterface.currentUser().getId())) {
            // current user moved
            if (lastPathUpdateLocationUser == null || lastPathUpdateLocationUser.distanceTo(currentLocation) > MIN_MOVE_DIST) {
                refreshPath();
                lastPathUpdateLocationUser = currentLocation;
            } else {
                Log.i(TAG, "Skipping unrequired path update: reason - user didnt move enough");
            }
        }

        if (firstMove) {
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

    public ArrayAdapter<User> getAutoCompleteAdapter() {
        return autoCompleteAdapter;
    }

    public void setAutoCompleteAdapter(AutocompleteAdapter autoCompleteAdapter) {
        this.autoCompleteAdapter = autoCompleteAdapter;
    }

    public void showWindowForFriend(User user) {
        this.map.showWindowForMarker(user.getId());
    }

    public User[] getFriendArray() {
        return friendArray;
    }

    public AutocompleteView getAutocompleteView() {
        return searchBox;
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

    public void resetSearchBar() {
        searchButton.callOnClick();
    }
}
