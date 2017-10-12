package com.comp30022.helium.strawberry.components.map.helpers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.activities.MainActivity;

public class AutocompleteAdapter extends ArrayAdapter<String> {

    Context mContext;
    int layoutResId;
    String data[];

    public AutocompleteAdapter(Context mContext, int layoutResId,
                               String[] data) {
        super(mContext, layoutResId, data);

        this.layoutResId = layoutResId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null) {
            // inflate the layout if it is not there in the first place
            LayoutInflater inflater = ((MainActivity) mContext).getLayoutInflater();
            view= inflater.inflate(layoutResId, parent, false);
        }

        String listItem = data[position];

        TextView textView = (TextView) view.findViewById(R.id.user_data_item);
        textView.setText(listItem);

        return view;
    }
}
