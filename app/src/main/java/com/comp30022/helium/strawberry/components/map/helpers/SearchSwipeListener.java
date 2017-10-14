package com.comp30022.helium.strawberry.components.map.helpers;

/**
 * Created by shreyashpatodia on 14/10/17.
 */
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * This TouchListener is Using for very First time ThemeSelection Screen with SwipeLeft Finger
 */
public abstract class SearchSwipeListener implements OnTouchListener {

    private static final String TAG = "SearchSwipeListener";

    private final GestureDetector gestureDetector;

    public SearchSwipeListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 2;
        private static final int SWIPE_VELOCITY_THRESHOLD = 1;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "On fling is called");
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD
                            && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD
                            && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight()
    {
        Log.e(TAG, "Swiped right");
    }

    public void onSwipeLeft() {
        Log.e(TAG, "Swiped left");
    }

    public void onSwipeTop() {
        Log.e(TAG, "Swiped top");
    }

    public void onSwipeBottom() {
        Log.e(TAG, "Swiped bottom");
    }
}