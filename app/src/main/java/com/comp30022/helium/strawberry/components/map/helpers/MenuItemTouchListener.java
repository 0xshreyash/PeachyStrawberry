package com.comp30022.helium.strawberry.components.map.helpers;

import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.comp30022.helium.strawberry.helpers.ColourScheme;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.maps.model.Marker;

/**
 * This class's onTouch even is called whenever the menu for a marker is pressed.
 *
 */
public abstract class MenuItemTouchListener implements View.OnTouchListener {

    private final View view;
    private final Handler handler = new Handler();

    private Marker marker;
    private boolean pressed = false;

    public MenuItemTouchListener(View view) {
        this.view = view;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        return onTouch(event, vv);
    }

    public boolean onTouch(MotionEvent event, View vv) {
        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                0 <= event.getY() && event.getY() <= vv.getHeight())
        {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: startPress(view);
                    break;

                case MotionEvent.ACTION_UP: handler.postDelayed(confirmClickRunnable, 150);
                    break;

                case MotionEvent.ACTION_CANCEL: endPress();
                    break;
                default:
                    break;
            }
        }
        else {
            endPress();
        }
        return false;
    }

    public void startPress(View view) {
        if (!pressed) {
            pressed = true;
            view.setBackgroundColor(ColourScheme.PRIMARY_LIGHT);
            handler.removeCallbacks(confirmClickRunnable);
            if (marker != null)
                marker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (pressed) {
            this.pressed = false;
            view.setBackgroundColor(ColourScheme.PRIMARY_DARK);
            handler.removeCallbacks(confirmClickRunnable);
            if (marker != null)
                marker.showInfoWindow();
            return true;
        }
        else
            return false;
    }

    /**
     * Confirm click if the press has ended.
     */
    private final Runnable confirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(view, marker);
            }
        }
    };

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}
