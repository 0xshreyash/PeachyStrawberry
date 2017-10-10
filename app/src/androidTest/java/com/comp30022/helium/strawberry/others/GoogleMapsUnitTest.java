package com.comp30022.helium.strawberry.others;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.comp30022.helium.strawberry.R;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test for Google Maps
 *
 * @author Max Lee
 */
@RunWith(AndroidJUnit4.class)
public class GoogleMapsUnitTest {
    private static final String TAG = GoogleMapsUnitTest.class.getSimpleName();

    public GoogleMapsUnitTest() {
        Log.i(TAG, "----------------------------------------\n" +
                   "         Google Maps Unit Test\n"+
                   "----------------------------------------\n");
    }

    @Test
    public void apiKey_isNotEmpty() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String key = appContext.getString(R.string.google_maps_key);
        Log.d(TAG, "[Test] API key is not empty - START");

        if (key.length() == 0) {
            Log.e(TAG, "[Test] API key is empty - FAILURE");
            throw new Exception("Key is empty");
        }

        Log.i(TAG, "[Test] API key is not empty - PASSED");
    }
}
