package com.comp30022.helium.strawberry.helpers.Cache;

import android.util.Log;
import android.util.LruCache;

import com.comp30022.helium.strawberry.entities.User;

/**
 * Created by noxm on 7/10/17.
 */

public class UserCache {
    private static final String TAG = "UserCache";
    private static final int CACHE_SIZE = 4 * 1024 * 1024; // 4MiB

    private static volatile UserCache instance = null;
    private static LruCache<String, User> cache;

    private UserCache() {
        if (instance != null){
            throw new RuntimeException("Already instantiated!");
        }
        cache = new LruCache<>(CACHE_SIZE);
        Log.i(TAG, "Created");
    }

    public void put(String key, User user) {
        synchronized (cache) {
            if(cache.get(key) == null) {
                Log.i(TAG, "User " + user.getId() + " created and cached");
                cache.put(key, user);
            } else {
                Log.w(TAG, "User " + user.getId() + " already in cache");
            }
        }
    }

    public User get(String key) {
        synchronized (cache) {
            User user = cache.get(key);
            Log.i(TAG, "Get User " + user);
            // null if not found
            return user;
        }
    }

    public static UserCache getInstance() {
        if (instance == null) {
            synchronized (UserCache.class) {
                if(instance == null)
                    instance = new UserCache();
            }
        }
        return instance;
    }
}
