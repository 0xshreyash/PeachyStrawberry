package com.comp30022.helium.strawberry.components.ar.helper;

import android.location.Location;

/**
 * This class is a convertion class for location -> ECEF (Earth centered earth focused)
 * -> ENU (east north up)
 * <p>
 * Adapted from
 * https://github.com/dat-ng/ar-location-based-android/blob/master/app/src/main/java/ng/dat/ar/helper/LocationHelper.java
 * <p>
 * and conversion formula obtained from:
 * http://digext6.defence.gov.au/dspace/bitstream/1947/3538/1/DSTO-TN-0432.pdf
 * <p>
 * <p>
 * The variables named here follows the variables named in the PDF file as the formula was
 * laid out
 * <p>
 * <p>
 * WE ASSUME A FLAT EARTH HERE
 **/
public class CoordinateConverter {
    // WGS 84 semi-major axis in meters(A)
    // value obtained from World Geodetic System 1984 (WGS 84) in
    // https://en.wikipedia.org/wiki/Geodetic_datum#Local_east.2C_north.2C_up_.28ENU.29_coordinates
    private final static double WGS84_A = 6378137.0;
    // WGS 84 eccentricity squared (E^2)
    private final static double WGS84_E2 = 0.00669437999014;

    /**
     * Converts GPS coordinates to ECEF (earth centered earth fixed) coordinates using the formula
     * as specified in
     * http://digext6.defence.gov.au/dspace/bitstream/1947/3538/1/DSTO-TN-0432.pdf
     * <p>
     * Look specifically at page 2, eq (1)
     *
     * @param location Location in GPS
     * @return ECEF coordinates
     */
    public static float[] GPS2ECEF(Location location) {
        double radiansLat = Math.toRadians(location.getLatitude());
        double radiansLong = Math.toRadians(location.getLongitude());

        float cosLat = (float) Math.cos(radiansLat);
        float sinLat = (float) Math.sin(radiansLat);
        float cosLong = (float) Math.cos(radiansLong);
        float sinLong = (float) Math.sin(radiansLong);

        float chi = (float) (WGS84_A / Math.sqrt(1.0 - WGS84_E2 * sinLat * sinLat));

        float x = (float) ((chi + location.getAltitude()) * cosLat * cosLong);
        float y = (float) ((chi + location.getAltitude()) * cosLat * sinLong);
        float z = (float) ((chi * (1.0 - WGS84_E2) + location.getAltitude()) * sinLat);

        return new float[]{x, y, z};

    }

    /**
     * Converts ECEF to ENU coordinates using the formula specified in:
     * http://digext6.defence.gov.au/dspace/bitstream/1947/3538/1/DSTO-TN-0432.pdf
     * <p>
     * Look specifically at page 3, eq(3)
     *
     * Here, you (location) are the origin, while targetECEF is the point where you point at
     *
     * @param location Your current location in GPS
     * @param ECEF ECEF converted coordinate of location
     * @param targetECEF Target ECEF
     * @return you as a origin towards targetECEF
     */
    public static float[] ECEF2ENU(Location location, float[] ECEF, float[] targetECEF) {
        double radiansLat = Math.toRadians(location.getLatitude());
        double radiansLon = Math.toRadians(location.getLongitude());

        float cosLat = (float) Math.cos(radiansLat);
        float sinLat = (float) Math.sin(radiansLat);
        float cosLong = (float) Math.cos(radiansLon);
        float sinLong = (float) Math.sin(radiansLon);

        float dx = targetECEF[0] - ECEF[0];
        float dy = targetECEF[1] - ECEF[1];
        float dz = targetECEF[2] - ECEF[2];

        float de = -sinLong * dx + cosLong * dy;
        float dn = -sinLat * cosLong * dx - sinLat * sinLong * dy + cosLat * dz;
        float du = cosLat * cosLong * dx + cosLat * sinLong * dy + sinLat * dz;

        return new float[]{de, dn, du, 1};
    }
}
