package com.comp30022.helium.strawberry.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shreyashpatodia on 15/09/17.
 */

public class MessageListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Message>  messageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = new ArrayList<>(messageList);
    }


}
