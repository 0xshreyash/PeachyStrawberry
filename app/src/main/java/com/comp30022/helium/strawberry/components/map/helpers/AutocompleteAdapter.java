package com.comp30022.helium.strawberry.components.map.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.MainActivity;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;
import com.comp30022.helium.strawberry.entities.StrawberryCallback;
import com.comp30022.helium.strawberry.entities.User;
import com.comp30022.helium.strawberry.entities.exceptions.FacebookIdNotSetException;

import java.net.MalformedURLException;

import static android.content.ContentValues.TAG;

public class AutocompleteAdapter extends ArrayAdapter<User> {

    Context mContext;
    int layoutResId;
    User data[];
    MapFragment parentFragment;
    TextChangeListener textChangeListener;

    public AutocompleteAdapter(Context mContext, int layoutResId, User[] data, MapFragment parentFragment) {
        super(mContext, layoutResId, data);

        this.layoutResId = layoutResId;
        this.mContext = mContext;
        this.data = data;
        this.parentFragment = parentFragment;
        this.textChangeListener = null;
    }

    public AutocompleteAdapter(Context mContext, int layoutResId, User[] data, MapFragment parentFragment, TextChangeListener textChangeListener) {
        super(mContext, layoutResId, data);

        this.layoutResId = layoutResId;
        this.mContext = mContext;
        this.data = data;
        this.parentFragment = parentFragment;
        this.textChangeListener = textChangeListener;

    }

    @Override
    public int getCount() {
        Log.e(TAG, "getCount: " + data.length);
        return data.length;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.e(TAG, "getting the adapter view");
        if (view == null) {
            // inflate the layout if it is not there in the first place
            LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResId, parent, false);
        }

        User currUser = data[position];

        final ImageView profileImage = (ImageView) view.findViewById(R.id.image_user_profile);

        view.setOnClickListener(new SearchOptionClickListener(parentFragment, currUser, textChangeListener));

        try {
            currUser.getFbPicture(User.ProfilePictureType.SQUARE, new StrawberryCallback<Bitmap>() {
                @Override
                public void run(final Bitmap bitmap) {
                    parentFragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profileImage.setImageBitmap(bitmap);
                        }
                    });
                }
            });
        } catch (FacebookIdNotSetException | MalformedURLException e) {
            e.printStackTrace();
        }

        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(currUser.getUsername());

        return view;
    }

    public User getTopUser() {
        if(textChangeListener != null)
            return textChangeListener.getTopUser();
        return null;
    }
}
