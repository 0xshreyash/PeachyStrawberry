package com.comp30022.helium.strawberry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.comp30022.helium.strawberry.chat.Message;
import com.comp30022.helium.strawberry.chat.MessageListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyashpatodia on 15/09/17.
 */

public class ChatActivity extends AppCompatActivity{
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        List<Message> messages = new ArrayList<>();

        // TODO: Actually get messages from somewhere.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

    }
}
