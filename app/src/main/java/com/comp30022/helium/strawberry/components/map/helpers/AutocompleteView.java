package com.comp30022.helium.strawberry.components.map.helpers;

/**
 * Created by shreyashpatodia on 13/10/17.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatAutoCompleteTextView;

public class AutocompleteView extends AppCompatAutoCompleteTextView {

    public AutocompleteView(Context context) {
        super(context);
    }

    public AutocompleteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AutocompleteView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
}
