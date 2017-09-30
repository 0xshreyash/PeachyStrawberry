package com.comp30022.helium.strawberry.components.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class FriendListAdapter extends RecyclerView.Adapter {

    // The context
    private Context context;

    // List of friends
    private List<User> friendList;

    private static final String TAG = "FriendListAdapter";

    // TODO: Use the ADDABLE_FRIEND AND ADDED_FRIEND THING LATER
    private static final int ADDABLE_FRIEND = 1;
    private static final int ADDED_FRIEND = 2;

    // Makeshift friend for the demoonstration
    private static final int FRIEND = 3;

    /**
     * Constructor for the adapter
     * @param context the context for the friend list
     * @param friendList the actual list of friends we need to display
     */
    public FriendListAdapter(Context context, List<User> friendList) {
        this.friendList = new ArrayList<>(friendList);
        for(int i = 0; i < friendList.size(); i++) {
            Log.d(TAG, friendList.get(i).toString());
        }
        this.context = context;
    }

    /**
     * Returns how many items are in the actual friend list.
     * @return the number of friends
     */
    @Override
    public int getItemCount() {
        Log.e("Check", "The size of the friend list is: " + friendList.size());
        //Log.e(TAG, "" + friendList.size());
        return friendList.size();
    }

    /**
     * Determines the appropriate ViewType according to the sender of the message.
     *
     */
    @Override
    public int getItemViewType(int position) {
        //User friend = friendList.get(position);
        Log.e(TAG, "Getting the view type of the user");
        // TODO: Check if the user if a friend or not. If the user is nto a friend
        return FRIEND;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        /*
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
        */

        //Log.e("Check", "Returning null");

        view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend, parent, false);
        return new FriendHolder(view);
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        User friend = friendList.get(position);

        switch(this.getItemViewType(position)) {
            case FRIEND:
                Log.e(TAG, "The person was a friend");
                ((FriendHolder)holder).bind(friend);
            /*
            case ADDABLE_FRIEND:
                ((AddableFriendHolder) holder).bind(friend);
                break;
            case ADDED_FRIEND:
                ((FriendHolder) holder).bind(friend);
                break;
            */
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
            userNameText.setText(friend.getUsername());
        }
    }

    /*
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
    */
}
