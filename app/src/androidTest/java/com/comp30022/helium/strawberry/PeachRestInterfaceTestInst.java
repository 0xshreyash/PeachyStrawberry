package com.comp30022.helium.strawberry;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test for REST requests
 *
 * @author Max Lee
 */
@RunWith(AndroidJUnit4.class)
public class PeachRestInterfaceTestInst {
    private static final String TAG = PeachRestInterfaceTestInst.class.getSimpleName();

    private static final int testCount = 4;

    Boolean[] results = new Boolean[testCount];
    Boolean[] returned = new Boolean[testCount];

    // Shreyash's token
    private String sampleFBToken = "EAAJ5bdPqZCIIBAPKtZBS1ZCsH2Q4mB1adeG8cFBFrZB29TMvZCmxCOEwygknWpS0N7Xesw8Qy9YdLBpZC34iESnRZBTKDBTH6yZAJj5veuLE3p6LUUkZCVKtZCPsH7l54GQPjcZCCwsZAWGMdI512XvBwRyR3CCoZC03AqwdA0STGDtiJoAsZBZArTClA4OsEhTopUOMcwaZC7DBK0gkqprZBK5Lk4ZB8G";

    public PeachRestInterfaceTestInst() {
        Log.i(TAG, "----------------------------------------\n" +
                "            Peach Rest Test\n" +
                "----------------------------------------\n");
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", sampleFBToken);

        for (int i = 0; i < testCount; i++) {
            results[i] = new Boolean(false);
            returned[i] = new Boolean(false);
        }

        // #0
        PeachRestInterface.get(null, new StrawberryListener(getSuccess(0), getError(0)));

        // #1
        PeachRestInterface.get("/user", new StrawberryListener(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                results[1] = false;
                returned[1] = true;
                String msg = (response == null) ? "null" : response;
                Log.d(TAG, 1 + " SUCCESS: " + msg);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                results[1] = true;
                returned[1] = true;
                String data = new String(error.networkResponse.data);

                String msg = (error.getMessage() == null) ? error.networkResponse.statusCode + " Error" : error.getMessage();

                Log.d(TAG, 1 + " ERROR: " + msg + "\n" + data);

            }
        }));
        waitResult(1);

        // #2
        PeachRestInterface.post("/authorize", tokenMap, new StrawberryListener(getSuccess(2), getError(2)));
        waitResult(2);

        // #3
        PeachRestInterface.get("/user", new StrawberryListener(getSuccess(3), getError(3)));
    }

    private void waitResult(int i) {
        while (!returned[i])
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Test
    public void test_results() throws Exception {
        for (int i = 0; i < testCount; i++) {
            while (!returned[i])
                Thread.sleep(100);
            assertTrue(results[i]);
        }
    }

    private Response.ErrorListener getError(int i) {
        return new TestErrorListener(i);
    }

    private Response.Listener<String> getSuccess(int i) {
        return new TestSuccessListener(i);
    }

    private class TestSuccessListener implements Response.Listener<String> {
        int i;

        TestSuccessListener(int i) {
            this.i = i;
        }

        @Override
        public void onResponse(String response) {
            results[i] = true;
            returned[i] = true;
            String msg = (response == null) ? "null" : response;
            Log.i(TAG, i + " SUCCESS: " + msg);
        }
    }

    private class TestErrorListener implements Response.ErrorListener {
        int i;

        TestErrorListener(int i) {
            this.i = i;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            results[i] = false;
            returned[i] = true;
            String data = new String(error.networkResponse.data);

            String msg = (error.getMessage() == null) ? error.networkResponse.statusCode + " Error" : error.getMessage();

            Log.e(TAG, i + " ERROR: " + msg + "\n" + data);
        }
    }

}
