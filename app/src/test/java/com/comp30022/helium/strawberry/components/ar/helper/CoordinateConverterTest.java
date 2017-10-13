package com.comp30022.helium.strawberry.components.ar.helper;


import com.comp30022.helium.strawberry.mocks.MockLocation;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * To test the methods, we used an online converter to validate that our results are within
 * DELTA_ERROR accuracy.
 * <p>
 * The online converter can be found at:
 * http://www.apsalin.com/convert-geodetic-to-cartesian.aspx
 */
public class CoordinateConverterTest {
    // anything within .5f is accepted as "similar"
    private static final float DELTA_ERROR = 0.5f;

    /**
     * Tests this with Melbourne university's location
     *
     * @throws Exception
     */
    @Test
    public void GPS2ECEF() throws Exception {
        MockLocation location1 = new MockLocation(144.9611740, -37.7963690);
        float[] ECEF1 = CoordinateConverter.GPS2ECEF(location1);
        float[] answer = new float[]{-4131735.08504359f, 2897246.60208257f, -3887608.71357792f};
        assertArrayEquals(answer, ECEF1, DELTA_ERROR);
    }

    /**
     * Tests this with London's eye (with altitude)
     *
     * @throws Exception
     */
    @Test
    public void GPS2ECEF2() {
        MockLocation location2 = new MockLocation(-0.119607, 51.503474, 50);
        float[] ECEF2 = CoordinateConverter.GPS2ECEF(location2);
        float[] answer = new float[]{3978368.49f, -8304.99f, 4968642.18f};
        assertArrayEquals(answer, ECEF2, DELTA_ERROR);

    }

    /**
     * Tests this with London's eye (without altitude)
     *
     * @throws Exception
     */
    @Test
    public void GPS2ECEF3() {
        MockLocation location3 = new MockLocation(-0.119607, 51.503474);
        float[] ECEF3 = CoordinateConverter.GPS2ECEF(location3);
        float[] answer = new float[]{3978337.36919787f, -8304.93437674656f, 4968603.05607586f};
        assertArrayEquals(answer, ECEF3, DELTA_ERROR);
    }

    /**
     * The only edge case for this function is when 2 locations are at the exact same point
     *
     * @throws Exception
     */
    @Test
    public void ECEF2ENU() throws Exception {
        // trivial case: self's location to self's location should be 0
        MockLocation location1 = new MockLocation(-0.119607, 51.503474);
        MockLocation location2 = new MockLocation(location1);
        float[] ECEF1 = CoordinateConverter.GPS2ECEF(location1);
        float[] ECEF2 = CoordinateConverter.GPS2ECEF(location2);

        float[] ENUDifference = CoordinateConverter.ECEF2ENU(location1, ECEF1, ECEF2);
        float[] answer = new float[]{0, 0, 0, 1};
        assertArrayEquals(answer, ENUDifference, DELTA_ERROR);
    }

    @Test
    public void ECEF2ENU1() throws Exception {
        // london eye
        MockLocation location1 = new MockLocation(-0.119607, 51.503474);
        // melbourne uni
        MockLocation location2 = new MockLocation(144.9611740, -37.7963690);

        float[] ECEF1 = CoordinateConverter.GPS2ECEF(location1);
        float[] ECEF2 = CoordinateConverter.GPS2ECEF(location2);

        float[] ENUDifference = CoordinateConverter.ECEF2ENU(location1, ECEF1, ECEF2);
        // calculated manually.
        float[] answer = {2888615f, 839346.5f, -11983296f, 1f};

        assertArrayEquals(answer, ENUDifference, DELTA_ERROR);
    }

    @Test
    public void ECEF2ENU2() throws Exception {
        // white house
        MockLocation location1 = new MockLocation(-0.175567, 51.473404);
        // melb uni
        MockLocation location2 = new MockLocation(144.9611740, -37.7963690);

        float[] ECEF1 = CoordinateConverter.GPS2ECEF(location1);
        float[] ECEF2 = CoordinateConverter.GPS2ECEF(location2);

        float[] ENUDifference = CoordinateConverter.ECEF2ENU(location1, ECEF1, ECEF2);
        // calculated manually.
        float[] answer = new float[]{2884572.2f, 838609f, -11985492f, 1f};
        assertArrayEquals(answer, ENUDifference, DELTA_ERROR);
    }
}