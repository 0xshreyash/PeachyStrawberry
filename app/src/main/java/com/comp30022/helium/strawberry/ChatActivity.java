package com.comp30022.helium.strawberry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.comp30022.helium.strawberry.chat.Message;
import com.comp30022.helium.strawberry.chat.MessageListAdapter;
import com.comp30022.helium.strawberry.chat.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyashpatodia on 15/09/17.
 */

public class ChatActivity extends AppCompatActivity{
    private RecyclerView mMessageRecycler;
    //private MessageListAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        List<Message> messages = new ArrayList<>();
        User me = new User("Shreyash", 1);
        User them = new User("Harry", 2);
        Message first = new Message("Hey how are you?", me, 100000002);
        Message second = new Message("I'm good thank you", them, 100000003);
        messages.add(first);
        messages.add(second);


        // TODO: Actually get messages from somewhere.
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
