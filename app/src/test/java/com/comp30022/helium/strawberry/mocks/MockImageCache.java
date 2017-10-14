package com.comp30022.helium.strawberry.mocks;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by noxm on 10/10/17.
 */

public class MockImageCache {
    private static final String TAG = "MockImageCache";

    private static MockImageCache instance = null;
    private final HashMap<String, Bitmap> cache;

    private MockImageCache() {
        if (instance != null) {
            throw new RuntimeException("Already instantiated!");
        }
        cache = new HashMap<>();
        Log.i(TAG, "created");
    }

    public void put(String key, Bitmap bitmap) {
        synchronized (cache) {
            if (key == null) {
                Log.w(TAG, "Key for bitmap cannot be null");
            } else if (cache.get(key) == null) {
                Log.i(TAG, "User " + key + " created and cached");
                cache.put(key, bitmap);
            } else {
                Log.w(TAG, "User " + key + " already in cache");
            }
        }
    }

    public Bitmap get(String key) {
        synchronized (cache) {
            if (key == null)
                return null;
            Bitmap bitmap = cache.get(key);
            Log.i(TAG, "Get bitmap " + key);
            // null if not found
            return bitmap;
        }
    }

    public static MockImageCache getInstance() {
        if (instance == null) {
            synchronized (MockImageCache.class) {
                if (instance == null)
                    instance = new MockImageCache();
            }
        }
        return instance;
    }
}
