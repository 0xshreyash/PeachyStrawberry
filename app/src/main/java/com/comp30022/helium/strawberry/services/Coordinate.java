package com.comp30022.helium.strawberry.services;


import com.jme3.math.Vector2f;

public class Coordinate {
    private final float x;
    private final float y;

    public Coordinate(double x, double y) {
        this.x = (float)x;
        this.y = (float)y;
    }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }


    public float getY() {
        return y;
    }

    public Vector2f getVector() {
        return new Vector2f(this.x, this.y);
    }
}
