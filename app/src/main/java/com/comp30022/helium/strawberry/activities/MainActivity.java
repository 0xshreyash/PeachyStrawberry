package com.comp30022.helium.strawberry.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.activities.fragments.ChatFragment;
import com.comp30022.helium.strawberry.activities.fragments.FriendListFragment;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;
import com.comp30022.helium.strawberry.components.ar.ARActivity;
import com.comp30022.helium.strawberry.components.location.LocationService;

import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.helpers.DisplayHelper;
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
    private ImageButton chatButton;
    private MapFragment mapFragment;
    private ChatFragment chatFragment;
    private FriendListFragment friendListFragment;

    private GoogleApiClient mGoogleApiClient;
    private LocationService mLocationService;

    // for friend List
    private boolean down = false;
    private boolean expanded = false;
    private int MAX_HEIGHT = 800;
    private float start = 0;

    // for chat
    private boolean chatDown = false;
    private boolean chatExpanded = false;
    private int MAX_WIDTH;
    private float chatStart = 0;
    private int MIN_WIDTH = 1;

    private boolean paused = false;

    private DisplayMetrics metrics;

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
                            Log.e(TAG, e.getMessage());
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
                    Log.d(TAG, "Server friend list is " + response);
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

                            User user = User.getUser(id, username, facebookId);
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
        chatButton = (ImageButton) findViewById(R.id.chat_expand_button);
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_test);
        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        MAX_WIDTH = metrics.widthPixels;
        Log.d("MAX_WIDTH", MAX_WIDTH + "asdfasd");
//        View tStats = findViewById(R.id.transport_stats);
//        View tOpt = findViewById(R.id.transport_option);
//        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) chatFragment.getView().getLayoutParams();
//        marginLayoutParams.setMargins(0, (int) ((tStats.getHeight() + tOpt.getHeight()) * metrics.density), 0, 0);

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

        chatButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Start
                        if (!chatDown) {
                            chatStart = motionEvent.getRawX();
                            chatDown = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        if (chatDown) {
                            chatDown = false;
                            stickyChatExpand();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (chatDown) {
                            float curr = motionEvent.getRawX();
                            updateChatWidth(chatStart, curr);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void updateChatWidth(float chatStart, float curr) {
        ViewGroup.LayoutParams params = chatFragment.getView().getLayoutParams();
        if (!chatExpanded) {
            params.width = (int) Math.abs(chatStart - curr);
        } else {
            params.width = MAX_WIDTH - (int) Math.abs(chatStart - curr);
        }
        chatFragment.getView().setLayoutParams(params);
    }

    private void stickyChatExpand() {
        ViewGroup.LayoutParams params = chatFragment.getView().getLayoutParams();
        if (!chatExpanded) {
            if (params.width > MAX_WIDTH / 5) {
                expandChat();
            } else {
                collapseChat();
            }
        } else {
            if (params.width < 4 * MAX_WIDTH / 5) {
                collapseChat();
            } else {
                expandChat();
            }
        }
    }

    private void collapseChat() {
        ViewGroup.LayoutParams params = chatFragment.getView().getLayoutParams();
        params.width = MIN_WIDTH;
        chatExpanded = false;
        chatButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_left_white_24dp));
        chatFragment.getView().setLayoutParams(params);

        ConstraintLayout.LayoutParams clayout = (ConstraintLayout.LayoutParams) chatButton.getLayoutParams();
        clayout.rightToLeft = R.id.chat_fragment;
        clayout.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
        chatButton.setLayoutParams(clayout);
    }

    private void expandChat() {
        ViewGroup.LayoutParams params = chatFragment.getView().getLayoutParams();
        params.width = MAX_WIDTH;
        chatExpanded = true;
        chatButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_right_white_24dp));
        chatFragment.getView().setLayoutParams(params);

        ConstraintLayout.LayoutParams clayout = (ConstraintLayout.LayoutParams) chatButton.getLayoutParams();
        clayout.rightToLeft = ConstraintLayout.LayoutParams.UNSET;
        clayout.leftToLeft = R.id.chat_fragment;
        chatButton.setLayoutParams(clayout);
    }

    private void expandFriendList() {
        ViewGroup.LayoutParams params = friendListFragment.getView().getLayoutParams();
        params.height = MAX_HEIGHT;
        expanded = true;
        listButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_keyboard_arrow_down_white_24dp));
        friendListFragment.getView().setLayoutParams(params);
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
            if (params.height < 4 * MAX_HEIGHT / 5) {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationService.getLocationRequest(true), mLocationService);
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
    protected void onPause() {
        super.onPause();
        this.paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.paused = false;
    }

    @Override
    public void update(Event info) {
        if (info instanceof PeachServerInterface.InterfaceReadyEvent) {
            PeachServerInterface.InterfaceReadyEvent event = (PeachServerInterface.InterfaceReadyEvent) info;
            if (!event.getValue()) {
                Toast toast = Toast.makeText(this, "Failed to authorize with existing token.", Toast.LENGTH_SHORT);
                toast.show();
                if(!paused)
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
            } else if (event.getKey().equals(StrawberryApplication.SELECTED_TRANSPORT_TAG)) {
                mapFragment.refreshPath();
            }

        }
    }

    // TODO: HARRY: DON"T NEED THIS! (DEBUG OLY)
    public void gotoAR(View bundle) {
        Intent intent = new Intent(getApplicationContext(), ARActivity.class);
        startActivity(intent);
    }
}
