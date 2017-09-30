package com.comp30022.helium.strawberry.components.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.entities.User;

import java.util.List;

/**
 * A Recycler view is just like a list view but has additional features that
 * makes it faster and is why I used the RecyclerVieww
 */
public class FriendListAdapter extends RecyclerView.Adapter {

    // The context
    private Context context;

    private String selectedId;

    // List of friends
    private List<User> friendList;

    private static final String TAG = "FriendListAdapter";

    // TODO: Use the ADDABLE_FRIEND AND ADDED_FRIEND THING LATER
    private static final int ADDABLE_FRIEND = 1;
    private static final int ADDED_FRIEND = 2;

    // Makeshift friend for the demoonstration
    private static final int FRIEND = 3;
    private static final int SELECTED_FRIEND = 4;

    /**
     * Constructor for the adapter
     * @param context the context for the friend list
     * @param friendList the actual list of friends we need to display
     */
    public FriendListAdapter(Context context, List<User> friendList) {
        this.friendList = friendList;
        /*
        for(int i = 0; i < friendList.size(); i++) {
            Log.e(TAG, friendList.get(i).toString());
        }
        */
        this.context = context;
        this.selectedId = "";
    }

    /**
     * Returns how many items are in the actual friend list.
     * @return the number of friends
     */
    @Override
    public int getItemCount() {
        //Log.i(TAG, "The size of the friend list is: " + friendList.size());
        //Log.e(TAG, "" + friendList.size());
        return friendList.size();
    }

    /**
     * Determines the appropriate ViewType according to the sender of the message.
     *
     */
    @Override
    public int getItemViewType(int position) {
        User friend = friendList.get(position);
        if(friend != null && selectedId.equals(friend.getUsername())) {
            return SELECTED_FRIEND;
        }
        //Log.i(TAG, "Getting the view type of the user");
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
        if(viewType == FRIEND) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_friend, parent, false);
            return new FriendHolder(view);
        }
        else if(viewType == SELECTED_FRIEND) {
            Log.e(TAG, "Selected Friend Done");
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selected_friend, parent, false);
            return new FriendHolder(view);
        }
        return null;
        //Log.e("Check", "Returning null");

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        User friend = friendList.get(position);
        switch(holder.getItemViewType()) {
            case FRIEND:
                //Log.e(TAG, "The person was a friend");
                ((FriendHolder)holder).bind(friend, position);
                break;
            case SELECTED_FRIEND:
                ((FriendHolder)holder).bind(friend, position);
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

    private class FriendHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView userNameText;
        ImageView profileImage;
        String id;
        String username;
        int position;

        FriendHolder(View itemView) {
            super(itemView);

            userNameText = (TextView)itemView.findViewById(R.id.username);
            profileImage = (ImageView)itemView.findViewById(R.id.image_user_profile);
            itemView.setOnClickListener(this);
        }

        void bind(User friend, int position) {
            userNameText.setText(friend.getUsername());
            id = friend.getId();
            username = friend.getUsername();
            this.position = position;

        }

        @Override
        public void onClick(View view) {
            Log.e(TAG, "onClick of " + username + " with id " + id);
            selectedId = this.id;
            StrawberryApplication.setString(StrawberryApplication.SELECTED_USER_TAG, id);
            Log.e(TAG, StrawberryApplication.getString(StrawberryApplication.SELECTED_USER_TAG));
            //FriendListAdapter.this.notify()
            //notifyDataSetChanged();
            //notifyItemChanged(position);
            Log.e(TAG, position + " " + this.getAdapterPosition());
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
