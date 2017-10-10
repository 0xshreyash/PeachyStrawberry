package com.comp30022.helium.strawberry.components;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.comp30022.helium.strawberry.components.server.rest.PeachRestInterface;
import com.comp30022.helium.strawberry.components.server.rest.components.PeachCookieStore;
import com.comp30022.helium.strawberry.components.server.rest.components.StrawberryListener;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.HashMap;

import static com.comp30022.helium.strawberry.mocks.MockPeachServerInterface.SAMPLE_FB_TOKEN;
import static com.comp30022.helium.strawberry.mocks.MockPeachServerInterface.SAMPLE_ID_SELF;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test for REST requests
 *
 * @author Max Lee
 */
@RunWith(AndroidJUnit4.class)
public class PeachRestInterfaceTest {
    private static final String TAG = "PeachRestInterfaceTest";

    private static final int testCount = 7;

    private static Boolean[] results = new Boolean[testCount];
    private static Boolean[] returned = new Boolean[testCount];

    @BeforeClass
    public static void queryResults() {
        Log.i(TAG, "----------------------------------------\n" +
                "            Peach Rest Test\n" +
                "----------------------------------------\n");
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", SAMPLE_FB_TOKEN);

        for (int i = 0; i < testCount; i++) {
            results[i] = new Boolean(false);
            returned[i] = new Boolean(false);
        }

        // Reset cookie
        CookieStore cookieStore = new PeachCookieStore();
        CookieManager manager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

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

        // #2
        PeachRestInterface.post("/authorize", tokenMap, new StrawberryListener(getSuccess(2), getError(2)));

        // need to wait for auth to finish
        waitResult(2);

        // #3
        PeachRestInterface.get("/user", new StrawberryListener(getSuccess(3), getError(3)));

        // #4
        PeachRestInterface.get("/user/" + SAMPLE_ID_SELF + "/location", new StrawberryListener(getSuccess(4), getError(4)));

        // #5
        PeachRestInterface.get("/user/" + SAMPLE_ID_SELF + "/friend", new StrawberryListener(getSuccess(5), getError(5)));

        // #6
        PeachRestInterface.get("/facebook/permission", new StrawberryListener(getSuccess(6), getError(6)));
    }

    private static void waitResult(int i) {
        while (!returned[i])
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    @Test
    public void server_canConnect() throws Exception {
        waitResult(0);
        assertTrue(results[0]);
    }

    @Test
    public void invalidAuth_throwRequireAuth() throws Exception {
        waitResult(1);
        assertTrue(results[1]);
    }

    @Test
    public void authorize_successfullyAuthorize() throws Exception {
        waitResult(2);
        assertTrue(results[2]);
    }

    @Test
    public void queryUser_selfQueryValid() throws Exception {
        waitResult(3);
        assertTrue(results[3]);
    }

    @Test
    public void queryUserLocation_success() throws Exception {
        waitResult(4);
        assertTrue(results[4]);
    }

    @Test
    public void queryUserFriend_success() throws Exception {
        waitResult(5);
        assertTrue(results[5]);
    }

    @Test
    public void queryFacebookPermissions_valid() throws Exception {
        waitResult(6);
        assertTrue(results[6]);
    }

    private static Response.ErrorListener getError(int i) {
        return new TestErrorListener(i);
    }

    private static Response.Listener<String> getSuccess(int i) {
        return new TestSuccessListener(i);
    }

    private static class TestSuccessListener implements Response.Listener<String> {
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

    private static class TestErrorListener implements Response.ErrorListener {
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
