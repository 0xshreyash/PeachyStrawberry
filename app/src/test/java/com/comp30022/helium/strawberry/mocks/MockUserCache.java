package com.comp30022.helium.strawberry.mocks;

import android.util.Log;

import com.comp30022.helium.strawberry.entities.User;

import java.util.HashMap;

/**
 * Created by noxm on 10/10/17.
 */

public class MockUserCache {
    private static final String TAG = "MockUserCache";

    private static volatile MockUserCache instance = null;
    private static HashMap<String, MockUser> cache;

    private MockUserCache() {
        if (instance != null) {
            throw new RuntimeException("Already instantiated!");
        }
        cache = new HashMap<>();
        Log.i(TAG, "Created");
    }

    public void put(String key, MockUser user) {
        synchronized (cache) {
            if (key == null) {
                Log.w(TAG, "Key for " + user + " cannot be null");
            } else if (cache.get(key) == null) {
                Log.i(TAG, "User " + user.getId() + " created and cached");
                cache.put(key, user);
            } else {
                Log.w(TAG, "User " + user.getId() + " already in cache");
            }
        }
    }

    public MockUser get(String key) {
        synchronized (cache) {
            if (key == null)
                return null;

            MockUser user = cache.get(key);
            Log.i(TAG, "Get User " + user);
            // null if not found
            return user;
        }
    }

    public static MockUserCache getInstance() {
        if (instance == null) {
            synchronized (MockUserCache.class) {
                if (instance == null)
                    instance = new MockUserCache();
            }
        }
        return instance;
    }
}
