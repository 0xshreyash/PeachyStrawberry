package com.comp30022.helium.strawberry.helpers.Cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by noxm on 5/10/17.
 */

public class ImageCache {
    private static final String TAG = "ImageCache";
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MiB

    private static ImageCache instance = null;
    private final LruCache<String, Bitmap> cache;

    private ImageCache() {
        if (instance != null) {
            throw new RuntimeException("Already instantiated!");
        }
        cache = new LruCache<>(CACHE_SIZE);
        Log.i(TAG, "created");
    }

    public void put(String key, Bitmap bitmap) {
        synchronized (cache) {
            if(key == null) {
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
            if(key == null)
                return null;
            Bitmap bitmap = cache.get(key);
            Log.i(TAG, "Get bitmap " + key);
            // null if not found
            return bitmap;
        }
    }

    public static ImageCache getInstance() {
        if (instance == null) {
            synchronized (ImageCache.class) {
                if (instance == null)
                    instance = new ImageCache();
            }
        }
        return instance;
    }
}
