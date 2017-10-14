package com.comp30022.helium.strawberry.helpers;


import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import com.comp30022.helium.strawberry.helpers.LocationHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by jjjjessie on 7/10/17.
 */
@RunWith(AndroidJUnit4.class)
public class LocationHelperTest {
    private Double EPSILON = 0.000001;

    private Location mockLocation;
    private LocationHelper mockLocationHelper;

    @Before
    public void setUp() throws Exception {
        mockLocationHelper = new LocationHelper();
        double lo, la;
        lo = 200.0;
        la = 20.0;
        mockLocation = new Location("MockLocation");
        mockLocation.setLatitude(la);
        mockLocation.setLongitude(lo);
    }

    @Test
    public void locationToString_isValid() throws Exception {
        String result;
        result = mockLocationHelper.locationToString(mockLocation);
        String ans = "20.0 200.0";
        assertEquals(ans, result);
    }

    @Test
    public void stringToLocation_isValid() throws Exception {
        String test = "20.0 200.0";
        Location result;
        result = mockLocationHelper.stringToLocation(test);
        boolean val = cmpLocation(result);
        assertEquals(true, val);

    }

    public boolean cmpLocation(Location result){
        Double latitude = result.getLatitude();
        Double longitude = result.getLongitude();

        if(!(latitude != null && Math.abs(latitude - mockLocation.getAltitude()) < EPSILON)) {
            return false;
        }
        if(!(longitude != null && Math.abs(longitude - mockLocation.getLongitude()) < EPSILON)) {
            return false;
        }

        return true;
    }

}
