package com.comp30022.helium.strawberry.entities;


import android.location.Location;


public class Coordinate {
    private double x;
    private double y;
    private boolean isLatLong = false;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(double x, double y, boolean isLatLong) {
        this.x = x;
        this.y = y;
        this.isLatLong = isLatLong;
    }

    public Coordinate(Coordinate c) {
        this.x = c.x;
        this.y = c.y;
        this.isLatLong = c.isLatLong;
    }

    public Coordinate(Location location) {
        this.x = location.getLongitude();
        this.y = location.getLatitude();
    }

    public void setIsLatLong(boolean bool) {
        this.isLatLong = bool;
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
        this.isLatLong = other.isLatLong;
    }

    public Coordinate subtract(Coordinate other) {
        return new Coordinate(this.x-other.x, this.y-other.y, this.isLatLong);
    }

    public Coordinate normalize() {
        double distToOrigin = euclidDistance(new Coordinate(0, 0));
        double normalX = this.x / distToOrigin;
        double normalY = this.y / distToOrigin;
        return new Coordinate(normalX, normalY, this.isLatLong);
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

    /**
     * Converts current LogLat Coordinate system into a Cartesian Cartesian format
     */
    public void toLocalCart() {
        if (this.isLatLong) {
            this.isLatLong = false;
            double tmp = this.x;
            this.x = this.y;
            this.y = tmp;
        }
    }

    /**
     * returns a new Coordinate in Cartesian format (from LongLat Coordinate)
     * @return new Coordinate in cartesian from loglat coordinate
     */
    public Coordinate toCart() {
        if (this.isLatLong) {
            Coordinate coord = new Coordinate(this.y, this.x);
            coord.isLatLong = false;
            return coord;
        } else {
            // already in cartesian format
            return new Coordinate(this);
        }
    }

    public void toLocalLongLat() {
        if (!this.isLatLong) {
            this.isLatLong = true;
            double tmp = this.x;
            this.x = this.y;
            this.y = tmp;
        }
    }


    public Coordinate toLongLat() {
        if (!this.isLatLong) {
            Coordinate coord = new Coordinate(this.y, this.x);
            coord.isLatLong = true;
            return coord;
        } else {
            return new Coordinate(this);
        }
    }

    public Coordinate rotateDegree(double theta) {
        // make sure we're in cartesian space
        theta = Math.toRadians(theta);
        if (this.isLatLong) {
            this.toLocalCart();
            double newX = this.x * Math.cos(theta) - y * Math.sin(theta);
            double newY = this.x * Math.sin(theta) + y * Math.cos(theta);
            Coordinate res = new Coordinate(newX, newY, this.isLatLong);
            res.toLocalLongLat();
            return res;
        } else {
            double newX = this.x * Math.cos(theta) - y * Math.sin(theta);
            double newY = this.x * Math.sin(theta) + y * Math.cos(theta);
            return new Coordinate(newX, newY, this.isLatLong);
        }
    }
}
