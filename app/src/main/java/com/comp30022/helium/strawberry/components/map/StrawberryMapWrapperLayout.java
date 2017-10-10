package com.comp30022.helium.strawberry.components.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Relative layout to wrap around map object.
 */
public class StrawberryMapWrapperLayout extends RelativeLayout {

    private GoogleMap map;

    /**
     * Offset needed to position the clickableView
     */
    private int offsetPixels;

    private Marker marker;

    /**
     * Our custom view which is returned from either the InfoWindowAdapter.getInfoContents
     * or InfoWindowAdapter.getInfoWindow
     */
    private View infoWindow;

    public StrawberryMapWrapperLayout(Context context) {
        super(context);
    }

    public StrawberryMapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StrawberryMapWrapperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Must be called onMapReady?
     */
    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.offsetPixels = bottomOffsetPixels;
    }

    /**
     * Called from InfoWindowAdapter.getInfoContents
     */
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    /**
     * Important because this dispatches the touchEvent on clicking the window
     * @param originalEvent
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent originalEvent) {
        boolean ret = false;

        if(map != null) {
            if (marker != null && marker.isInfoWindowShown()) {
                if (infoWindow != null) {
                    Point mapPoint = map.getProjection().toScreenLocation(marker.getPosition());

                    MotionEvent adjustedEvent = MotionEvent.obtain(originalEvent);
                    adjustedEvent.offsetLocation(-mapPoint.x + (infoWindow.getWidth() / 2),
                            -mapPoint.y + infoWindow.getHeight() + offsetPixels);

                    ret = infoWindow.dispatchTouchEvent(adjustedEvent);
                }
            }
        }
        return ret || super.dispatchTouchEvent(originalEvent);
    }
}
