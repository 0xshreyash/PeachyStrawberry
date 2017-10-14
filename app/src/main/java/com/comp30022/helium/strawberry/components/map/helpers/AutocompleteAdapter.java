package com.comp30022.helium.strawberry.components.map.helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.MainActivity;
import com.comp30022.helium.strawberry.activities.fragments.MapFragment;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class AutocompleteAdapter extends ArrayAdapter<String> {

    Context mContext;
    int layoutResId;
    String data[];
    MapFragment parentFragment;
    TextChangeListener textChangeListener;

    public AutocompleteAdapter(Context mContext, int layoutResId,
                               String[] data, MapFragment parentFragment) {

        super(mContext, layoutResId, data);

        this.layoutResId = layoutResId;
        this.mContext = mContext;
        this.data = data;
        this.parentFragment = parentFragment;
        this.textChangeListener = null;

    }

    public AutocompleteAdapter(Context mContext, int layoutResId,
                               String[] data, MapFragment parentFragment, TextChangeListener textChangeListener) {
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
        if(view == null) {
            // inflate the layout if it is not there in the first place
            LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
            view= inflater.inflate(layoutResId, parent, false);
        }

        view.setOnClickListener(new SearchOptionClickListener(parentFragment, data, textChangeListener));

        String listItem = data[position];

        TextView textView = (TextView) view.findViewById(R.id.username);
        textView.setText(listItem);

        return view;
    }
}
