package com.comp30022.helium.strawberry.components.ar.helper;

import android.location.Location;
import android.opengl.Matrix;

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
    // index values for camera coordinates float[]{x,y,z,w}
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final int W = 3;
    // offset for conversion in camera to screen space
    private static final float OFFSET = .5f;

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

    /**
     * converting ENU to camera space, given a camera projection matrix
     * @param ENU ENU coordinates
     * @param projectionMatrix Camera ROTATED projection matrix
     * @return camera space coordinates {x, y, z, w}
     */
    public static float[] convertToCameraSpace(float[] ENU, float[] projectionMatrix) {
        float[] cameraSpace = new float[4];
        // multiply ENU (as a vector) with the projection matrix to get CameraSpace
        Matrix.multiplyMV(cameraSpace, 0, projectionMatrix, 0, ENU, 0);
        return cameraSpace;
    }

    /**
     * Converting from camera to screen space
     * @param cameraSpace Camera coordinates {x,y,z,w}
     * @param width camera width
     * @param height camera height
     * @return screen space {x,y}, origin at top left corner of android phone
     */
    public static float[] convertToScreenSpace(float[] cameraSpace, int width, int height) {
        float x = (OFFSET + cameraSpace[X] / cameraSpace[W]) * width;
        float y = (OFFSET - cameraSpace[Y] / cameraSpace[W]) * height;
        return new float[]{x, y};
    }

    /**
     * returns ENU location where currentLocation is considered the ORIGIN, and targetLocation
     * the "target"
     * @param targetLocation target locaion
     * @param currentLocation Origin, you.
     * @return ENU coordinates {north, east, up, 1}
     */
    public static float[] getENU(Location targetLocation, Location currentLocation) {
        float[] deviceLocationECEF = CoordinateConverter.GPS2ECEF(currentLocation);
        float[] targetECEF = CoordinateConverter.GPS2ECEF(targetLocation);
        return CoordinateConverter.ECEF2ENU(currentLocation,
                deviceLocationECEF,
                targetECEF);
    }

}
