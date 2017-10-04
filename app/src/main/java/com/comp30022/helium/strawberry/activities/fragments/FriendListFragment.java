package com.comp30022.helium.strawberry.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.friends.FriendListAdapter;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment which will display the list of friends when instantiated.
 *
 */
public class FriendListFragment extends Fragment implements Subscriber<Integer> {

    private RecyclerView mFriendRecycler;
    private FriendListAdapter mFriendAdapter;
    private List<User> friends;
    private String TAG = "FriendListFragment";
    private View myView;
    private static int DEFAULT_SELECTION = 0;

    public FriendListFragment() {
        friends = new ArrayList<>();
    }

    /**
     * Called when the fragment is created.
     * @param savedInstanceState the saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PeachServerInterface.getInstance().getFriends(new StrawberryListener(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray friendsJSON = new JSONArray(response.toString());
                        //username = self.get("username").toString()
                        for(int i = 0; i < friendsJSON.length(); i++) {
                            JSONObject friend = new JSONObject(friendsJSON.get(i).toString());
                            String username = friend.get("username").toString();
                            String facebookId = friend.get("friendId").toString();
                            String id = friend.get("id").toString();

                            friends.add(new User(id, username, facebookId));
                            Log.i(TAG, "Adding " + username + " to friends");
                            Log.i(TAG, friends.get(i).getUsername());
                            // Set the first person to be selected initially.
                            setRecyclerProperties(DEFAULT_SELECTION);

                        }
                        Log.i(TAG, "Size of friends: " + friends.size());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null));
        } catch (NotInstantiatedException | InstanceExpiredException e) {
            e.printStackTrace();
        }


    }

    /**
     * Called to assign a view to the FriendFragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the created view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_friend_list, null);
    }

    /**
     * Called once the view has been created, assign the recycler view and the adapter
     * accordingly.
     * @param view the view that was created for the friend.
     * @param savedInstance the saved instance.
     */
    public void onViewCreated(View view, Bundle savedInstance) {

        setRecyclerView(view, savedInstance);
    }

    public void update(Integer info) {
        setRecyclerProperties(info);
    }


    public void setRecyclerProperties(Integer info) {
        DEFAULT_SELECTION = info;
        this.mFriendAdapter = new FriendListAdapter(this.getView().getContext(), friends, this);
        mFriendAdapter.setSelectedPosition(info);
        mFriendRecycler.setAdapter(mFriendAdapter);

        mFriendRecycler.setLayoutManager(new LinearLayoutManager(myView.getContext()));

    }

    public void setRecyclerView(View view, Bundle savedInstance) {
        myView = view;
        mFriendRecycler = (RecyclerView)myView.findViewById(R.id.recyclerview_friend_list);
        // TODO: Make sure we use getContext instead of view.getContext()
        this.setRecyclerProperties(DEFAULT_SELECTION);
    }

    public View getView() {
        return myView;
    }

    public void setView(View myView) {
        this.myView = myView;
    }
}
