package com.comp30022.helium.strawberry.components.map.helpers;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;

/**
 * Created by shreyashpatodia on 14/10/17.
 */

public class SearchOptionClickListener implements View.OnClickListener {

    private MapFragment parentFragment;
    private static final String TAG = "SearchOptionClicked";
    private String[] options;
    private TextChangeListener textChangeListener;

    public SearchOptionClickListener(MapFragment parentFragment, String[] options, TextChangeListener textChangeListener) {
        this.parentFragment = parentFragment;
        this.options = options;
        this.textChangeListener = textChangeListener;
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "OnClick for search Item is called");
        parentFragment.resetSearchBar();
        TextView username = (TextView) view.findViewById(R.id.username);

        parentFragment.showWindowForFriend(username.getText().toString());

        //options = new String[options.length];
        if(textChangeListener != null) {
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
