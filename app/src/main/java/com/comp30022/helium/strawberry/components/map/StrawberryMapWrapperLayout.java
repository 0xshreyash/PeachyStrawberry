package com.comp30022.helium.strawberry.components.map;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Wrap map fragment in a relative layout, to be able to get the
 * offset.
 */
public class StrawberryMapWrapperLayout extends RelativeLayout {

    private GoogleMap map;
    private int offsetPixels;
    private Marker marker;
    private View clickableView;


    public StrawberryMapWrapperLayout(Context context) {
        super(context);
    }

    public void init(GoogleMap map, int offsetPixels) {
        this.map = map;
        this.offsetPixels = offsetPixels;
    }

    @Override
    public  boolean dispatchTouchEvent(MotionEvent originalEvent) {
        boolean toReturn = false;
        if(map != null) {
            if (marker != null && marker.isInfoWindowShown()) {
                if (clickableView != null) {
                    Point point = map.getProjection().toScreenLocation(marker.getPosition());

                    MotionEvent newE = MotionEvent.obtain(originalEvent);

                    newE.offsetLocation(
                            -point.x + clickableView.getWidth() / 2.0f,
                            -point.y + clickableView.getHeight() + offsetPixels
                    );

                    toReturn = clickableView.dispatchTouchEvent(newE);
                }
            }
        }
        return toReturn || super.dispatchTouchEvent(originalEvent);
    }
}
