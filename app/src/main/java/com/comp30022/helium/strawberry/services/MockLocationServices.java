package com.comp30022.helium.strawberry.services;

import com.comp30022.helium.strawberry.entities.Coordinate;
import com.comp30022.helium.strawberry.entities.User;

public class MockLocationServices {
    private static MockLocationServices ourInstance = null;
    private static Coordinate coordinate;

    public static MockLocationServices getInstance() {
        if (ourInstance == null) {
            ourInstance = new MockLocationServices();
        }
        return ourInstance;
    }

    private MockLocationServices() {
        // taken from google maps - Union house
        coordinate = new Coordinate(144.960961, -37.796927);
    }


    /**
     * Returns a mock coordinate (Union house)
     * Original purpose should return the coordinate of User f
     * @param f User object - this is a dummy now, not used at all
     * @return Coordinate of union house
     */
    public static Coordinate getCoordinate(User f) {
        return coordinate;
    }
}
