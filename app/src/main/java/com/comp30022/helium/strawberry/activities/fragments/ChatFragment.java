package com.comp30022.helium.strawberry.activities.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.activities.MainActivity;
import com.comp30022.helium.strawberry.components.chat.Message;
import com.comp30022.helium.strawberry.components.chat.MessageListAdapter;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.components.server.exceptions.InstanceExpiredException;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.patterns.Event;
import com.comp30022.helium.strawberry.patterns.Subscriber;
import com.comp30022.helium.strawberry.patterns.exceptions.NotInstantiatedException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class ChatFragment extends Fragment implements Subscriber<Event> {
    private static final int QUERY_TIME_SECS = 3;
    private static final int BG_QUERY_TIME_SECS = 15;
    private static final String TAG = "StrawberryChat";
    private RecyclerView mMessageRecycler;
    private View loadingLayout, emptyLayout;
    private NotificationManager notificationManager;

    boolean blockNotify;
    private Timer timer;
    private User friend;
    private User me;
    List<Message> messages;
    private MessageListAdapter mMessageAdapter;
    private static int NOTIFICATION_ID = 1;
    private static final String TRACKING_ALERT = "Tracking Alert";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        messages = new ArrayList<>();
        blockNotify = true;

        me = PeachServerInterface.currentUser();

        String selectedId = StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG);

        if(selectedId == null)
            selectedId = me.getId();

        friend = User.getUser(selectedId);

        //TODO: update later to STOMP
        timer = new Timer();
        timer.scheduleAtFixedRate(getChatQueryTimerTask(), 0, QUERY_TIME_SECS * 1000);

        // Creating a notification channel
        notificationManager =
                (NotificationManager)getActivity().
                        getSystemService(getContext().NOTIFICATION_SERVICE);
        // The id of the channel.


        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, null);
        Button button = (Button) view.findViewById(R.id.button_chatbox_send);

        loadingLayout = view.findViewById(R.id.load_message_layout);
        emptyLayout = view.findViewById(R.id.no_message_layout);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSend();
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstance) {
        mMessageRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_message_list);
        setRecyclerProperties();
    }

    /**
     * Used to set the recycler properties on creation and on sending/receiving of new
     * messages.
     */
    public void setRecyclerProperties() {
        mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        timer.cancel();
        timer.purge();

        timer = new Timer();
        timer.scheduleAtFixedRate(getChatQueryTimerTask(), 0, QUERY_TIME_SECS * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();

        timer = new Timer();
        timer.scheduleAtFixedRate(getChatQueryTimerTask(), 0, BG_QUERY_TIME_SECS * 1000);
    }

    private void parseAndSaveChatLog(JSONArray chats) {
        Log.d(TAG, chats.toString());
        try {
            for (int i = 0; i < chats.length(); i++) {
                JSONObject chat = new JSONObject(chats.get(i).toString());

                User sender, receiver;
                if (chat.get("from").equals(me.getId()))
                    sender = me;
                else
                    sender = User.getUser(chat.getString("from"));

                if (chat.get("to").equals(me.getId()))
                    receiver = me;
                else
                    receiver = User.getUser(chat.getString("to"));

                updateMessage(new Message(chat.getString("message"), sender, receiver, chat.getLong("timestamp")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLoading(boolean b) {
        loadingLayout.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    private void showEmpty(boolean b) {
        emptyLayout.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    /**
     * Gets the chat from the server
     */

    private void queryChat() {
        if (friend == null || friend.getId() == null)
            return;
        try {
            if (messages.isEmpty()) {
                PeachServerInterface.getInstance().getRecentChatLog(friend, new StrawberryListener(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray chats = new JSONArray(response);
                            hideLoading(true);
                            parseAndSaveChatLog(chats);
//                                    blockNotify = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null));

            } else {
                Long time = messages.get(messages.size() - 1).getCreatedAt();
                PeachServerInterface.getInstance().getChatLog(friend, time, new StrawberryListener(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            hideLoading(true);
                            JSONArray chats = new JSONArray(response);
                            parseAndSaveChatLog(chats);
//                                    blockNotify = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null));
            }

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
     *
     * @param message messsage to be updated
     */
    private void updateMessage(Message message) {
        // Adding a new message
        if ((
                message.getSender().getId().equals(friend.getId()) ||
                        (message.getSender().getId().equals(me.getId()) && message.getReceiver().getId().equals(friend.getId()))
        ) && !messages.contains(message)) {

            messages.add(message);
            Log.d(TAG, message + " updated");

            if(message.getSender().getId().equals(friend.getId())) {
                if(message.getMessage().split(":")[0].equals(TRACKING_ALERT)) {
                    this.makeNotification(message, true);
                }
                else {
                    this.makeNotification(message, false);
                }
            }

            Collections.sort(messages, new Comparator<Message>() {
                @Override
                public int compare(Message m1, Message m2) {
                    if (m1.getCreatedAt() < m2.getCreatedAt())
                        return -1;
                    if (m1.getCreatedAt() > m2.getCreatedAt())
                        return 1;
                    return 0;
                }
            });

            // update view
            setRecyclerProperties();
            mMessageRecycler.scrollToPosition(messages.size() - 1);

            // notify
//            if (!blockNotify) {
//                if (!message.getSender().getId().equals(me.getId())) {
//                    Toast toast = Toast.makeText(this.getContext(), friend.getUsername() + ": " +
//                            message.getMessage(), Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
        }
        showEmpty(messages.size() == 0);
    }

    /**
     * Called when the Send button is pressed.
     */
    public void clickSend() {
        //Log.e(TAG, view.toString());
        EditText editText = (EditText) this.getView().findViewById(R.id.edittext_chatbox);

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

    @Override
    public void update(Event info) {
        if (info instanceof StrawberryApplication.GlobalVariableChangeEvent) {
            StrawberryApplication.GlobalVariableChangeEvent event = (StrawberryApplication.GlobalVariableChangeEvent) info;
            if (event.getKey().equals(StrawberryApplication.SELECTED_USER_TAG)) {
                friend = User.getUser((String) event.getValue());
                messages = new ArrayList<>();
                showEmpty(false);
                hideLoading(false);
                setRecyclerProperties();
            }
        }
    }

    public void makeNotification(Message message, boolean autGeneratedMessage) {

        Notification notification = null;
        String content = message.getMessage();
        Intent intent=new Intent(StrawberryApplication.getInstance().getApplicationContext(),
                MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StrawberryApplication.getInstance().
                getApplicationContext(), 0, intent, 0);
        Context context = getContext();
        String title = "New message from " + message.getSender().getUsername() + "!";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            notification = new Notification();
            notification.icon = R.mipmap.ic_launcher;
            try {
                Method deprecatedMethod = notification.getClass().getMethod("setLatestEventInfo",
                        Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
                deprecatedMethod.invoke(notification, getContext(), content, null, pendingIntent);
            } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                Log.w(TAG, "Method not found", e);
            }
        } else {
            // Use new API
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(content);

            notification = builder.build();
        }

        //notification.vibrate = new long[]{1000};
        Uri sound;
        if(autGeneratedMessage)
            sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        else
            sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.sound = sound;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
