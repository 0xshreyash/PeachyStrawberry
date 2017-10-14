package com.comp30022.helium.strawberry.others;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationPropertyTest {
    private static final String TAG = ApplicationPropertyTest.class.getSimpleName();

    public ApplicationPropertyTest() {
        Log.i(TAG, "----------------------------------------\n" +
                   "            Application Test\n"+
                   "----------------------------------------\n");
    }

    @Test
    public void packageName_isCorrect() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Log.d(TAG, "[Test] Package Name is correct - START");

        try {
            assertEquals("com.comp30022.helium.strawberry", appContext.getPackageName());
        } catch (Exception e) {
            Log.e(TAG, "[Test] Package Name is correct - FAILURE");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        Log.i(TAG, "[Test] Package Name is correct - SUCCESS");
    }
}
