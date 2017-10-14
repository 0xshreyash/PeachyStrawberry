package com.comp30022.helium.strawberry.entities.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.mocks.MockImageCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by jjjjessie on 14/10/17.
 */
public class ImageCacheTest {
    @Mock
    MockImageCache mockImageCache;

    @Mock
    Bitmap bitmap;

    /**
     * Setup befoere the tests are run
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mockImageCache = MockImageCache.getInstance();

        String imagePath;
        imagePath = "drawable://" + R.drawable.blue_circle;
        bitmap = BitmapFactory.decodeFile(imagePath);
    }

    /**
     * Test whether correct bitmap can be put inside the cache or not.
     */
    @Test
    public void put() throws Exception {
        mockImageCache.put("key1", bitmap);
        Field cacheField = MockImageCache.getInstance().getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);
        HashMap<String, Bitmap> cacheMap =
                (HashMap<String, Bitmap>) cacheField.get(MockImageCache.getInstance());
        Bitmap testBitmap = cacheMap.get("key1");
        assertEquals(bitmap, testBitmap);
    }

    /**
     * Test whether correct bitmap can be get from the cache or not.
     */
    @Test
    public void get() throws Exception {
        mockImageCache.put("key2", bitmap);
        Bitmap testBitmap = mockImageCache.get("key2");
        assertEquals(bitmap, testBitmap);
    }

}