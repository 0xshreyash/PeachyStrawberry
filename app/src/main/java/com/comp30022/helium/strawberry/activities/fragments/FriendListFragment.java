package com.comp30022.helium.strawberry.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.friends.FriendListAdapter;
import com.comp30022.helium.strawberry.entities.User;

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

    /**
     * Called when the fragment is created.
     * @param savedInstanceState the saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        friends = new ArrayList<>();
        // Get the application token,
        String token = StrawberryApplication.getString("token");
        // Connect to facebook and get the users.
        User firstFriend = new User("1", "Shreyash");
        User secondFriend = new User("2", "Harry");

        // TODO: Actually getString messages from somewhere.

        super.onCreate(savedInstanceState);
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

        mFriendRecycler = (RecyclerView)view.findViewById(R.id.reyclerview_friend_list);

        // TODO: Make sure we use getContext instead of view.getContext()
        mFriendAdapter = new FriendListAdapter(getContext(), friends);

        mFriendRecycler.setAdapter(mFriendAdapter);
        mFriendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
