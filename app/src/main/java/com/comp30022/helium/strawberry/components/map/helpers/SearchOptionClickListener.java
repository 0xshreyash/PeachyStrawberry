package com.comp30022.helium.strawberry.components.map.helpers;

import android.util.Log;
import android.view.View;

import com.comp30022.helium.strawberry.activities.fragments.MapFragment;
import com.comp30022.helium.strawberry.entities.User;

/**
 * Created by shreyashpatodia on 14/10/17.
 */

public class SearchOptionClickListener implements View.OnClickListener {

    private final User currUser;
    private MapFragment parentFragment;
    private static final String TAG = "SearchOptionClicked";
    private TextChangeListener textChangeListener;

    public SearchOptionClickListener(MapFragment parentFragment, User currUser, TextChangeListener textChangeListener) {
        this.currUser = currUser;
        this.parentFragment = parentFragment;
        this.textChangeListener = textChangeListener;
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "OnClick for search Item is called");

        parentFragment.showWindowForFriend(currUser);
        parentFragment.toggleSearchBar();

        //options = new String[options.length];
        if (textChangeListener != null) {
            textChangeListener.onTextChanged(new CharSequence() {
                @Override
                public int length() {
                    return 0;
                }

                @Override
                public char charAt(int i) {
                    return 0;
                }

                @Override
                public CharSequence subSequence(int i, int i1) {
                    return null;
                }
            }, 0, 0, 0);
        }
        //View parentView = (View)view.getParent();
        //parentView.setVisibility(View.GONE);

    }
}
