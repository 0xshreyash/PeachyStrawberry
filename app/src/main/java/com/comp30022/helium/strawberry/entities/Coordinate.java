package com.comp30022.helium.strawberry.entities;


import android.location.Location;


public class Coordinate {
    private final double x;
    private final double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(Location location) {
        this.x = location.getLongitude();
        this.y = location.getLatitude();
    }

    public double getX() {
        return x;
    }


    public double getY() {
        return y;
    }

    public Coordinate subtract(Coordinate other) {
        return new Coordinate(this.x-other.x, this.y-other.y);
    }

    public Coordinate normalize() {
        double distToOrigin = euclidDistance(new Coordinate(0, 0));
        double normalX = this.x / distToOrigin;
        double normalY = this.y / distToOrigin;
        return new Coordinate(normalX, normalY);
    }

    public double euclidDistance(Coordinate other) {
        double subX = (this.x-other.x);
        double subY = (this.y-other.y);
        return Math.sqrt(subX*subX + subY*subY);
    }

    public double dot(Coordinate other) {
        return this.x * other.x + this.y * other.y;
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
