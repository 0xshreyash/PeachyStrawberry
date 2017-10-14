package com.comp30022.helium.strawberry.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import com.comp30022.helium.strawberry.R;
import com.comp30022.helium.strawberry.StrawberryApplication;
import com.comp30022.helium.strawberry.helpers.ColourScheme;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by noxm on 10/10/17.
 */

@RunWith(AndroidJUnit4.class)
public class ColourSchemeTest {
    private static Resources res;
    private static Context context;

    @BeforeClass
    public static void setupResource() {
        res = StrawberryApplication.getInstance().getResources();
        context = StrawberryApplication.getInstance().getApplicationContext();
    }
    @Test
    public void colours_allOpaque() {
        assertTrue(Color.alpha(ColourScheme.PRIMARY) == 255);
        assertTrue(Color.alpha(ColourScheme.PRIMARY_DARK) == 255);
        assertTrue(Color.alpha(ColourScheme.PRIMARY_LIGHT) == 255);
        assertTrue(Color.alpha(ColourScheme.SECONDARY_DARK) == 255);
        assertTrue(Color.alpha(ColourScheme.SECONDARY) == 255);
        assertTrue(Color.alpha(ColourScheme.ACCENT) == 255);
    }

    @Test
    public void colours_consistent() {
        assertEquals(ColourScheme.PRIMARY, ContextCompat.getColor(context, R.color.colorPrimary));
        assertEquals(ColourScheme.PRIMARY_DARK, ContextCompat.getColor(context, R.color.colorPrimaryDark));
        assertEquals(ColourScheme.PRIMARY_LIGHT, ContextCompat.getColor(context, R.color.colorPrimaryLight));
        assertEquals(ColourScheme.SECONDARY, ContextCompat.getColor(context, R.color.colorSecondary));
        assertEquals(ColourScheme.SECONDARY_DARK, ContextCompat.getColor(context, R.color.colorSecondaryDark));
        assertEquals(ColourScheme.ACCENT, ContextCompat.getColor(context, R.color.colorAccent));
    }


}
