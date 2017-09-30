package com.comp30022.helium.strawberry.components.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.comp30022.helium.strawberry.entities.User;
import java.util.List;

/**
 * Created by shreyashpatodia on 30/09/17.
 */

public class FriendsListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<User> friendList;

    private static final int ADDABLE_FRIEND = 1;


    @Override
    public int getItemCount() {
        //Log.e("Check", "" + mMessageList.size());
        return friendList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        return ADDABLE_FRIEND;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sent_message, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_received_message, parent, false);
            return new ReceivedMessageHolder(view);
        }
        //Log.e("Check", "Returning null");
        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);
        //Log.e("Check", "Checking if Bind View Holder works or not");
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private class FriendHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        ImageView profileImage;

        FriendHolder(View itemView) {
            super(itemView);

            userNameText = (TextView)itemView.findViewById(R.id.username);
            profileImage = (ImageView)itemView.findViewById(R.id.image_user_profile);
        }

        void bind(User friend) {
            userNameText.setText(friend.getName());
        }
    }

    private class AddableFriendHolder extends FriendHolder{

        Button addFriendButton;

        AddableFriendHolder(View itemView) {
            super(itemView);
            addFriendButton = (Button) itemView.findViewById(R.id.add_friend_button);
        }

        void bind(User friend) {
            super.bind(friend);
            
            // Format the stored timestamp into a readable String using method.
            // TODO: Profile Picture
        }
    }
}
