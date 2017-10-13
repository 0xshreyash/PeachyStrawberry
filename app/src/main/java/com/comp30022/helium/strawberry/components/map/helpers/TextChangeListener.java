package com.comp30022.helium.strawberry.components.map.helpers;

/**
 * Created by shreyashpatodia on 13/10/17.
 */
import android.app.Fragment;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.MainActivity;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;

import java.util.ArrayList;
import java.util.Map;

public class TextChangeListener implements TextWatcher {

    MapFragment parentFragment;
    Context context;

    public TextChangeListener(MapFragment parentFragment, Context context){
        this.parentFragment = parentFragment;
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        parentFragment.getAutoCompleteAdapter().notifyDataSetChanged();
        String[] friendList = parentFragment.getFriendList();

        parentFragment.setAutoCompleteAdapter(new AutocompleteAdapter(context, R.layout.item_friend,
                findRelevantResults(friendList, userInput.toString())));
        parentFragment.getAutocompleteView().setAdapter(parentFragment.getAutoCompleteAdapter());
    }

    public String[] findRelevantResults(String[] friendList, String userInput) {
        ArrayList<String> relevantFriends = new ArrayList<>();
        for(String friend : friendList) {
            if(userInput.toLowerCase().contains(friend.toLowerCase())) {
                relevantFriends.add(friend);
            }
        }
        String[] relevantFriendArray = new String[relevantFriends.size()];
        relevantFriendArray = relevantFriends.toArray(relevantFriendArray);
        return relevantFriendArray;
    }
}

