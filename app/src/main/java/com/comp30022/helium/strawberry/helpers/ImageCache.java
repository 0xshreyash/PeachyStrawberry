package com.comp30022.helium.strawberry.helpers;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by noxm on 5/10/17.
 */

public class ImageCache {
    private final HashMap<String, Bitmap> map;
    private static ImageCache instance = null;

    private ImageCache() {
        map = new HashMap<>();
    }

    public void put(String key, Bitmap bitmap) {
        map.put(key, bitmap);
    }

    public Bitmap get(String key) {
        if(map.containsKey(key))
            return map.get(key);
        return null;
    }

    public static ImageCache getInstance() {
        if(instance == null)
            instance = new ImageCache();
        return instance;
    }
}
