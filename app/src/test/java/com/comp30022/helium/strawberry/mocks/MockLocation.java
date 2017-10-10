package com.comp30022.helium.strawberry.mocks;

import android.annotation.SuppressLint;
import android.location.Location;

/**
 * Because this is used in junit NON-INSTRUMENTED tests, we have to mock
 * location class otherwise the android provided Location class
 * returns 0 every time (even if you do .setLongitude(X), .setLatitude(X) ... and so on.
 *
 * This class provides the basic needs to get longitude, altitude and latitude. Feel free
 * to add more as needed. It also has a constructor that accepts them(long/lat/alt)
 * immediately for easy mocking.
 */
public class MockLocation extends Location {
    private double longitude;
    private double latitude;
    private double altitude;

    public MockLocation(String provider) {
        super(provider);
    }

    public MockLocation(Location l) {
        super(l);
        this.longitude = l.getLongitude();
        this.latitude = l.getLatitude();
        this.altitude = l.getAltitude();
    }

    public MockLocation(double longitude, double latitude, double altitude) {
        super("Mocked");
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public MockLocation(double longitude, double latitude) {
        this(longitude, latitude, 0);
    }


    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getAltitude() {
        return altitude;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Longitude: %f Latitude: %f Altitude: %f", this.longitude,
                this.latitude, this.altitude);
    }

}
