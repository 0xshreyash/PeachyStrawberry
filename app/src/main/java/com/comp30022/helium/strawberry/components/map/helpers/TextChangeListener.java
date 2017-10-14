package com.comp30022.helium.strawberry.components.map.helpers;

/**
 * Created by shreyashpatodia on 13/10/17.
 */

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;
import com.comp30022.helium.strawberry.entities.User;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class TextChangeListener implements TextWatcher {

    MapFragment parentFragment;
    Context context;
    private User[] relevantFriends;

    public TextChangeListener(MapFragment parentFragment, Context context) {
        this.parentFragment = parentFragment;
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
        parentFragment.getAutoCompleteAdapter().notifyDataSetChanged();

        User[] friendList = parentFragment.getFriendArray();

        Log.e(TAG, "friends list length " + friendList.length);
        Log.e(TAG, "UserInput " + userInput.toString());

        relevantFriends = findRelevantResults(friendList, userInput.toString().trim());

        parentFragment.setAutoCompleteAdapter(new AutocompleteAdapter(context, R.layout.item_friend, relevantFriends, parentFragment, this));
        parentFragment.getAutocompleteView().setAdapter(parentFragment.getAutoCompleteAdapter());
    }

    public User[] findRelevantResults(User[] friendList, String userInput) {
        ArrayList<User> relevantFriends = new ArrayList<>();

        if (userInput.length() > 0) {
            for (User friend : friendList) {
                Log.e(TAG, "The friend I am comparing to " + friend);

                if (friend.getUsername().toLowerCase().contains(userInput.toLowerCase())) {
                    relevantFriends.add(friend);
                }
            }
        }

        User[] relevantFriendArray = new User[relevantFriends.size()];
        relevantFriendArray = relevantFriends.toArray(relevantFriendArray);

        return relevantFriendArray;
    }

    public User getTopUser() {
        if (relevantFriends.length > 0)
            return relevantFriends[0];
        return null;
    }
}

