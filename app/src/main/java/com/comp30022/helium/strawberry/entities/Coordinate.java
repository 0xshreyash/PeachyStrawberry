package com.comp30022.helium.strawberry.entities;


import android.location.Location;


/**
 * Difference between Coordinate and pre-built Vector2f is the fact that this class
 * is storing coordinates in doubles (the precision is important in bearings).
 */
public class Coordinate {
    private double x;
    private double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public Coordinate(Coordinate c) {
        this.x = c.x;
        this.y = c.y;
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

    public void update(Coordinate other) {
        this.x = other.x;
        this.y = other.y;
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



    public Coordinate rotateDegree(double theta) {
        // make sure we're in cartesian space
        theta = Math.toRadians(theta);
        double newX = this.x * Math.cos(theta) - y * Math.sin(theta);
        double newY = this.x * Math.sin(theta) + y * Math.cos(theta);
        return new Coordinate(newX, newY);
    }
}
