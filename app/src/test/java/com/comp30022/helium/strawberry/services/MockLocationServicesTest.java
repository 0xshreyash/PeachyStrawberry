package com.comp30022.helium.strawberry.services;

import com.comp30022.helium.strawberry.entities.Coordinate;

import org.junit.Test;

/**
 * Created by noxm on 26/08/17.
 */
public class MockLocationServicesTest {
    @Test
    public void getInstance() throws Exception {
         if(MockLocationServices.getInstance() == null)
             throw new Exception("Unable to fetch singleton instance");
    }

    @Test
    public void getCoordinate() throws Exception {
        MockLocationServices mockLocationServices = MockLocationServices.getInstance();
        Coordinate coord = mockLocationServices.getCoordinate(null);

        if(coord == null)
            throw new Exception("Unable to fetch mock coordinate");
    }
}