package com.comp30022.helium.strawberry.components.map.helpers;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;

/**
 * Created by shreyashpatodia on 14/10/17.
 */

public class SearchOptionClickListener implements View.OnClickListener {

    private MapFragment parentFragment;
    private static final String TAG = "SearchOptionClicked";

    public SearchOptionClickListener(MapFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "OnClick for search Item is called");
        TextView username = (TextView) view.findViewById(R.id.username);
        parentFragment.showWindowForFriend(username.getText().toString());
        //View parentView = (View)view.getParent();
        //parentView.setVisibility(View.GONE);

    }
}
