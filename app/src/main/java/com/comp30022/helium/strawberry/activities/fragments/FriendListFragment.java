package com.comp30022.helium.strawberry.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.comp30022.helium.strawberry.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyashpatodia on 30/09/17.
 */

public class FriendListFragment extends Fragment {

    private RecyclerView mMessageRecycler;
    //private MessageListAdapter mMessageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        List<User> friends = new ArrayList<>();

        User firstFriend = new User("1", "Shreyash");
        User secondFriend = new User("2", "Harry");

        // TODO: Actually getString messages from somewhere.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Log.d("Hi How are you?", "I am good ");
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        //mMessageRecycler.setHasFixedSize(true);
        MessageListAdapter mMessageAdapter = new MessageListAdapter(this, messages);
        //mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}
