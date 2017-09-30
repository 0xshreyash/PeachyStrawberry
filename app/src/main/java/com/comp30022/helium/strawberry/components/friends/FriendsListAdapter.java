package com.comp30022.helium.strawberry.components.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A Recycler view is just like a list view but has additional features that
 * makes it faster and is why I used the RecyclerVieww
 */
public class FriendsListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<User> friendList;

    private static final int ADDABLE_FRIEND = 1;
    private static final int ADDED_FRIEND = 2;


    public FriendsListAdapter() {
        friendList = new ArrayList<>();
    }
    @Override
    public int getItemCount() {
        //Log.e("Check", "" + mMessageList.size());
        return friendList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        User friend = friendList.get(position);
        // TODO: Check if the user if a friend or not. If the user is nto a friend

        return ADDABLE_FRIEND;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == ADDABLE_FRIEND) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.addable_friend, parent, false);
            return new AddableFriendHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.added_friend, parent, false);
            return new FriendHolder(view);
        }
        //Log.e("Check", "Returning null");
        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        User friend = (User) friendList.get(position);

        switch(holder.getItemViewType()) {
            case ADDABLE_FRIEND:
                ((AddableFriendHolder) holder).bind(friend);
                break;
            case ADDED_FRIEND:
                ((FriendHolder) holder).bind(friend);
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
