package com.comp30022.helium.strawberry.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.components.chat.Message;
import com.comp30022.helium.strawberry.components.chat.MessageListAdapter;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Chat uses a Recycler View (which is the region where the messages are
 * displayed. The activity chat has space for a recycler view and also allows
 * for you to write messages.
 * RecyclerView is used because it is standard in chat applications.
 */
public class ChatFragment extends Fragment {

    /* Recycler view contains all the messages */
    private static final int QUERY_TIME_SECS = 3;
    private static final String TAG = "StrawberryChat";
    private static final long RECENT_TIME = 86400000; // 1 day
    private RecyclerView mMessageRecycler;

    boolean blockNotify;
    private Timer timer;
    private User friend;
    private User me;
    List<Message> messages;
    private MessageListAdapter mMessageAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        messages = new ArrayList<>();
        blockNotify = true;

        me = PeachServerInterface.currentUser();
        String selectedId = StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG);
        friend = new User(selectedId);

        //TODO: update later to STOMP
        timer = new Timer();
        timer.scheduleAtFixedRate(getChatQueryTimerTask(), 0, QUERY_TIME_SECS * 1000);

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, null);
        Button button = (Button) view.findViewById(R.id.button_chatbox_send);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickSend();
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstance) {

        mMessageRecycler = (RecyclerView)view.findViewById(R.id.reyclerview_message_list);
        setRecyclerProperties();
    }

    /**
     * Used to set the recycler properties on creation and on sending/receiving of new
     * messages.
     */
    public void setRecyclerProperties() {

        mMessageAdapter = new MessageListAdapter(getContext(), messages);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(getChatQueryTimerTask(), 0, QUERY_TIME_SECS * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        timer = null;
    }

    /**
     * Gets the chat from the server
     */
    private void queryChat() {
        try {
            PeachServerInterface.getInstance().getChatLog(friend, recentTime(),
                    new StrawberryListener(new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray chats = new JSONArray(response);
                        Log.d(TAG, chats.toString());
                        for (int i = 0; i < chats.length(); i++) {
                            JSONObject chat = new JSONObject(chats.get(i).toString());
                            User sender;

                            if (chat.get("from").equals(me.getId()))
                                sender = me;
                            else
                                sender = friend;
                            updateMessage(new Message(chat.getString("message"), sender,
                                    chat.getLong("timestamp")));
                        }
                        blockNotify = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null));

        } catch (NotInstantiatedException | InstanceExpiredException e) {
            e.printStackTrace();
        }
    }

    public TimerTask getChatQueryTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Getting chat log");
                queryChat();
            }
        };
    }

    /**
     * Updates a message
     * @param message messsage to be updated
     */
    private void updateMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);

            Collections.sort(messages, new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    if(m1.getCreatedAt() < m2.getCreatedAt())
                        return -1;
                    if(m1.getCreatedAt() > m2.getCreatedAt())
                        return 1;
                    return 0;
                }
            });

            // update view
            setRecyclerProperties();
            mMessageRecycler.scrollToPosition(messages.size() - 1);

            // notify
            if (!blockNotify) {
                if (!message.getSender().getId().equals(me.getId())) {
                    Toast toast = Toast.makeText(this.getContext(), friend.getUsername() + ": " +
                            message.getMessage(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }

    /**
     * Gets the most recennt time
     * @return time as long
     */
    private Long recentTime() {
        return System.currentTimeMillis() - RECENT_TIME;
    }

    /**
     * Called when the Send button is pressed.
     */
    public void clickSend() {

        //Log.e(TAG, view.toString());
        EditText editText = (EditText)this.getView().findViewById(R.id.edittext_chatbox);

        String message = editText.getText().toString();
        Log.d(TAG, "sending " + message);

        if (message.length() > 0) {
            try {
                PeachServerInterface.getInstance().postChat(message, friend.getId(),
                        new StrawberryListener(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        queryChat();
                    }
                }, null));
            } catch (NotInstantiatedException | InstanceExpiredException e) {
                e.printStackTrace();
            }
            editText.getText().clear();
        }
    }
}
