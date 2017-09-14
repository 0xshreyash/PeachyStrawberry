package com.comp30022.helium.strawberry.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by shreyashpatodia on 15/09/17.
 */

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    private TextView messageText, timeText, nameText;
    private ImageView profileImage;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);

    }
}
