package com.comp30022.helium.strawberry.components.chat;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.fragments.ChatFragment;
import com.comp30022.helium.strawberry.components.server.PeachServerInterface;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;
import com.comp30022.helium.strawberry.helpers.BitmapHelper;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Custom adapter for list of messages, stores the number. This
 * adapter "adapts" what messages are to be displayed to on the screen
 * and the format they are to be displayed in.
 */
public class MessageListAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_MESSAGE_SENT = 1;
    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ChatFragment mContext;
    private List<Message> mMessageList;

    public MessageListAdapter(ChatFragment context, List<Message> messageList) {
        mContext = context;
        //Log.e("Check", "Checking if Bind View Holder works or not");
        //Log.d("New list received", messageList.getString(0).getMessage());
        mMessageList = new ArrayList<>(messageList);
    }

    @Override
    public int getItemCount() {
        //Log.e("Check", "" + mMessageList.size());
        return mMessageList.size();
    }

    /**
     * Determines the appropriate ViewType according to the sender of the message.
     */
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        //Log.e("Check", "User ID is nothing");
        // TODO: Get current user ID and check if they are equal
        String currentUserId = PeachServerInterface.currentUser().getId();
        if (message.getSender().getId().equals(currentUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    /**
     * Inflates the appropriate layout according to the ViewType.
     */
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


    /**
     * Passes the message object to a ViewHolder so that the contents can be bound to UI.
     */
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

    /**
     * View holder for messages sent by the current user.
     */
    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        View view;

        SentMessageHolder(View itemView) {
            super(itemView);

            view = itemView;
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());

            Date msgDate = new Date(message.getCreatedAt());
            String dateString;
            if(DateUtils.isToday(msgDate.getTime())){
                dateString = DateFormat.format("hh:mm a", msgDate).toString();
            } else {
                dateString = DateFormat.format("MM/dd/yyyy\nhh:mm a", msgDate).toString();
            }
            timeText.setText(dateString);
        }

        public View getItemView() {
            return view;
        }

        public void setItemView(View view) {
            this.view = view;
        }
    }

    /**
     * View holder for messages received by the current user.
     */
    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private static final long DAY_MILLISECS = 86400000; // 1DAY
        TextView messageText, timeText, nameText;
        ImageView profileImage;
        View view;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            view = itemView;
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(final Message message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            Date msgDate = new Date(message.getCreatedAt());
            String dateString;
            if(DateUtils.isToday(msgDate.getTime())){
                dateString = DateFormat.format("hh:mm a", msgDate).toString();
            } else {
                dateString = DateFormat.format("MM/dd/yyyy\nhh:mm a", msgDate).toString();
            }
            timeText.setText(dateString);

            nameText.setText(message.getSender().getUsername());

            message.getSender().getFacebookId(new StrawberryCallback<String>() {
                @Override
                public void run(String s) {
                    try {
                        message.getSender().getFbPicture(User.ProfilePictureType.SQUARE, new StrawberryCallback<Bitmap>() {
                            @Override
                            public void run(final Bitmap bitmap) {
                                mContext.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        profileImage.setImageBitmap(BitmapHelper.makeCircular(bitmap));
                                    }
                                });
                            }
                        });
                    } catch (FacebookIdNotSetException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        public View getItemView() {
            return view;
        }

        public void setItemView(View view) {
            this.view = view;
        }
    }
}
