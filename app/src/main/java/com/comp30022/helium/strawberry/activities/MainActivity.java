package com.comp30022.helium.strawberry.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.activities.fragments.FriendListFragment;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;
import com.comp30022.helium.strawberry.components.location.LocationService;

import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Event;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Subscriber<Event> {
    private static final String TAG = "MainActivity";

    private ImageButton listButton;
    private MapFragment mapFragment;
    private FriendListFragment friendListFragment;

    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;

    // for friend List
    private boolean down = false;
    private boolean expanded = false;
    private int MAX_HEIGHT = 800;
    private float start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Strawberry");
        super.onCreate(savedInstanceState);
        StrawberryApplication.registerSubscriber(this);

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
        // auto add friends
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

                            // refresh friends list
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        refreshFriendsListCache();
                    }
                }
        ).executeAsync();
    }

    private void refreshFriendsListCache() {
        try {
            PeachServerInterface.getInstance().getFriends(new StrawberryListener(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONArray friendsJSON = null;
                    try {
                        friendsJSON = new JSONArray(response.toString());
                        //username = self.get("username").toString()
                        Set<String> friendSet = new HashSet<>();

                        for (int i = 0; i < friendsJSON.length(); i++) {
                            JSONObject friend = new JSONObject(friendsJSON.get(i).toString());
                            String id = friend.get("id").toString();
                            String username = friend.get("username").toString();
                            String facebookId = friend.get("facebookId").toString();

                            User user = new User(id, username, facebookId);
                            friendSet.add(user.toString());
                        }

                        // refresh friends list
                        Log.i(TAG, "Friend set is " + friendSet);
                        StrawberryApplication.setStringSet("friends", friendSet);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    setupComplete();
                }
            }, null));
        } catch (NotInstantiatedException | InstanceExpiredException e) {
            e.printStackTrace();
        }
    }


    private void setupComplete() {
        // init loc service
        mLocationService = new LocationService();
        mLocationService.setup(mGoogleApiClient);

        // finished loading content view
        setContentView(R.layout.activity_main);
        listButton = (ImageButton) findViewById(R.id.friend_list_button);
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_test);

        friendListFragment = (FriendListFragment) getFragmentManager().findFragmentById(R.id.friend_list_fragment);
        listButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Start
                        if (!down) {
                            start = motionEvent.getRawY();
                            down = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        if (down) {
                            down = false;
                            stickyListHeight();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (down) {
                            float curr = motionEvent.getRawY();
                            updateListHeight(start, curr);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void expandFriendList() {
        ViewGroup.LayoutParams params = friendListFragment.getView().getLayoutParams();
        params.height = MAX_HEIGHT;
        expanded = true;
        listButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_down_white_24dp));
    }

    private void collapseFriendList() {
        ViewGroup.LayoutParams params = friendListFragment.getView().getLayoutParams();
        params.height = 0;
        expanded = false;
        listButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_up_white_24dp));
        friendListFragment.getView().setLayoutParams(params);
    }

    private void stickyListHeight() {
        ViewGroup.LayoutParams params = friendListFragment.getView().getLayoutParams();
        if (!expanded) {
            if (params.height > MAX_HEIGHT / 5) {
                expandFriendList();
            } else {
                collapseFriendList();
            }
        } else {
            if (params.height < 4 * MAX_HEIGHT / 4) {
                collapseFriendList();
            } else {
                expandFriendList();
            }
        }
    }

    private void updateListHeight(float start, float curr) {
        ViewGroup.LayoutParams params = friendListFragment.getView().getLayoutParams();
        if (!expanded) {
            params.height = (int) Math.abs(start - curr);
        } else {
            params.height = MAX_HEIGHT - (int) Math.abs(start - curr);
        }
        friendListFragment.getView().setLayoutParams(params);
    }

    // if does not have permission, boot to start
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            backToStart();
        }
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

    private void backToStart() {
        Intent intent = new Intent(getApplicationContext(), InitActivity.class);
        LoginManager.getInstance().logOut();
        startActivity(intent);
        this.finish();
    }

    @Override
    public void update(Event info) {
        if (info instanceof PeachServerInterface.InterfaceReadyEvent) {
            PeachServerInterface.InterfaceReadyEvent event = (PeachServerInterface.InterfaceReadyEvent) info;
            if (!event.getValue()) {
                Toast toast = Toast.makeText(this, "Failed to authorize with existing token.", Toast.LENGTH_SHORT);
                toast.show();
                backToStart();
            } else {
                // successful, guarantee that we have permissions
                // TODO: 3/10/17  replace with simple callback for the chain of events
                autoAddFriends();
            }
        } else if (info instanceof StrawberryApplication.GlobalVariableChangeEvent) {
            StrawberryApplication.GlobalVariableChangeEvent event = (StrawberryApplication.GlobalVariableChangeEvent) info;
            if (event.getKey().equals(StrawberryApplication.SELECTED_USER_TAG)) {
                // selected user has changed
                mapFragment.refreshPath();
                collapseFriendList();
            }
        }
    }
}
