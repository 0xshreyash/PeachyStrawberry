package com.comp30022.helium.strawberry.helpers;

import android.util.DisplayMetrics;

/**
 * Created by noxm on 7/10/17.
 */

public class DisplayHelper {
    public static float pixelToDP(int pixels, DisplayMetrics dm) {
        return pixels * dm.density / dm.densityDpi;
    }

    public static int dpToPixel(float dp, DisplayMetrics dm) {
        return (int) (dp / dm.density * dm.densityDpi);
    }
}
