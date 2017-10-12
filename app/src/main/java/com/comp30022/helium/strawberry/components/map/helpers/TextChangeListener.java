package com.comp30022.helium.strawberry.components.map.helpers;

/**
 * Created by shreyashpatodia on 13/10/17.
 */
import android.app.Fragment;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.comp30022.helium.strawberry.activities.MainActivity;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;

import java.util.Map;

public class TextChangeListener implements TextWatcher {

    MapFragment parentFragment;

    public TextChangeListener(MapFragment parentFragment){
        this.parentFragment = parentFragment;
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


        // TODO: update myAdapter with the actual name of the adapter
        // used in main
        parentFragment.getAutoCompleteAdapter().notifyDataSetChanged();

        // TODO: update this with the actual getter for the users list
        String[] usersList = parentFragment.getFriendList();

        mainActivity.myAdapter = new AutocompleteAdapter(mainActivity, R.layout.list_view, usersList);

        // TODO: update myAdapter with the actual name of the adapter
        // used in main and also myAutoComplete
        mainActivity.myAutoComplete.setAdapter(mainActivity.myAdapter);
    }
}
