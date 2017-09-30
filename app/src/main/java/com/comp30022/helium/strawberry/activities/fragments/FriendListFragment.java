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
public class FriendListFragment extends Fragment {

    private RecyclerView mFriendRecycler;
    private FriendListAdapter mFriendAdapter;
    private List<User> friends;
    private String TAG = "FriendListFragment";
    private View myView;

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
                            String id = friend.get("id").toString();
                            friends.add(new User(id, username));
                            Log.e(TAG, "Adding " + username + " to friends");
                            Log.e(TAG, friends.get(i).getUsername());
                            setRecyclerProperties();

                        }
                        Log.e(TAG, "Size of friends: " + friends.size());

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

    public void setRecyclerProperties() {
        this.mFriendAdapter = new FriendListAdapter(myView.getContext(), friends);
        mFriendRecycler.setAdapter(mFriendAdapter);
        mFriendRecycler.setLayoutManager(new LinearLayoutManager(myView.getContext()));

    }

    public void setRecyclerView(View view, Bundle savedInstance) {
        myView = view;
        mFriendRecycler = (RecyclerView)myView.findViewById(R.id.recyclerview_friend_list);
        // TODO: Make sure we use getContext instead of view.getContext()
        this.setRecyclerProperties();
    }
}
