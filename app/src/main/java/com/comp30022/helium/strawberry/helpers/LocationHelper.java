package com.comp30022.helium.strawberry.helpers;

import android.location.Location;

/**
 * Created by noxm on 6/10/17.
 */

public class LocationHelper {
    private static final String GENERATED_BY_HELPER = "GeneratedByHelper";

    public static String locationToString(Location l) {
        return l.getLatitude() + " " + l.getLongitude();
    }

    public static Location stringToLocation(String s) {
        String str[] = s.split(" ");
        Location loc = new Location(GENERATED_BY_HELPER);
        loc.setLatitude(Double.parseDouble(str[0]));
        loc.setLongitude(Double.parseDouble(str[1]));
        return loc;
    }
}
